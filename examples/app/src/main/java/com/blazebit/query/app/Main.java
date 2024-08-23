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

import java.io.IOException;
import com.microsoft.graph.beta.models.ManagedDevice;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
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
//import com.blazebit.query.connector.azure.base.AzureConnectorConfig;
//import com.blazebit.query.connector.azure.base.invoker.ApiClient;
//import com.blazebit.query.connector.azure.base.invoker.auth.OAuth;
//import com.blazebit.query.connector.azure.blob.services.v20230501.model.BlobServiceProperties;
import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.azure.graph.AzureGraphConnectorConfig;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceManagerConnectorConfig;
//import com.blazebit.query.connector.azure.storage.accounts.v20230501.model.StorageAccount;
//import com.blazebit.query.connector.azure.virtual.machine.v20240301.model.VirtualMachine;
import com.blazebit.query.connector.github.GithubConnectorConfig;
import com.blazebit.query.connector.gitlab.GitlabConnectorConfig;
import com.blazebit.query.connector.gitlab.GroupMember;
import com.blazebit.query.connector.gitlab.ProjectMember;
import com.blazebit.query.connector.gitlab.ProjectProtectedBranch;
import com.blazebit.query.connector.view.EntityViewConnectorConfig;
import com.blazebit.query.spi.Queries;
import com.blazebit.query.spi.QueryContextBuilder;
//import com.fasterxml.jackson.databind.DeserializationFeature;
import com.microsoft.graph.beta.models.Application;
import com.microsoft.graph.beta.models.ConditionalAccessPolicy;
import com.microsoft.graph.beta.models.User;
import com.microsoft.graph.beta.serviceclient.GraphServiceClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Project;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHProject;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.NetworkAcl;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Volume;
import software.amazon.awssdk.services.ec2.model.Vpc;
import software.amazon.awssdk.services.ecr.model.Repository;
import software.amazon.awssdk.services.ecs.model.Cluster;
import software.amazon.awssdk.services.efs.model.FileSystemDescription;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.route53.model.HealthCheck;
import software.amazon.awssdk.services.route53.model.HostedZone;
import software.amazon.awssdk.services.s3.model.Bucket;

public class Main {

    private static final String AZURE_TENANT_ID = "";
    private static final String AZURE_CLIENT_ID = "";
    private static final String AZURE_CLIENT_SECRET = "";
    private static final String AWS_REGION = "";
    private static final String AWS_ACCESS_KEY_ID = "";
    private static final String AWS_SECRET_ACCESS_KEY = "";
    private static final String GITLAB_HOST = "";
    private static final String GITLAB_KEY = "";
    private static final String GITHUB_KEY = "";

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
//            queryContextBuilder.setProperty(AzureConnectorConfig.API_CLIENT.getPropertyName(), createApiClient());
//            queryContextBuilder.setProperty(AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getPropertyName(), createResourceManager());
//            queryContextBuilder.setProperty(AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getPropertyName(), createGraphServiceClient());
//            queryContextBuilder.setProperty(AwsConnectorConfig.ACCOUNT.getPropertyName(), createAwsAccount());
            queryContextBuilder.setProperty(EntityViewConnectorConfig.ENTITY_VIEW_MANAGER.getPropertyName(), evm);
//            queryContextBuilder.setProperty(GitlabConnectorConfig.GITLAB_API.getPropertyName(), createGitlabApi());
            queryContextBuilder.setProperty(GithubConnectorConfig.GITHUB.getPropertyName(), createGithub());
//            queryContextBuilder.registerSchemaObjectAlias(VirtualMachine.class, "OpenAPIVirtualMachine");
//            queryContextBuilder.registerSchemaObjectAlias(StorageAccount.class, "OpenAPIStorageAccount");
//            queryContextBuilder.registerSchemaObjectAlias(BlobServiceProperties.class, "OpenAPIBlobServiceProperties");

            // Azure Resource manager
            queryContextBuilder.registerSchemaObjectAlias(VirtualMachineInner.class, "AzureVirtualMachine");
            queryContextBuilder.registerSchemaObjectAlias(StorageAccountInner.class, "AzureStorageAccount");
            queryContextBuilder.registerSchemaObjectAlias(BlobServicePropertiesInner.class, "AzureBlobServiceProperties");

            // Azure Graph
            queryContextBuilder.registerSchemaObjectAlias(User.class, "AzureUser");
            queryContextBuilder.registerSchemaObjectAlias(ConditionalAccessPolicy.class, "AzureConditionalAccessPolicy");
            queryContextBuilder.registerSchemaObjectAlias(Application.class, "AzureApplication");
            queryContextBuilder.registerSchemaObjectAlias(ManagedDevice.class, "AzureManagedDevice");

            // IAM
            queryContextBuilder.registerSchemaObjectAlias(software.amazon.awssdk.services.iam.model.User.class, "AwsUser");
            // EC2
            queryContextBuilder.registerSchemaObjectAlias(Instance.class, "AwsInstance");
            queryContextBuilder.registerSchemaObjectAlias(Volume.class, "AwsVolume");
            queryContextBuilder.registerSchemaObjectAlias(Vpc.class, "AwsVpc");
            queryContextBuilder.registerSchemaObjectAlias(SecurityGroup.class, "AwsSecurityGroup");
            queryContextBuilder.registerSchemaObjectAlias(NetworkAcl.class, "AwsNetworkAcl");
            // RDS
            queryContextBuilder.registerSchemaObjectAlias(DBInstance.class, "AwsDBInstance");
            // EFS
            queryContextBuilder.registerSchemaObjectAlias(FileSystemDescription.class, "AwsFileSystem");
            // ECR
            queryContextBuilder.registerSchemaObjectAlias(Repository.class, "AwsRepository");
            // ECS
            queryContextBuilder.registerSchemaObjectAlias(Cluster.class, "AwsCluster");
            // ELB
            queryContextBuilder.registerSchemaObjectAlias(LoadBalancer.class, "AwsLoadBalancer");
            // Lambda
            queryContextBuilder.registerSchemaObjectAlias(FunctionConfiguration.class, "AwsFunction");
            // Route53
            queryContextBuilder.registerSchemaObjectAlias(HostedZone.class, "AwsHostedZone");
            queryContextBuilder.registerSchemaObjectAlias(HealthCheck.class, "AwsHealthCheck");
            // S3
            queryContextBuilder.registerSchemaObjectAlias(Bucket.class, "AwsBucket");

            // Gitlab
            queryContextBuilder.registerSchemaObjectAlias(Project.class, "GitlabProject");
            queryContextBuilder.registerSchemaObjectAlias(Group.class, "GitlabGroup");
            queryContextBuilder.registerSchemaObjectAlias(ProjectMember.class, "GitlabProjectMember");
            queryContextBuilder.registerSchemaObjectAlias(GroupMember.class, "GitlabGroupMember");
            queryContextBuilder.registerSchemaObjectAlias(org.gitlab4j.api.models.User.class, "GitlabUser");
            queryContextBuilder.registerSchemaObjectAlias(ProjectProtectedBranch.class, "GitlabProjectProtectedBranch");

            // GitHub
            queryContextBuilder.registerSchemaObjectAlias(GHOrganization.class, "GitHubOrganization");
            queryContextBuilder.registerSchemaObjectAlias(GHRepository.class, "GitHubRepository");
            queryContextBuilder.registerSchemaObjectAlias(GHBranch.class, "GitHubBranch");
            queryContextBuilder.registerSchemaObjectAlias(GHProject.class, "GitHubProject");
            queryContextBuilder.registerSchemaObjectAlias(GHTeam.class, "GitHubTeam");

            try (QueryContext queryContext = queryContextBuilder.build()) {
                try (EntityManager em = emf.createEntityManager();
                     QuerySession session = queryContext.createSession(Map.of( EntityViewConnectorConfig.ENTITY_MANAGER.getPropertyName(), em))) {
//                    testAws( session );
//                    testGitlab( session );
                    testGitHub( session );
//                    testEntityView( session );
//                    testAzureGraph( session );
//                    testAzureResourceManager( session );
//                    testAzureOpenAPI( session );
                }
            }
        }
    }

    private static void testAws(QuerySession session) {
        // IAM
        TypedQuery<Object[]> awsUserQuery = session.createQuery(
                "select u.* from AwsUser u" );
        List<Object[]> awsUserResult = awsUserQuery.getResultList();
        System.out.println("AwsUsers");
        print(awsUserResult);

        // EC2
        TypedQuery<Object[]> awsInstanceQuery = session.createQuery(
                "select i.* from AwsInstance i" );
        List<Object[]> awsInstanceResult = awsInstanceQuery.getResultList();
        System.out.println("AwsInstances");
        print(awsInstanceResult);

        TypedQuery<Object[]> awsVolumeQuery = session.createQuery(
                "select v.* from AwsVolume v" );
        List<Object[]> awsVolumeResult = awsVolumeQuery.getResultList();
        System.out.println("AwsVolumes");
        print(awsVolumeResult);

        TypedQuery<Object[]> awsVpcQuery = session.createQuery(
                "select v.* from AwsVpc v" );
        List<Object[]> awsVpcResult = awsVpcQuery.getResultList();
        System.out.println("AwsVpcs");
        print(awsVpcResult);

        TypedQuery<Object[]> awsSecurityGroupQuery = session.createQuery(
                "select g.* from AwsSecurityGroup g" );
        List<Object[]> awsSecurityGroupResult = awsSecurityGroupQuery.getResultList();
        System.out.println("AwsSecurityGroups");
        print(awsSecurityGroupResult);

        TypedQuery<Object[]> awsNetworkAclQuery = session.createQuery(
                "select g.* from AwsNetworkAcl g" );
        List<Object[]> awsNetworkAclResult = awsNetworkAclQuery.getResultList();
        System.out.println("AwsNetworkAcls");
        print(awsNetworkAclResult);

        // RDS
        TypedQuery<Object[]> awsDbInstanceQuery = session.createQuery(
                "select i.* from AwsDBInstance i" );
        List<Object[]> awsDbInstanceResult = awsDbInstanceQuery.getResultList();
        System.out.println("AwsDbInstances");
        print(awsDbInstanceResult);

        // EFS
        TypedQuery<Object[]> awsFileSystemQuery = session.createQuery(
                "select f.* from AwsFileSystem f" );
        List<Object[]> awsFileSystemResult = awsFileSystemQuery.getResultList();
        System.out.println("AwsFileSystems");
        print(awsFileSystemResult);

        // ECR
        TypedQuery<Object[]> awsRepositoryQuery = session.createQuery(
                "select f.* from AwsRepository f" );
        List<Object[]> awsRepositoryResult = awsRepositoryQuery.getResultList();
        System.out.println("AwsRepositories");
        print(awsRepositoryResult);

        // ECS
        TypedQuery<Object[]> awsClusterQuery = session.createQuery(
                "select f.* from AwsCluster f" );
        List<Object[]> awsClusterResult = awsClusterQuery.getResultList();
        System.out.println("AwsClusters");
        print(awsClusterResult);

        // ELB
        TypedQuery<Object[]> awsLoadBalancerQuery = session.createQuery(
                "select f.* from AwsLoadBalancer f" );
        List<Object[]> awsLoadBalancerResult = awsLoadBalancerQuery.getResultList();
        System.out.println("AwsLoadBalancers");
        print(awsLoadBalancerResult);

        // Lambda
        TypedQuery<Object[]> awsFunctionsQuery = session.createQuery(
                "select f.* from AwsFunction f" );
        List<Object[]> awsFunctionsResult = awsFunctionsQuery.getResultList();
        System.out.println("AwsFunctions");
        print(awsFunctionsResult);

        // Route53
        TypedQuery<Object[]> awsHostedZoneQuery = session.createQuery(
                "select f.* from AwsHostedZone f" );
        List<Object[]> awsHostedZoneResult = awsHostedZoneQuery.getResultList();
        System.out.println("AwsHostedZones");
        print(awsHostedZoneResult);
        TypedQuery<Object[]> awsHealthCheckQuery = session.createQuery(
                "select f.* from AwsHealthCheck f" );
        List<Object[]> awsHealthCheckResult = awsHealthCheckQuery.getResultList();
        System.out.println("AwsHealthChecks");
        print(awsHealthCheckResult);

        // S3
        TypedQuery<Object[]> awsBucketQuery = session.createQuery(
                "select f.* from AwsBucket f" );
        List<Object[]> awsBucketResult = awsBucketQuery.getResultList();
        System.out.println("AwsBuckets");
        print(awsBucketResult);
    }

    private static void testGitlab(QuerySession session) {
        TypedQuery<Object[]> gitlabProjectQuery = session.createQuery(
                "select p.* from GitlabProject p" );
        List<Object[]> gitlabProjectResult = gitlabProjectQuery.getResultList();
        System.out.println("GitlabProjects");
        print(gitlabProjectResult);
        TypedQuery<Object[]> gitlabGroupQuery = session.createQuery(
                "select p.* from GitlabGroup p" );
        List<Object[]> gitlabGroupResult = gitlabGroupQuery.getResultList();
        System.out.println("GitlabGroups");
        print(gitlabGroupResult);
        TypedQuery<Object[]> gitlabProjectMemberQuery = session.createQuery(
                "select p.* from GitlabProjectMember p" );
        List<Object[]> gitlabProjectMemberResult = gitlabProjectMemberQuery.getResultList();
        System.out.println("GitlabProjectMembers");
        print(gitlabProjectMemberResult);
        TypedQuery<Object[]> gitlabGroupMemberQuery = session.createQuery(
                "select p.* from GitlabGroupMember p" );
        List<Object[]> gitlabGroupMemberResult = gitlabGroupMemberQuery.getResultList();
        System.out.println("GitlabGroupMembers");
        print(gitlabGroupMemberResult);
        TypedQuery<Object[]> gitlabUserQuery = session.createQuery(
                "select p.* from GitlabUser p" );
        List<Object[]> gitlabUserResult = gitlabUserQuery.getResultList();
        System.out.println("GitlabUsers");
        print(gitlabUserResult);
        TypedQuery<Object[]> gitlabProtectedBranchQuery = session.createQuery(
                "select p.* from GitlabProjectProtectedBranch p" );
        List<Object[]> gitlabProtectedBranchResult = gitlabProtectedBranchQuery.getResultList();
        System.out.println("GitlabProtectedBranches");
        print(gitlabProtectedBranchResult);
    }

    private static void testGitHub(QuerySession session) {
        TypedQuery<Object[]> gitHubOrganizationQuery = session.createQuery(
                "select p.* from GitHubOrganization p" );
        List<Object[]> gitHubOrganizationResult = gitHubOrganizationQuery.getResultList();
        System.out.println("GitHubOrganizations");
        print(gitHubOrganizationResult);
        TypedQuery<Object[]> gitHubRepositoryQuery = session.createQuery(
                "select p.* from GitHubRepository p" );
        List<Object[]> gitHubRepositoryResult = gitHubRepositoryQuery.getResultList();
        System.out.println("GitHubRepositories");
        print(gitHubRepositoryResult);
        TypedQuery<Object[]> gitHubBranchQuery = session.createQuery(
                "select p.* from GitHubBranch p" );
        List<Object[]> gitHubBranchResult = gitHubBranchQuery.getResultList();
        System.out.println("GitHubBranches");
        print(gitHubBranchResult);
        TypedQuery<Object[]> gitHubProjectQuery = session.createQuery(
                "select p.* from GitHubProject p" );
        List<Object[]> gitHubProjectResult = gitHubProjectQuery.getResultList();
        System.out.println("GitHubProjects");
        print(gitHubProjectResult);
        TypedQuery<Object[]> gitHubTeamQuery = session.createQuery(
                "select p.* from GitHubTeam p" );
        List<Object[]> gitHubTeamResult = gitHubTeamQuery.getResultList();
        System.out.println("GitHubTeams");
        print(gitHubTeamResult);
    }

    private static void testEntityView(QuerySession session) {
        TypedQuery<Object[]> entityViewQuery = session.createQuery(
                "select t.id, e.text1 from " + name(TestEntityView.class) + " t, unnest(t.elements) e" );
        List<Object[]> entityViewResult = entityViewQuery.getResultList();
        print(entityViewResult, "id", "text1");
    }

    private static void testAzureGraph(QuerySession session) {
        TypedQuery<Object[]> userQuery = session.createQuery(
                "select u.* from AzureUser u" );
        List<Object[]> userResult = userQuery.getResultList();
        System.out.println("User");
        print(userResult);

        TypedQuery<Object[]> conditionalAccessPolicyQuery = session.createQuery(
                "select c.* from AzureConditionalAccessPolicy c" );
        List<Object[]> conditionalAccessPolicyResult = conditionalAccessPolicyQuery.getResultList();
        System.out.println("Conditional access policies");
        print(conditionalAccessPolicyResult);

        TypedQuery<Object[]> applicationQuery = session.createQuery(
                "select a.* from AzureApplication a" );
        List<Object[]> applicationResult = applicationQuery.getResultList();
        System.out.println("Applications");
        print(applicationResult);

        TypedQuery<Object[]> managedDevices = session.createQuery(
        "select a.* from AzureManagedDevice a" );
        List<Object[]> managedDevicesResult = managedDevices.getResultList();
        System.out.println("Managed Devices");
        print(managedDevicesResult);
    }

    private static void testAzureResourceManager(QuerySession session) {
        TypedQuery<Object[]> vmQuery1 = session.createQuery(
                "select vm.* from AzureVirtualMachine vm where vm.storageProfile.osDisk.osType <> 'Linux'" );
        List<Object[]> vmResult1 = vmQuery1.getResultList();
        System.out.println("Non Linux VMs");
        print(vmResult1);

        TypedQuery<Object[]> vmQuery2 = session.createQuery(
                "select vm.* from AzureVirtualMachine vm where vm.osProfile.linuxConfiguration.disablePasswordAuthentication = false" );
        List<Object[]> vmResult2 = vmQuery2.getResultList();
        System.out.println("VMs");
        print(vmResult2);

        TypedQuery<Object[]> storageAccountsQuery2 = session.createQuery(
                "select sa.* from AzureStorageAccount sa where exists (select 1 from AzureBlobServiceProperties bs where bs.id like sa.id || '/%' and bs.versioningEnabled)" );
        List<Object[]> storageAccountsResult2 = storageAccountsQuery2.getResultList();
        System.out.println("StorageAccounts");
        print(storageAccountsResult2);
    }

    private static void testAzureOpenAPI(QuerySession session) {
        TypedQuery<Object[]> vmQuery1 = session.createQuery(
                "select vm.* from OpenAPIVirtualMachine vm where vm.properties.osProfile.linuxConfiguration.disablePasswordAuthentication = false" );
        List<Object[]> vmResult1 = vmQuery1.getResultList();
        System.out.println("VMs");
        print(vmResult1);

        TypedQuery<Object[]> storageAccountsQuery1 = session.createQuery(
                "select sa.* from OpenAPIStorageAccount sa where exists (select 1 from OpenAPIBlobServiceProperties bs where bs.id like sa.id || '/%' and bs.properties.isVersioningEnabled)" );
        List<Object[]> storageAccountsResult1 = storageAccountsQuery1.getResultList();
        System.out.println("StorageAccounts");
        print(storageAccountsResult1);
    }

    private static AwsConnectorConfig.Account createAwsAccount() {
        return new AwsConnectorConfig.Account(
                Region.of(AWS_REGION),
                StaticCredentialsProvider.create(AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY))
        );
    }

    private static GitLabApi createGitlabApi() {
        final GitLabApi gitLabApi = new GitLabApi( GITLAB_HOST, GITLAB_KEY );
        if ( !GITLAB_HOST.startsWith( "https://gitlab.com" )) {
            gitLabApi.setIgnoreCertificateErrors( true );
        }
        return gitLabApi;
    }

    private static GitHub createGithub() {
		try {
			return new GitHubBuilder().withOAuthToken( GITHUB_KEY ).build();
		}
		catch (IOException e) {
			throw new RuntimeException( e );
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

//    private static ApiClient createApiClient() {
//        String basePath = "https://login.microsoftonline.com/" + TENANT_ID;
//        OAuth oAuth = new OAuth(basePath, "/oauth2/v2.0/token")
//                .setCredentials(CLIENT_ID, CLIENT_SECRET, false)
//                // Default scope
//                .setScope("https://management.core.windows.net//.default");
//        ApiClient apiClient = new ApiClient(Map.of("azure_auth", oAuth));
//        apiClient.getJSON().getMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        return apiClient;
//    }

    private static AzureResourceManager createResourceManager() {
        AzureProfile profile = new AzureProfile( AZURE_TENANT_ID, null, AzureEnvironment.AZURE);
        ClientSecretCredential credentials = new ClientSecretCredentialBuilder()
                .clientId( AZURE_CLIENT_ID )
                .clientSecret( AZURE_CLIENT_SECRET )
                .tenantId( AZURE_TENANT_ID )
                .build();
        return AzureResourceManager.authenticate(credentials, profile).withDefaultSubscription();
    }

    private static GraphServiceClient createGraphServiceClient() {
        ClientSecretCredential credentials = new ClientSecretCredentialBuilder()
                .clientId( AZURE_CLIENT_ID )
                .clientSecret( AZURE_CLIENT_SECRET )
                .tenantId( AZURE_TENANT_ID )
                .build();
        // Default scope
        return new GraphServiceClient(credentials, "https://graph.microsoft.com/.default");
    }

}
