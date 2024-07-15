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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import com.azure.core.credential.TokenCredential;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
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
import com.microsoft.graph.beta.models.Application;
import com.microsoft.graph.beta.models.ConditionalAccessPolicy;
import com.microsoft.graph.beta.models.User;
import com.microsoft.graph.beta.serviceclient.GraphServiceClient;
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
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("default")) {
            SessionFactory sf = emf.unwrap(SessionFactory.class);
            sf.inTransaction( s -> {
                s.persist(new TestEntity(1L, "Test", new TestEmbeddable("text1", "text2")));
            } );

            CriteriaBuilderFactory cbf = Criteria.getDefault().createCriteriaBuilderFactory(emf);
            EntityViewConfiguration defaultConfiguration = EntityViews.createDefaultConfiguration();
            defaultConfiguration.addEntityView(TestEntityView.class);
            defaultConfiguration.addEntityView(TestEmbeddableView.class);
            EntityViewManager evm = defaultConfiguration.createEntityViewManager(cbf);

            QueryContextBuilder queryContextBuilder = Queries.createQueryContextBuilder();
            queryContextBuilder.setProperty(AzureConnectorConfig.API_CLIENT.getPropertyName(), createApiClient());
            queryContextBuilder.setProperty(AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getPropertyName(), createResourceManager());
            queryContextBuilder.setProperty(AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getPropertyName(), createGraphServiceClient());
            queryContextBuilder.setProperty(EntityViewConnectorConfig.ENTITY_VIEW_MANAGER.getPropertyName(), evm);
            queryContextBuilder.registerSchemaObjectAlias(VirtualMachine.class, "OpenAPIVirtualMachine");
            queryContextBuilder.registerSchemaObjectAlias(StorageAccount.class, "OpenAPIStorageAccount");
            queryContextBuilder.registerSchemaObjectAlias(BlobServiceProperties.class, "OpenAPIBlobServiceProperties");

            queryContextBuilder.registerSchemaObjectAlias(VirtualMachineInner.class, "AzureVirtualMachine");
            queryContextBuilder.registerSchemaObjectAlias(StorageAccountInner.class, "AzureStorageAccount");
            queryContextBuilder.registerSchemaObjectAlias(BlobServicePropertiesInner.class, "AzureBlobServiceProperties");

            queryContextBuilder.registerSchemaObjectAlias(User.class, "AzureUser");
            queryContextBuilder.registerSchemaObjectAlias(ConditionalAccessPolicy.class, "AzureConditionalAccessPolicy");
            queryContextBuilder.registerSchemaObjectAlias(Application.class, "AzureApplication");

            try (QueryContext queryContext = queryContextBuilder.build()) {
                try (EntityManager em = emf.createEntityManager();
                     QuerySession session = queryContext.createSession(Map.of( EntityViewConnectorConfig.ENTITY_MANAGER.getPropertyName(), em))) {
                    TypedQuery<Object[]> entityViewQuery = session.createQuery(
                            "select t.id, e.text1 from " + name(TestEntityView.class) + " t, unnest(t.elements) e" );
                    List<Object[]> entityViewResult = entityViewQuery.getResultList();
                    print(entityViewResult, "id", "text1");

                    TypedQuery<Object[]> userQuery = session.createQuery(
                            "select u.* from AzureUser u", Object[].class );
                    List<Object[]> userResult = userQuery.getResultList();
                    System.out.println("User");
                    print(userResult);

                    TypedQuery<Object[]> conditionalAccessPolicyQuery = session.createQuery(
                            "select c.* from AzureConditionalAccessPolicy c", Object[].class );
                    List<Object[]> conditionalAccessPolicyResult = conditionalAccessPolicyQuery.getResultList();
                    System.out.println("Conditional access policies");
                    print(conditionalAccessPolicyResult);

                    TypedQuery<Object[]> applicationQuery = session.createQuery(
                            "select a.* from AzureApplication a", Object[].class );
                    List<Object[]> applicationResult = applicationQuery.getResultList();
                    System.out.println("Applications");
                    print(applicationResult);

                    TypedQuery<Object[]> vmQuery1 = session.createQuery(
                            "select vm.* from OpenAPIVirtualMachine vm where vm.properties.osProfile.linuxConfiguration.disablePasswordAuthentication = false" );
                    List<Object[]> vmResult1 = vmQuery1.getResultList();
                    System.out.println("VMs");
                    print(vmResult1);

                    TypedQuery<Object[]> vmQuery2 = session.createQuery(
                            "select vm.* from AzureVirtualMachine vm where vm.osProfile.linuxConfiguration.disablePasswordAuthentication = false" );
                    List<Object[]> vmResult2 = vmQuery2.getResultList();
                    System.out.println("VMs");
                    print(vmResult2);

                    TypedQuery<Object[]> storageAccountsQuery1 = session.createQuery(
                            "select sa.* from OpenAPIStorageAccount sa where exists (select 1 from OpenAPIBlobServiceProperties bs where bs.id like sa.id || '/%' and bs.properties.isVersioningEnabled)", Object[].class );
                    List<Object[]> storageAccountsResult1 = storageAccountsQuery1.getResultList();
                    System.out.println("StorageAccounts");
                    print(storageAccountsResult1);

                    TypedQuery<Object[]> storageAccountsQuery2 = session.createQuery(
                            "select sa.* from AzureStorageAccount sa where exists (select 1 from AzureBlobServiceProperties bs where bs.id like sa.id || '/%' and bs.versioningEnabled)", Object[].class );
                    List<Object[]> storageAccountsResult2 = storageAccountsQuery2.getResultList();
                    System.out.println("StorageAccounts");
                    print(storageAccountsResult2);
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
        if (columnHeaders.length > 0) {
            System.out.print( "Row" );
            System.out.print( "\t" );
            for ( String columnHeader : columnHeaders ) {
                System.out.print( "| " );
                System.out.print( columnHeader );
                System.out.print( "\t" );
                System.out.print( "\t" );
            }
            System.out.println();
        }
        for (int i = 0; i < tuples.size(); i++) {
            Object[] tuple = tuples.get(i);
            if (columnHeaders.length != 0 && tuple.length != columnHeaders.length) {
                throw new IllegalArgumentException("Inconsistent column header. Tuple length is " + tuple.length + " but only " + columnHeaders.length + " column header given");
            }
            System.out.print(i + 1);
            System.out.print("\t");
            for (Object o : tuple) {
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
        OAuth oAuth = new OAuth(basePath, "/oauth2/v2.0/token")
                .setCredentials(CLIENT_ID, CLIENT_SECRET, false)
                // Default scope
                .setScope("https://management.core.windows.net//.default");
        ApiClient apiClient = new ApiClient(Map.of("azure_auth", oAuth));
        apiClient.getJSON().getMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return apiClient;
    }

    private static AzureResourceManager createResourceManager() {
        AzureProfile profile = new AzureProfile(TENANT_ID, null, AzureEnvironment.AZURE);
        ClientSecretCredential credentials = new ClientSecretCredentialBuilder()
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .tenantId(TENANT_ID)
                .build();
        return AzureResourceManager.authenticate(credentials, profile).withDefaultSubscription();
    }

    private static GraphServiceClient createGraphServiceClient() {
        ClientSecretCredential credentials = new ClientSecretCredentialBuilder()
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .tenantId(TENANT_ID)
                .build();
        // Default scope
        return new GraphServiceClient(credentials, "https://graph.microsoft.com/.default");
    }

}
