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
package com.blazebit.query.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.apache.calcite.avatica.remote.Driver;

public class Main {

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        try (Connection connection = new Driver().connect(
                "jdbc:avatica:remote:url=http://localhost:8765;serialization=protobuf",
                new Properties()
        )) {
            try ( ResultSet rs = connection.getMetaData().getColumns( null, "AZURE", "VIRTUAL_MACHINE", "OSPROFILE" ) ) {
                final int columnCount = rs.getMetaData().getColumnCount();
                while ( rs.next() ) {
                    for ( int i = 1; i <= columnCount; i++ ) {
                        Object object = rs.getObject( i );
                        System.out.println(object);
                    }
                }
            }
            try ( ResultSet rs = connection.getMetaData().getUDTs( null, null, null, null ) ) {
                final int columnCount = rs.getMetaData().getColumnCount();
                while ( rs.next() ) {
                    for ( int i = 1; i <= columnCount; i++ ) {
                        Object object = rs.getObject( i );
                        System.out.println(object);
                    }
                }
            }
            PreparedStatement statement = connection.prepareStatement(
                    "select vm.OSPROFILE from AZURE.VIRTUAL_MACHINE vm where vm.OSPROFILE.linuxConfiguration.disablePasswordAuthentication = false" );
			try (ResultSet rs = statement.executeQuery()) {
                while ( rs.next() ) {
                    Object object = rs.getObject( 1 );
                    System.out.println(object);
                }
			}
        }
    }

}
