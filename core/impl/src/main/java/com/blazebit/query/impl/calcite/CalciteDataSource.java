/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRel;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.avatica.AvaticaConnection;
import org.apache.calcite.avatica.DriverVersion;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.interpreter.BindableConvention;
import org.apache.calcite.jdbc.CalciteFactory;
import org.apache.calcite.jdbc.CalcitePrepare;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.Driver;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.prepare.CalcitePrepareImpl;
import org.apache.calcite.prepare.Prepare;
import org.apache.calcite.rel.type.DelegatingTypeSystem;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlTimeLiteral;
import org.apache.calcite.sql.SqlTimestampTzLiteral;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorScope;
import org.apache.calcite.sql2rel.SqlRexConvertletTable;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.util.TimeString;
import org.apache.calcite.util.TimestampWithTimeZoneString;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

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
		this.typeFactory = new CustomJavaTypeFactory( typeSystem );
		this.rootSchema = CalciteSchema.createRootSchema( true );
	}

	public SchemaPlus getRootSchema() {
		return rootSchema.plus();
	}

	@Override
	public CalcitePrepare createPrepare() {
		return new MyCalcitePrepareImpl();
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
	public PrintWriter getLogWriter() {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) {
	}

	@Override
	public void setLoginTimeout(int seconds) {
	}

	@Override
	public int getLoginTimeout() {
		return 0;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) {
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
				return switch ( literal.getTypeName() ) {
					case TIMESTAMP_TZ -> rexBuilder.makeTimestampLiteral(
							literal.getValueAs( TimestampWithTimeZoneString.class ).getLocalTimestampString(),
							((SqlTimestampTzLiteral) literal).getPrec()
					);
					case TIME_TZ -> rexBuilder.makeTimeLiteral(
							literal.getValueAs( TimeString.class ),
							((SqlTimeLiteral) literal).getPrec() );
					default -> super.convertLiteral( literal );
				};
			}
		}
	}
}
