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

import com.blazebit.query.calcite.CalciteDataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.avatica.AvaticaConnection;
import org.apache.calcite.avatica.DriverVersion;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.jdbc.CalciteFactory;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.Driver;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.DelegatingTypeSystem;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.schema.SchemaPlus;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class CalciteDataSourceImpl extends Driver implements CalciteDataSource {
    private final Properties properties;
    private final JavaTypeFactory typeFactory;
    private final CalciteSchema rootSchema;

    public CalciteDataSourceImpl(CalciteDataSourceBuilderImpl builder) {
        this.properties = builder.properties;
        CalciteConnectionConfig cfg = new CalciteConnectionConfigImpl( builder.properties );
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

    @Override
    public SchemaPlus getRootSchema() {
        return rootSchema.plus();
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
        } catch (Exception e) {
            throw new SQLException( "Can't unwrap to " + iface.getTypeName(), e );
        }
    }
}
