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

import com.azure.core.credential.TokenCredential;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.fluent.models.VirtualMachineInner;
import com.azure.resourcemanager.storage.fluent.models.BlobServicePropertiesInner;
import com.azure.resourcemanager.storage.fluent.models.StorageAccountInner;
import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViews;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;
import com.blazebit.query.QueryContext;
import com.blazebit.query.QuerySession;
import com.blazebit.query.TypedQuery;
import com.blazebit.query.connector.azure.base.AzureConnectorConfig;
import com.blazebit.query.connector.azure.base.invoker.ApiClient;
import com.blazebit.query.connector.azure.base.invoker.auth.OAuth;
import com.blazebit.query.connector.azure.blob.services.v20230501.model.BlobServiceProperties;
import com.blazebit.query.connector.azure.graph.AzureGraphConnectorConfig;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceManagerConnectorConfig;
import com.blazebit.query.connector.azure.storage.accounts.v20230501.model.StorageAccount;
import com.blazebit.query.connector.azure.virtual.machine.v20240301.model.VirtualMachine;
import com.blazebit.query.connector.view.EntityViewConnectorConfig;
import com.blazebit.query.spi.Queries;
import com.blazebit.query.spi.QueryContextBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.microsoft.graph.models.User;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {

    private static final String TENANT_ID = "";
    private static final String CLIENT_ID = "";
    private static final String CLIENT_SECRET = "";

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
            queryContextBuilder.setProperty( AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getPropertyName(), createResourceManager() );
            queryContextBuilder.setProperty( AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getPropertyName(), createGraphServiceClient() );
            queryContextBuilder.setProperty( EntityViewConnectorConfig.ENTITY_VIEW_MANAGER.getPropertyName(), evm );
            queryContextBuilder.registerSchemaObjectAlias( VirtualMachine.class, "OpenAPIVirtualMachine" );
            queryContextBuilder.registerSchemaObjectAlias( StorageAccount.class, "OpenAPIStorageAccount" );
            queryContextBuilder.registerSchemaObjectAlias( BlobServiceProperties.class, "OpenAPIBlobServiceProperties" );

            queryContextBuilder.registerSchemaObjectAlias( VirtualMachineInner.class, "AzureVirtualMachine" );
            queryContextBuilder.registerSchemaObjectAlias( StorageAccountInner.class, "AzureStorageAccount" );
            queryContextBuilder.registerSchemaObjectAlias( BlobServicePropertiesInner.class, "AzureBlobServiceProperties" );

            queryContextBuilder.registerSchemaObjectAlias( User.class, "AzureUser" );

            try (QueryContext queryContext = queryContextBuilder.build()) {
                try (EntityManager em = emf.createEntityManager();
                     QuerySession session = queryContext.createSession(Map.of( EntityViewConnectorConfig.ENTITY_MANAGER.getPropertyName(), em))) {
                    TypedQuery<Object[]> entityViewQuery = session.createQuery(
                            "select t.id, e.text1 from " + name(TestEntityView.class) + " t, unnest(t.elements) e" );
                    List<Object[]> entityViewResult = entityViewQuery.getResultList();
                    print(entityViewResult, "id", "text1");

//                    TypedQuery<String> userQuery = session.createQuery(
//                            "select u.id from AzureUser u where cardinality(u.authentication.methods) < 1", String.class );
//                    List<String> userResult = userQuery.getResultList();
//                    System.out.println("User ids");
//                    for ( String id : userResult ) {
//                        System.out.println( id );
//                    }

                    TypedQuery<Object[]> vmQuery1 = session.createQuery(
                            "select vm.* from OpenAPIVirtualMachine vm where vm.properties.osProfile.linuxConfiguration.disablePasswordAuthentication = false" );
                    List<Object[]> vmResult1 = vmQuery1.getResultList();
                    System.out.println("VMs");
                    for ( Object[] vmId : vmResult1 ) {
                        System.out.println( Arrays.toString( vmId ) );
                    }

                    TypedQuery<Object[]> vmQuery2 = session.createQuery(
                            "select vm.* from AzureVirtualMachine vm where vm.osProfile.linuxConfiguration.disablePasswordAuthentication = false" );
                    List<Object[]> vmResult2 = vmQuery2.getResultList();
                    System.out.println("VMs");
                    for ( Object[] vmId : vmResult2 ) {
                        System.out.println( Arrays.toString( vmId ) );
                    }

                    TypedQuery<String> storageAccountsQuery1 = session.createQuery(
                            "select sa.id from OpenAPIStorageAccount sa where exists (select 1 from OpenAPIBlobServiceProperties bs where bs.id like sa.id || '/%' and bs.properties.isVersioningEnabled)", String.class );
                    List<String> storageAccountsResult1 = storageAccountsQuery1.getResultList();
                    System.out.println("StorageAccount ids");
                    for ( String id : storageAccountsResult1 ) {
                        System.out.println( id );
                    }

                    TypedQuery<String> storageAccountsQuery2 = session.createQuery(
                            "select sa.id from AzureStorageAccount sa where exists (select 1 from AzureBlobServiceProperties bs where bs.id like sa.id || '/%' and bs.versioningEnabled)", String.class );
                    List<String> storageAccountsResult2 = storageAccountsQuery2.getResultList();
                    System.out.println("StorageAccount ids");
                    for ( String id : storageAccountsResult2 ) {
                        System.out.println( id );
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
        String basePath = "https://login.microsoftonline.com/" + TENANT_ID;
        OAuth oAuth = new OAuth( basePath, "/oauth2/v2.0/token" )
                .setCredentials( CLIENT_ID, CLIENT_SECRET, false )
                // Default scope
                .setScope( "https://management.core.windows.net//.default" );
        ApiClient apiClient = new ApiClient( Map.of( "azure_auth", oAuth ) );
        apiClient.getJSON().getMapper().configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        return apiClient;
    }

    private static AzureResourceManager createResourceManager() {
        AzureProfile profile = new AzureProfile(TENANT_ID, null, AzureEnvironment.AZURE);
        TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();
        return AzureResourceManager.authenticate(credential, profile).withDefaultSubscription();
    }

    private static GraphServiceClient createGraphServiceClient() {
        AzureProfile profile = new AzureProfile(TENANT_ID, null, AzureEnvironment.AZURE);
        TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();
        // Default scope
        return new GraphServiceClient(credential, "https://management.core.windows.net//.default");
    }

}
