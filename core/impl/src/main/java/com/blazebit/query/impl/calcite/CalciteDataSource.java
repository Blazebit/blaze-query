/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.query.impl.calcite;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.avatica.AvaticaConnection;
import org.apache.calcite.avatica.DriverVersion;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.jdbc.CalciteFactory;
import org.apache.calcite.jdbc.CalcitePrepare;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.Driver;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.plan.Contexts;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.prepare.CalciteSqlValidator;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.type.DelegatingTypeSystem;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserImplFactory;
import org.apache.calcite.sql.util.SqlOperatorTables;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.sql2rel.StandardConvertletTable;

import static org.apache.calcite.rel.rel2sql.SqlImplementor.POS;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class CalciteDataSource extends Driver implements DataSource {
	private final Properties properties;
	private final JavaTypeFactory typeFactory;
	private final CalciteSchema rootSchema;

	public CalciteDataSource() {
		properties = new Properties();
		properties.setProperty( "lex", "JAVA" );

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

	public static RelRoot parseQueryToRel(CalciteConnection connection, String sql) {
		final CalcitePrepare.Context prepareContext = connection.createPrepareContext();

		// SQL Parsing
		final CalciteConnectionConfig config = prepareContext.config();
		SqlParser.Config parserConfig = SqlParser.config()
				.withQuotedCasing(config.quotedCasing())
				.withUnquotedCasing(config.unquotedCasing())
				.withQuoting(config.quoting())
				.withConformance(config.conformance())
				.withCaseSensitive(config.caseSensitive());
		final SqlParserImplFactory parserFactory = config.parserFactory( SqlParserImplFactory.class, null);
		if (parserFactory != null) {
			parserConfig = parserConfig.withParserFactory(parserFactory);
		}
		SqlParser parser = SqlParser.create(sql, parserConfig);
		SqlNode sqlNode;
		try {
			sqlNode = parser.parseStmt();
		} catch (SqlParseException e) {
			throw new RuntimeException(
					"parse failed: " + e.getMessage(), e);
		}
		if ( !isSelect( sqlNode.getKind()) ) {
			throw new RuntimeException("Unsupported sql: " + sql);
		}

		// Convert the SQL AST to a RelNode
		final JavaTypeFactory typeFactory = prepareContext.getTypeFactory();
		final CalciteCatalogReader catalogReader = new CalciteCatalogReader(
				prepareContext.getRootSchema(),
				prepareContext.getDefaultSchemaPath(),
				typeFactory,
				prepareContext.config()
		);
		final SqlValidator validator = createSqlValidator( prepareContext, catalogReader);

		final SqlToRelConverter.Config sqlToRelConfig =
				SqlToRelConverter.config()
						.withTrimUnusedFields(true)
						.withExpand(false)
						.withInSubQueryThreshold(20)
						.withExplain(false);

		final VolcanoPlanner planner = new VolcanoPlanner( null, Contexts.of( prepareContext.config()));
		SqlToRelConverter sqlToRelConverter = new SqlToRelConverter(
				(rowType, queryString, schemaPath, viewPath) -> null,
				validator,
				catalogReader,
				RelOptCluster.create( planner, new RexBuilder( typeFactory)),
				StandardConvertletTable.INSTANCE,
				sqlToRelConfig
		);
		return sqlToRelConverter.convertQuery(sqlNode, true, true);
	}

	public static RelRoot parsePredicateToRel(CalciteConnection connection, String sql) {
		final CalcitePrepare.Context prepareContext = connection.createPrepareContext();

		// SQL Parsing
		final CalciteConnectionConfig config = prepareContext.config();
		SqlParser.Config parserConfig = SqlParser.config()
				.withQuotedCasing(config.quotedCasing())
				.withUnquotedCasing(config.unquotedCasing())
				.withQuoting(config.quoting())
				.withConformance(config.conformance())
				.withCaseSensitive(config.caseSensitive());
		final SqlParserImplFactory parserFactory = config.parserFactory(SqlParserImplFactory.class, null);
		if (parserFactory != null) {
			parserConfig = parserConfig.withParserFactory(parserFactory);
		}
		SqlParser parser = SqlParser.create(sql, parserConfig);
		SqlNode sqlNode;
		try {
			sqlNode = parser.parseExpression();
		} catch (SqlParseException e) {
			throw new RuntimeException(
					"parse failed: " + e.getMessage(), e);
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
		final SqlValidator validator = createSqlValidator( prepareContext, catalogReader);

		final SqlToRelConverter.Config sqlToRelConfig =
				SqlToRelConverter.config()
						.withTrimUnusedFields(true)
						.withExpand(false)
						.withInSubQueryThreshold(20)
						.withExplain(false);

		final VolcanoPlanner planner = new VolcanoPlanner( null, Contexts.of( prepareContext.config()));
		SqlToRelConverter sqlToRelConverter = new SqlToRelConverter(
				(rowType, queryString, schemaPath, viewPath) -> null,
				validator,
				catalogReader,
				RelOptCluster.create( planner, new RexBuilder( typeFactory)),
				StandardConvertletTable.INSTANCE,
				sqlToRelConfig
		);
		return sqlToRelConverter.convertQuery(sqlNode, true, true);
	}

	private static SqlValidator createSqlValidator(
			CalcitePrepare.Context context,
			CalciteCatalogReader catalogReader) {
		final SqlOperatorTable opTab0 =
				context.config().fun( SqlOperatorTable.class,
									  SqlStdOperatorTable.instance());
		final List<SqlOperatorTable> list = new ArrayList<>();
		list.add(opTab0);
		list.add(catalogReader);
		final SqlOperatorTable opTab = SqlOperatorTables.chain( list);
		final JavaTypeFactory typeFactory = context.getTypeFactory();
		final CalciteConnectionConfig connectionConfig = context.config();
		final SqlValidator.Config config = SqlValidator.Config.DEFAULT
				.withLenientOperatorLookup(connectionConfig.lenientOperatorLookup())
				.withConformance(connectionConfig.conformance())
				.withDefaultNullCollation(connectionConfig.defaultNullCollation())
				.withIdentifierExpansion(true);
		return new CalciteSqlValidator( opTab, catalogReader, typeFactory,
										config);
	}

	private static boolean isSelect(SqlKind kind) {
		switch (kind) {
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
		AvaticaConnection connection = ( (CalciteFactory) factory ).newConnection(
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
}
