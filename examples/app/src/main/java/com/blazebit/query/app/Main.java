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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViews;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;
import com.blazebit.query.Query;
import com.blazebit.query.QueryContext;
import com.blazebit.query.QuerySession;
import com.blazebit.query.connector.azure.blob.services.model.BlobServiceProperties;
import com.blazebit.query.connector.azure.invoker.ApiClient;
import com.blazebit.query.connector.azure.invoker.auth.OAuth;
import com.blazebit.query.connector.azure.storage.accounts.model.StorageAccount;
import com.blazebit.query.connector.azure.subscription.AzureConnectorConfig;
import com.blazebit.query.connector.azure.vm.model.VirtualMachine;
import com.blazebit.query.connector.view.EntityViewConnectorConfig;
import com.blazebit.query.spi.Queries;
import com.blazebit.query.spi.QueryContextBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory( "default" )) {
            SessionFactory sf = emf.unwrap( SessionFactory.class );
            sf.inTransaction( s -> {
                s.persist( new TestEntity( 1L, "Test", new TestEmbeddable( "text1", "text2" ) ) );
            } );

            CriteriaBuilderFactory cbf = Criteria.getDefault().createCriteriaBuilderFactory( emf );
            EntityViewConfiguration defaultConfiguration = EntityViews.createDefaultConfiguration();
            defaultConfiguration.addEntityView( TestEntityView.class );
            defaultConfiguration.addEntityView( TestEmbeddableView.class );
            EntityViewManager evm = defaultConfiguration.createEntityViewManager( cbf );

            QueryContextBuilder queryContextBuilder = Queries.createQueryContextBuilder();
            queryContextBuilder.setProperty( AzureConnectorConfig.API_CLIENT.getPropertyName(), createApiClient() );
            queryContextBuilder.setProperty( EntityViewConnectorConfig.ENTITY_VIEW_MANAGER.getPropertyName(), evm );
            try (QueryContext queryContext = queryContextBuilder.build()) {
                try (EntityManager em = emf.createEntityManager();
                     QuerySession session = queryContext.createSession(Map.of( EntityViewConnectorConfig.ENTITY_MANAGER.getPropertyName(), em))) {

                    Query entityViewQuery = session.createQuery(
                            "select t.id, e.text1 from " + name(TestEntityView.class) + " t, unnest(t.elements) e" );
                    List<Object[]> entityViewResult = entityViewQuery.getResultList();
                    print(entityViewResult, "id", "text1");

                    Query vmQuery = session.createQuery(
                            "select vm.* from " + name(VirtualMachine.class) + " vm where vm.properties.osProfile.linuxConfiguration.disablePasswordAuthentication = false" );
                    List<Object[]> vmResult = vmQuery.getResultList();
                    System.out.println("VMs");
                    for ( Object[] tuple : vmResult ) {
                        System.out.println( Arrays.toString( tuple ) );
                    }

                    Query storageAccountsQuery = session.createQuery(
                            "select sa.* from " + name(StorageAccount.class) + " sa where exists (select 1 from " + name(BlobServiceProperties.class) + " bs where bs.id like sa.id || '/%' and bs.properties.isVersioningEnabled)" );
                    List<Object[]> storageAccountsResult = storageAccountsQuery.getResultList();
                    System.out.println("StorageAccounts");
                    for ( Object[] tuple : storageAccountsResult ) {
                        System.out.println( Arrays.toString( tuple ) );
                    }
                }
            }
        }
    }

    private static String name(Class<?> clazz) {
        String name = clazz.getName();
        StringBuilder sb = new StringBuilder( name.length() + 20 );
        sb.append( '`' );
        for ( int i = 0; i < name.length(); i++ ) {
            char c = name.charAt( i );
            if ( c == '.' ) {
                sb.append( '`' );
                sb.append( '.' );
                sb.append( '`' );
            } else {
                sb.append( c );
            }
        }
        sb.append( '`' );
        return sb.toString();
    }

    private static void print(List<Object[]> tuples, String... columnHeaders) {
        System.out.print("Row");
        System.out.print("\t");
        for ( String columnHeader : columnHeaders ) {
            System.out.print("| ");
            System.out.print( columnHeader);
            System.out.print("\t");
            System.out.print("\t");
        }
        System.out.println();
        for ( int i = 0; i < tuples.size(); i++ ) {
            Object[] tuple = tuples.get( i );
            if ( tuple.length != columnHeaders.length ) {
                throw new IllegalArgumentException( "Inconsistent column header. Tuple length is " + tuple.length + " but only " + columnHeaders.length + " column header given" );
            }
            System.out.print( i + 1 );
            System.out.print( "\t" );
            for ( Object o : tuple ) {
                System.out.print("| ");
                System.out.print( o );
                System.out.print("\t");
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    private static ApiClient createApiClient() {
        String tenantId = "";
        String basePath = "https://login.microsoftonline.com/" + tenantId;
        String clientId = "";
        String clientSecret = "";
        OAuth oAuth = new OAuth( basePath, "/oauth2/v2.0/token" )
                .setCredentials( clientId, clientSecret, false )
                // Default scope
                .setScope( "https://management.core.windows.net//.default" );
        ApiClient apiClient = new ApiClient( Map.of( "azure_auth", oAuth ) );
        apiClient.getJSON().getMapper().configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        return apiClient;
    }

}
