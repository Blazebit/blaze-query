/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite;

import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRel;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.avatica.AvaticaConnection;
import org.apache.calcite.avatica.DriverVersion;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.interpreter.BindableConvention;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.jdbc.CalciteFactory;
import org.apache.calcite.jdbc.CalcitePrepare;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.Driver;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.plan.Contexts;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.prepare.CalcitePrepareImpl;
import org.apache.calcite.prepare.CalciteSqlValidator;
import org.apache.calcite.prepare.Prepare;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.type.DelegatingTypeSystem;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlTimeLiteral;
import org.apache.calcite.sql.SqlTimestampTzLiteral;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserImplFactory;
import org.apache.calcite.sql.util.SqlOperatorTables;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorScope;
import org.apache.calcite.sql.validate.implicit.TypeCoercionImpl;
import org.apache.calcite.sql2rel.SqlRexConvertletTable;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.sql2rel.StandardConvertletTable;
import org.apache.calcite.util.TimeString;
import org.apache.calcite.util.TimestampWithTimeZoneString;
import org.checkerframework.checker.nullness.qual.Nullable;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.POS;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class CalciteDataSource extends Driver implements DataSource {
	private final Properties properties;
	private final JavaTypeFactory typeFactory;
	private final CalciteSchema rootSchema;

	public CalciteDataSource(Properties properties) {
		properties.setProperty( "lex", "JAVA" );
		this.properties = properties;

		CalciteConnectionConfig cfg = new CalciteConnectionConfigImpl( properties );
		RelDataTypeSystem typeSystem = cfg.typeSystem( RelDataTypeSystem.class, RelDataTypeSystem.DEFAULT );
		if ( cfg.conformance().shouldConvertRaggedUnionTypesToVarying() ) {
			typeSystem = new DelegatingTypeSystem( typeSystem ) {
				@Override
				public boolean shouldConvertRaggedUnionTypesToVarying() {
					return true;
				}
			};
		}
		this.typeFactory = new JavaTypeFactoryImpl( typeSystem );
		this.rootSchema = CalciteSchema.createRootSchema( true );
	}

	public SchemaPlus getRootSchema() {
		return rootSchema.plus();
	}

	@Override
	public CalcitePrepare createPrepare() {
		return new MyCalcitePrepareImpl();
	}

	public static RelRoot parseQueryToRel(CalciteConnection connection, String sql) {
		final CalcitePrepare.Context prepareContext = connection.createPrepareContext();

		// SQL Parsing
		final CalciteConnectionConfig config = prepareContext.config();
		SqlParser.Config parserConfig = SqlParser.config()
				.withQuotedCasing( config.quotedCasing() )
				.withUnquotedCasing( config.unquotedCasing() )
				.withQuoting( config.quoting() )
				.withConformance( config.conformance() )
				.withCaseSensitive( config.caseSensitive() );
		final SqlParserImplFactory parserFactory = config.parserFactory( SqlParserImplFactory.class, null );
		if ( parserFactory != null ) {
			parserConfig = parserConfig.withParserFactory( parserFactory );
		}
		SqlParser parser = SqlParser.create( sql, parserConfig );
		SqlNode sqlNode;
		try {
			sqlNode = parser.parseStmt();
		}
		catch (SqlParseException e) {
			throw new RuntimeException(
					"parse failed: " + e.getMessage(), e );
		}
		if ( !isSelect( sqlNode.getKind() ) ) {
			throw new RuntimeException( "Unsupported sql: " + sql );
		}

		// Convert the SQL AST to a RelNode
		final JavaTypeFactory typeFactory = prepareContext.getTypeFactory();
		final CalciteCatalogReader catalogReader = new CalciteCatalogReader(
				prepareContext.getRootSchema(),
				prepareContext.getDefaultSchemaPath(),
				typeFactory,
				prepareContext.config()
		);
		final SqlValidator validator = createSqlValidator( prepareContext, catalogReader );

		final SqlToRelConverter.Config sqlToRelConfig =
				SqlToRelConverter.config()
						.withTrimUnusedFields( true )
						.withExpand( false )
						.withInSubQueryThreshold( 20 )
						.withExplain( false );

		final VolcanoPlanner planner = new VolcanoPlanner( null, Contexts.of( prepareContext.config() ) );
		RelOptTable.ViewExpander viewExpander = (rowType, queryString, schemaPath, viewPath) -> null;
		SqlToRelConverter sqlToRelConverter = new SqlToRelConverter(
				viewExpander,
				validator,
				catalogReader,
				RelOptCluster.create( planner, new RexBuilder( typeFactory ) ),
				StandardConvertletTable.INSTANCE,
				sqlToRelConfig
		);
		return sqlToRelConverter.convertQuery( sqlNode, true, true );
	}

	public static RelRoot parsePredicateToRel(CalciteConnection connection, String sql) {
		final CalcitePrepare.Context prepareContext = connection.createPrepareContext();

		// SQL Parsing
		final CalciteConnectionConfig config = prepareContext.config();
		SqlParser.Config parserConfig = SqlParser.config()
				.withQuotedCasing( config.quotedCasing() )
				.withUnquotedCasing( config.unquotedCasing() )
				.withQuoting( config.quoting() )
				.withConformance( config.conformance() )
				.withCaseSensitive( config.caseSensitive() );
		final SqlParserImplFactory parserFactory = config.parserFactory( SqlParserImplFactory.class, null );
		if ( parserFactory != null ) {
			parserConfig = parserConfig.withParserFactory( parserFactory );
		}
		SqlParser parser = SqlParser.create( sql, parserConfig );
		SqlNode sqlNode;
		try {
			sqlNode = parser.parseExpression();
		}
		catch (SqlParseException e) {
			throw new RuntimeException(
					"parse failed: " + e.getMessage(), e );
		}

		SqlIdentifier e = new SqlIdentifier( ImmutableList.of( "azure", "vm", "VirtualMachine" ), POS );
		SqlCall fromPart = SqlStdOperatorTable.AS.createCall( POS, e, new SqlIdentifier( "vm", POS ) );
		sqlNode = new SqlSelect(
				POS,
				null,
				new SqlNodeList( ImmutableList.of( SqlLiteral.createExactNumeric( "1", POS ) ), POS ),
				fromPart,
				sqlNode,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null
		);

		// Convert the SQL AST to a RelNode
		final JavaTypeFactory typeFactory = prepareContext.getTypeFactory();
		final CalciteCatalogReader catalogReader = new CalciteCatalogReader(
				prepareContext.getRootSchema(),
				prepareContext.getDefaultSchemaPath(),
				typeFactory,
				prepareContext.config()
		);
		final SqlValidator validator = createSqlValidator( prepareContext, catalogReader );

		final SqlToRelConverter.Config sqlToRelConfig =
				SqlToRelConverter.config()
						.withTrimUnusedFields( true )
						.withExpand( false )
						.withInSubQueryThreshold( 20 )
						.withExplain( false );

		final VolcanoPlanner planner = new VolcanoPlanner( null, Contexts.of( prepareContext.config() ) );
		RelOptTable.ViewExpander viewExpander = (rowType, queryString, schemaPath, viewPath) -> null;
		SqlToRelConverter sqlToRelConverter = new SqlToRelConverter(
				viewExpander,
				validator,
				catalogReader,
				RelOptCluster.create( planner, new RexBuilder( typeFactory ) ),
				StandardConvertletTable.INSTANCE,
				sqlToRelConfig
		);
		return sqlToRelConverter.convertQuery( sqlNode, true, true );
	}

	private static SqlValidator createSqlValidator(
			CalcitePrepare.Context context,
			CalciteCatalogReader catalogReader) {
		final SqlOperatorTable opTab0 = context.config().fun( SqlOperatorTable.class, SqlStdOperatorTable.instance() );
		final List<SqlOperatorTable> list = new ArrayList<>();
		list.add( opTab0 );
		list.add( catalogReader );
		final SqlOperatorTable opTab = SqlOperatorTables.chain( list );
		final JavaTypeFactory typeFactory = context.getTypeFactory();
		final CalciteConnectionConfig connectionConfig = context.config();
		final SqlValidator.Config config = SqlValidator.Config.DEFAULT
				.withLenientOperatorLookup( connectionConfig.lenientOperatorLookup() )
				.withConformance( connectionConfig.conformance() )
				.withDefaultNullCollation( connectionConfig.defaultNullCollation() )
				.withIdentifierExpansion( true )
				.withTypeCoercionFactory( MyTypeCoercionImpl::new );
		return new CalciteSqlValidator( opTab, catalogReader, typeFactory, config );
	}

	private static boolean isSelect(SqlKind kind) {
		switch ( kind ) {
			case INSERT:
			case DELETE:
			case UPDATE:
			case MERGE:
				return false;
			default:
				return true;
		}
	}

	@Override
	protected DriverVersion createDriverVersion() {
		return new DriverVersion(
				"Blaze-Query Calcite driver",
				"1.0.0-SNAPSHOT",
				"Blaze-Query",
				"1.0.0-SNAPSHOT",
				true,
				4,
				2,
				1,
				0
		);
	}

	@Override
	protected String getConnectStringPrefix() {
		return "";
	}

	@Override
	public Connection getConnection() throws SQLException {
		AvaticaConnection connection = ((CalciteFactory) factory).newConnection(
				this,
				factory,
				"jdbc:calcite:",
				properties,
				rootSchema,
				typeFactory
		);
		handler.onConnectionInit( connection );
		return connection;
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return getConnection();
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return iface != null && iface.isAssignableFrom( getClass() );
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		try {
			if ( isWrapperFor( iface ) ) {
				return (T) this;
			}
			throw new SQLException( "Can't unwrap to " + iface.getTypeName() );
		}
		catch (Exception e) {
			throw new SQLException( "Can't unwrap to " + iface.getTypeName(), e );
		}
	}

	private static class MyTypeCoercionImpl extends TypeCoercionImpl {
		public MyTypeCoercionImpl(RelDataTypeFactory typeFactory, SqlValidator validator) {
			super( typeFactory, validator );
		}

//        @Override
//        public boolean binaryComparisonCoercion(SqlCallBinding binding) {
//            SqlOperator operator = binding.getOperator();
//            SqlKind kind = operator.getKind();
//            int operandCnt = binding.getOperandCount();
//            boolean coerced = false;
//            // Binary operator
//            if (operandCnt == 2) {
//                final RelDataType type1 = binding.getOperandType( 0);
//                final RelDataType type2 = binding.getOperandType(1);
//                // EQUALS(=) NOT_EQUALS(<>)
//                if (kind.belongsTo(SqlKind.BINARY_EQUALITY)) {
//                    // INSTANT and datetime
//                    coerced = dateTimeInstantEquality(binding, type1, type2) || coerced;
//                }
//                // Binary comparison operator like: = > >= < <=
//                if (kind.belongsTo(SqlKind.BINARY_COMPARISON)) {
//                    final RelDataType commonType = commonTypeForBinaryComparison(type1, type2);
//                    if (null != commonType) {
//                        coerced = coerceOperandsType(binding.getScope(), binding.getCall(), commonType);
//                    }
//                }
//            }
//            return coerced || super.binaryComparisonCoercion( binding );
//        }
//
//        protected boolean dateTimeInstantEquality(
//                SqlCallBinding binding,
//                RelDataType left,
//                RelDataType right) {
//            // REVIEW Danny 2018-05-23 we do not need to coerce type for EQUALS
//            // because SqlToRelConverter already does this.
//            // REVIEW Danny 2019-09-23, we should unify the coercion rules in TypeCoercion
//            // instead of SqlToRelConverter.
//            if ( isJavaType(left, Instant.class)
//                    && SqlTypeUtil.isDatetime(right)) {
//                return coerceOperandType(binding.getScope(), binding.getCall(), 0, right);
//            }
//            if (isJavaType(right, Instant.class)
//                    && SqlTypeUtil.isDatetime(left)) {
//                return coerceOperandType(binding.getScope(), binding.getCall(), 1, left);
//            }
//            return false;
//        }
//
//        private static boolean isJavaType(RelDataType left, Class<?> javaType) {
//            return left instanceof RelDataTypeFactoryImpl.JavaType && ( (RelDataTypeFactoryImpl.JavaType) left ).getJavaClass() == javaType;
//        }
//
//        @Override
//        public @Nullable RelDataType commonTypeForBinaryComparison(
//                @Nullable RelDataType type1, @Nullable RelDataType type2) {
//            if (type1 == null || type2 == null) {
//                return null;
//            }
//
//            SqlTypeName typeName1 = type1.getSqlTypeName();
//            SqlTypeName typeName2 = type2.getSqlTypeName();
//
//            if (typeName1 == null || typeName2 == null) {
//                return null;
//            }
//
//            // DATETIME + CHARACTER -> DATETIME
//            // REVIEW Danny 2019-09-23: There is some legacy redundant code in SqlToRelConverter
//            // that coerce Datetime and CHARACTER comparison.
//            if (SqlTypeUtil.isCharacter(type1) && SqlTypeUtil.isDatetime(type2)) {
//                return type2;
//            }
//
//            if (SqlTypeUtil.isDatetime(type1) && SqlTypeUtil.isCharacter(type2)) {
//                return type1;
//            }
//
//            // DATE + TIMESTAMP -> TIMESTAMP
//            if (SqlTypeUtil.isDate(type1) && SqlTypeUtil.isTimestamp(type2)) {
//                return type2;
//            }
//
//            if (SqlTypeUtil.isDate(type2) && SqlTypeUtil.isTimestamp(type1)) {
//                return type1;
//            }
//
//            if (SqlTypeUtil.isString(type1) && typeName2 == SqlTypeName.NULL) {
//                return type1;
//            }
//
//            if (typeName1 == SqlTypeName.NULL && SqlTypeUtil.isString(type2)) {
//                return type2;
//            }
//
//            if (SqlTypeUtil.isDecimal(type1) && SqlTypeUtil.isCharacter(type2)
//                    || SqlTypeUtil.isCharacter(type1) && SqlTypeUtil.isDecimal(type2)) {
//                // There is no proper DECIMAL type for VARCHAR, using max precision/scale DECIMAL
//                // as the best we can do.
//                return SqlTypeUtil.getMaxPrecisionScaleDecimal(factory);
//            }
//
//            // Keep sync with MS-SQL:
//            // 1. BINARY/VARBINARY can not cast to FLOAT/REAL/DOUBLE
//            // because of precision loss,
//            // 2. CHARACTER to TIMESTAMP need explicit cast because of TimeZone.
//            // Hive:
//            // 1. BINARY can not cast to any other types,
//            // 2. CHARACTER can only be coerced to DOUBLE/DECIMAL.
//            if (SqlTypeUtil.isBinary(type2) && SqlTypeUtil.isApproximateNumeric(type1)
//                    || SqlTypeUtil.isBinary(type1) && SqlTypeUtil.isApproximateNumeric(type2)) {
//                return null;
//            }
//
//            // 1 > '1' will be coerced to 1 > 1.
//            if (SqlTypeUtil.isAtomic(type1) && SqlTypeUtil.isCharacter(type2)) {
//                if (SqlTypeUtil.isTimestamp(type1)) {
//                    return null;
//                }
//                return type1;
//            }
//
//            if (SqlTypeUtil.isCharacter(type1) && SqlTypeUtil.isAtomic(type2)) {
//                if (SqlTypeUtil.isTimestamp(type2)) {
//                    return null;
//                }
//                return type2;
//            }
//
//            if (validator.config().conformance().allowLenientCoercion()) {
//                if (SqlTypeUtil.isString(type1) && SqlTypeUtil.isArray(type2)) {
//                    return type2;
//                }
//
//                if (SqlTypeUtil.isString(type2) && SqlTypeUtil.isArray(type1)) {
//                    return type1;
//                }
//            }
//
//            return null;
//        }
//
//        @Override
//        protected boolean coerceOperandType(
//                @Nullable SqlValidatorScope scope,
//                SqlCall call,
//                int index,
//                RelDataType targetType) {
//            // Transform the JavaType to SQL type because the SqlDataTypeSpec
//            // does not support deriving JavaType yet.
//            if ( RelDataTypeFactoryImpl.isJavaType( targetType)) {
//                targetType = ((JavaTypeFactory) factory).toSql(targetType);
//            }
//
//            SqlNode operand = call.getOperandList().get(index);
//            if (operand instanceof SqlDynamicParam ) {
//                // Do not support implicit type coercion for dynamic param.
//                return false;
//            }
//            requireNonNull(scope, "scope");
//            RelDataType operandType = validator.deriveType(scope, operand);
//            if (coerceStringToArray(call, operand, index, operandType, targetType)) {
//                return true;
//            }
//
//            // Check it early.
//            if (!needToCast( scope, operand, targetType, SqlTypeCoercionRule.lenientInstance())) {
//                return false;
//            }
//            // Fix up nullable attr.
//            RelDataType targetType1 = syncAttributes(operandType, targetType);
//            SqlNode desired = castTo(operand, targetType1);
//            call.setOperand(index, desired);
//            updateInferredType(desired, targetType1);
//            return true;
//        }
//
//        RelDataType syncAttributes(
//                RelDataType fromType,
//                RelDataType toType) {
//            RelDataType syncedType = toType;
//            if (fromType != null) {
//                syncedType = factory.createTypeWithNullability(syncedType, fromType.isNullable());
//                if (SqlTypeUtil.inCharOrBinaryFamilies(fromType)
//                        && SqlTypeUtil.inCharOrBinaryFamilies(toType)) {
//                    Charset charset = fromType.getCharset();
//                    if (charset != null && SqlTypeUtil.inCharFamily(syncedType)) {
//                        SqlCollation collation = getCollation( fromType);
//                        syncedType =
//                                factory.createTypeWithCharsetAndCollation(syncedType, charset,
//                                                                          collation);
//                    }
//                }
//            }
//            return syncedType;
//        }
//        private static SqlNode castTo(SqlNode node, RelDataType type) {
//            return SqlStdOperatorTable.CAST.createCall( SqlParserPos.ZERO, node,
//                                                        SqlTypeUtil.convertTypeToSpec(type).withNullable(type.isNullable()));
//        }
	}

	private static class MyCalcitePrepareImpl extends CalcitePrepareImpl {
		@Override
		protected CalcitePreparingStmt getPreparingStmt(Context context, Type elementType, CalciteCatalogReader catalogReader, RelOptPlanner planner) {
			final JavaTypeFactory typeFactory = context.getTypeFactory();
			final EnumerableRel.Prefer prefer;
			if ( elementType == Object[].class ) {
				prefer = EnumerableRel.Prefer.ARRAY;
			}
			else {
				prefer = EnumerableRel.Prefer.CUSTOM;
			}
			final Convention resultConvention =
					enableBindable ? BindableConvention.INSTANCE : EnumerableConvention.INSTANCE;
			return new MyCalcitePreparingStmt(
					this,
					context,
					catalogReader,
					typeFactory,
					context.getRootSchema(),
					prefer,
					createCluster( planner, new RexBuilder( typeFactory ) ),
					resultConvention,
					createConvertletTable()
			);
		}
	}

	private static class MyCalcitePreparingStmt extends CalcitePrepareImpl.CalcitePreparingStmt {

		private final RelOptCluster cluster;

		public MyCalcitePreparingStmt(
				CalcitePrepareImpl prepare,
				CalcitePrepare.Context context,
				CatalogReader catalogReader,
				RelDataTypeFactory typeFactory,
				CalciteSchema schema,
				EnumerableRel.@Nullable Prefer prefer,
				RelOptCluster cluster,
				Convention resultConvention,
				SqlRexConvertletTable convertletTable) {
			super(
					prepare,
					context,
					catalogReader,
					typeFactory,
					schema,
					prefer,
					cluster,
					resultConvention,
					convertletTable
			);
			this.cluster = cluster;
		}

		@Override
		protected SqlToRelConverter getSqlToRelConverter(
				SqlValidator validator,
				CatalogReader catalogReader,
				SqlToRelConverter.Config config) {
			return new MySqlToRelConverter( this, validator, catalogReader, cluster, convertletTable, config );
		}

		@Override
		protected SqlValidator createSqlValidator(CatalogReader catalogReader) {
			return CalciteDataSource.createSqlValidator( context, (CalciteCatalogReader) catalogReader );
		}
	}

	private static class MySqlToRelConverter extends SqlToRelConverter {
		public MySqlToRelConverter(
				RelOptTable.ViewExpander viewExpander,
				@Nullable SqlValidator validator,
				Prepare.CatalogReader catalogReader,
				RelOptCluster cluster,
				SqlRexConvertletTable convertletTable,
				Config config) {
			super( viewExpander, validator, catalogReader, cluster, convertletTable, config );
		}

		@Override
		protected Blackboard createBlackboard(
				SqlValidatorScope scope,
				@Nullable Map<String, RexNode> nameToNodeMap, boolean top) {
			return new MyBlackboard( scope, nameToNodeMap, top );
		}

		private class MyBlackboard extends Blackboard {
			public MyBlackboard(
					@Nullable SqlValidatorScope scope,
					@Nullable Map<String, RexNode> nameToNodeMap,
					boolean top) {
				super( scope, nameToNodeMap, top );
			}

			@Override
			public RexNode convertLiteral(SqlLiteral literal) {
				return visit( literal );
			}

			@Override
			public RexNode visit(SqlLiteral literal) {
				switch ( literal.getTypeName() ) {
					case TIMESTAMP_TZ:
						return rexBuilder.makeTimestampLiteral(
								literal.getValueAs( TimestampWithTimeZoneString.class ).getLocalTimestampString(),
								((SqlTimestampTzLiteral) literal).getPrec()
						);
//                        return rexBuilder.makeTimestampTzLiteral(
//                                literal.getValueAs(TimestampWithTimeZoneString.class),
//                                ((SqlTimestampTzLiteral) literal).getPrec()
//                        );
					case TIME_TZ:
						return rexBuilder.makeTimeLiteral(
								literal.getValueAs( TimeString.class ),
								((SqlTimeLiteral) literal).getPrec() );
//                        return rexBuilder.makeTimeTzLiteral(
//                                literal.getValueAs( TimeWithTimeZoneString.class),
//                                ((SqlTimeTzLiteral) literal).getPrec());
					default:
						return super.convertLiteral( literal );
				}
			}
		}
	}
}
