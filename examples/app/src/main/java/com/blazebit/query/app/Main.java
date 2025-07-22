/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.app;

import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.postgresqlflexibleserver.PostgreSqlManager;
import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViews;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;
import com.blazebit.query.QueryContext;
import com.blazebit.query.QuerySession;
import com.blazebit.query.TypedQuery;
import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.ec2.AwsInstance;
import com.blazebit.query.connector.aws.ec2.AwsNetworkAcl;
import com.blazebit.query.connector.aws.ec2.AwsSecurityGroup;
import com.blazebit.query.connector.aws.ec2.AwsVolume;
import com.blazebit.query.connector.aws.ec2.AwsVpc;
import com.blazebit.query.connector.aws.ecr.AwsRepository;
import com.blazebit.query.connector.aws.ecs.AwsCluster;
import com.blazebit.query.connector.aws.efs.AwsFileSystem;
import com.blazebit.query.connector.aws.elb.AwsLoadBalancer;
import com.blazebit.query.connector.aws.iam.AccessKeyMetaDataLastUsed;
import com.blazebit.query.connector.aws.iam.AccountSummary;
import com.blazebit.query.connector.aws.iam.AwsMFADevice;
import com.blazebit.query.connector.aws.iam.AwsPasswordPolicy;
import com.blazebit.query.connector.aws.iam.AwsUser;
import com.blazebit.query.connector.aws.lambda.AwsFunction;
import com.blazebit.query.connector.aws.rds.AwsDBInstance;
import com.blazebit.query.connector.aws.route53.AwsHealthCheck;
import com.blazebit.query.connector.aws.route53.AwsHostedZone;
import com.blazebit.query.connector.aws.s3.AwsBucket;
import com.blazebit.query.connector.azure.graph.AzureGraphApplication;
import com.blazebit.query.connector.azure.graph.AzureGraphClientAccessor;
import com.blazebit.query.connector.azure.graph.AzureGraphConditionalAccessPolicy;
import com.blazebit.query.connector.azure.graph.AzureGraphConnectorConfig;
import com.blazebit.query.connector.azure.graph.AzureGraphManagedDevice;
import com.blazebit.query.connector.azure.graph.AzureGraphOrganization;
import com.blazebit.query.connector.azure.graph.AzureGraphServicePlanInfo;
import com.blazebit.query.connector.azure.graph.AzureGraphUser;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceBlobServiceProperties;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceManagerConnectorConfig;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceManagedCluster;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourcePostgreSqlFlexibleServer;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceManagerPostgreSqlManager;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceManagerPostgreSqlManagerConnectorConfig;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourcePostgreSqlFlexibleServerBackup;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourcePostgreSqlFlexibleServerWithParameters;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceStorageAccount;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceSubscription;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceVault;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceVirtualMachine;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceVirtualNetwork;
import com.blazebit.query.connector.github.graphql.GitHubBranchProtectionRule;
import com.blazebit.query.connector.github.graphql.GitHubConnectorConfig;
import com.blazebit.query.connector.github.graphql.GitHubGraphQlClient;
import com.blazebit.query.connector.github.graphql.GitHubOrganization;
import com.blazebit.query.connector.github.graphql.GitHubPullRequest;
import com.blazebit.query.connector.github.graphql.GitHubRepository;
import com.blazebit.query.connector.github.graphql.GitHubRuleset;
import com.blazebit.query.connector.github.v0314.model.OrganizationSimple;
import com.blazebit.query.connector.github.v0314.model.ShortBranch;
import com.blazebit.query.connector.github.v0314.model.Team;
import com.blazebit.query.connector.gitlab.GitlabConnectorConfig;
import com.blazebit.query.connector.gitlab.GitlabGraphQlClient;
import com.blazebit.query.connector.gitlab.GitlabGraphQlConnectorConfig;
import com.blazebit.query.connector.gitlab.GitlabGroup;
import com.blazebit.query.connector.gitlab.GitlabMergeRequest;
import com.blazebit.query.connector.gitlab.GitlabProject;
import com.blazebit.query.connector.gitlab.GitlabUser;
import com.blazebit.query.connector.gitlab.GroupMember;
import com.blazebit.query.connector.gitlab.ProjectMember;
import com.blazebit.query.connector.gitlab.ProjectProtectedBranch;
import com.blazebit.query.connector.jira.cloud.model.UserPermission;
import com.blazebit.query.connector.jira.datacenter.model.PermissionGrantBean;
import com.blazebit.query.connector.kandji.DeviceParameter;
import com.blazebit.query.connector.kandji.KandjiJavaTimeModule;
import com.blazebit.query.connector.kandji.model.GetDeviceDetails200Response;
import com.blazebit.query.connector.kandji.model.ListDevices200ResponseInner;
import com.blazebit.query.connector.view.EntityViewConnectorConfig;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.Queries;
import com.blazebit.query.spi.QueryContextBuilder;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.directory.Directory;
import com.google.api.services.directory.DirectoryScopes;
import com.google.api.services.directory.model.Member;
import com.google.api.services.directory.model.Role;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.asset.v1.IamPolicySearchResult;
import com.google.iam.admin.v1.ServiceAccount;
import com.microsoft.graph.beta.serviceclient.GraphServiceClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Project;
import org.hibernate.SessionFactory;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

	private static final String AZURE_TENANT_ID = "";
	private static final String AZURE_CLIENT_ID = "";
	private static final String AZURE_CLIENT_SECRET = "";
	private static final String AWS_ACCOUNT_ID = "";
	private static final String AWS_REGION = "";
	private static final String AWS_ACCESS_KEY_ID = "";
	private static final String AWS_SECRET_ACCESS_KEY = "";
	private static final String GITLAB_HOST = "";
	private static final String GITLAB_KEY = "";
	private static final String GITHUB_KEY = "";
	private static final String KANDJI_BASE_PATH = "";
	private static final String KANDJI_KEY = "";

	private static final String GOOGLE_WORKSPACE_CLIENT_ID = "";
	private static final String GOOGLE_WORKSPACE_CLIENT_EMAIL = "";
	private static final String GOOGLE_WORKSPACE_PROJECT_ID = "";
	private static final String GOOGLE_WORKSPACE_PRIVATE_KEY_ID = "";
	private static final String GOOGLE_WORKSPACE_PRIVATE_KEY = "";
	private static final String GOOGLE_WORKSPACE_SERVICE_ACCOUNT_USER = "";

	private static final String JIRA_CLOUD_HOST = "";
	private static final String JIRA_DATACENTER_HOST = "";
	private static final String JIRA_CLOUD_USER = "";
	private static final String JIRA_CLOUD_TOKEN = "";
	private static final String JIRA_DATACENTER_TOKEN = "";

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
			queryContextBuilder.setProperty( AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getPropertyName(), createResourceManager());
			queryContextBuilder.setPropertyProvider( AzureResourceManagerPostgreSqlManagerConnectorConfig.POSTGRESQL_MANAGER.getPropertyName(),
					Main::createPostgreSqlManagers );
			queryContextBuilder.setProperty( "serverParameters", List.of("ssl_min_protocol_version", "authentication_timeout"));
			queryContextBuilder.setProperty( AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getPropertyName(), createGraphServiceClient());
//			queryContextBuilder.setProperty( AwsConnectorConfig.ACCOUNT.getPropertyName(), createAwsAccount() );
//			queryContextBuilder.setProperty( GoogleDirectoryConnectorConfig.GOOGLE_DIRECTORY_SERVICE.getPropertyName(), createGoogleDirectory() );
//			queryContextBuilder.setProperty( GoogleDriveConnectorConfig.GOOGLE_DRIVE_SERVICE.getPropertyName(), createGoogleDrive() );
//			queryContextBuilder.setProperty( GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getPropertyName(), createGcpCredentialsProvider() );
//			queryContextBuilder.setProperty( JiraDatacenterConnectorConfig.API_CLIENT.getPropertyName(), createJiraDatacenterApiClient());
//			queryContextBuilder.setProperty( JiraCloudConnectorConfig.API_CLIENT.getPropertyName(), createJiraCloudApiClient());
			queryContextBuilder.setProperty( EntityViewConnectorConfig.ENTITY_VIEW_MANAGER.getPropertyName(), evm );
			queryContextBuilder.setProperty( GitlabConnectorConfig.GITLAB_API.getPropertyName(), createGitlabApi());
			queryContextBuilder.setProperty( GitlabGraphQlConnectorConfig.GITLAB_GRAPHQL_CLIENT.getPropertyName(), createGitlabGraphQLClient());
//            queryContextBuilder.setProperty(KandjiConnectorConfig.API_CLIENT.getPropertyName(), createKandjiApiClient());
//            queryContextBuilder.setProperty(GithubConnectorConfig.GITHUB.getPropertyName(), createGithub());
			queryContextBuilder.setProperty( GitHubConnectorConfig.GITHUB_GRAPHQL_CLIENT.getPropertyName(), createGitHubGraphQLClient());
//            queryContextBuilder.setProperty(com.blazebit.query.connector.github.v0314.GithubConnectorConfig.API_CLIENT.getPropertyName(), createGitHubApiClient());

			// Azure Resource manager
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceVirtualMachine.class, "AzureVirtualMachine" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceStorageAccount.class, "AzureStorageAccount" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceBlobServiceProperties.class,
					"AzureBlobServiceProperties" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceManagedCluster.class, "AzureManagedCluster" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceVirtualNetwork.class, "AzureVirtualNetwork" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceVault.class, "AzureVault" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourcePostgreSqlFlexibleServer.class,"AzurePostgreSqlFlexibleServer" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourcePostgreSqlFlexibleServerBackup.class,"AzurePostgreSqlFlexibleServerBackup" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourcePostgreSqlFlexibleServerWithParameters.class,"AzurePostgreSqlFlexibleServerWithParameters" );

			// Azure Graph
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphUser.class, "AzureUser" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphConditionalAccessPolicy.class,
					"AzureConditionalAccessPolicy" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphApplication.class, "AzureApplication" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphManagedDevice.class, "AzureManagedDevice" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphOrganization.class, "AzureOrganization" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphServicePlanInfo.class, "AzureAvailableServicePlan" );

			// IAM
			queryContextBuilder.registerSchemaObjectAlias( AwsUser.class, "AwsUser" );
			queryContextBuilder.registerSchemaObjectAlias( AwsPasswordPolicy.class, "AwsIamPasswordPolicy" );
			queryContextBuilder.registerSchemaObjectAlias( AwsMFADevice.class, "AwsMFADevice" );
			queryContextBuilder.registerSchemaObjectAlias( AccountSummary.class, "AwsIamAccountSummary" );
			queryContextBuilder.registerSchemaObjectAlias( AccessKeyMetaDataLastUsed.class,
					"AwsAccessKeyMetaDataLastUsed" );

			// EC2
			queryContextBuilder.registerSchemaObjectAlias( AwsInstance.class, "AwsInstance" );
			queryContextBuilder.registerSchemaObjectAlias( AwsVolume.class, "AwsVolume" );
			queryContextBuilder.registerSchemaObjectAlias( AwsVpc.class, "AwsVpc" );
			queryContextBuilder.registerSchemaObjectAlias( AwsSecurityGroup.class, "AwsSecurityGroup" );
			queryContextBuilder.registerSchemaObjectAlias( AwsNetworkAcl.class, "AwsNetworkAcl" );
			// RDS
			queryContextBuilder.registerSchemaObjectAlias( AwsDBInstance.class, "AwsDBInstance" );
			// EFS
			queryContextBuilder.registerSchemaObjectAlias( AwsFileSystem.class, "AwsFileSystem" );
			// ECR
			queryContextBuilder.registerSchemaObjectAlias( AwsRepository.class, "AwsRepository" );
			// ECS
			queryContextBuilder.registerSchemaObjectAlias( AwsCluster.class, "AwsCluster" );
			// ELB
			queryContextBuilder.registerSchemaObjectAlias( AwsLoadBalancer.class, "AwsLoadBalancer" );
			// Lambda
			queryContextBuilder.registerSchemaObjectAlias( AwsFunction.class, "AwsFunction" );
			// Route53
			queryContextBuilder.registerSchemaObjectAlias( AwsHostedZone.class, "AwsHostedZone" );
			queryContextBuilder.registerSchemaObjectAlias( AwsHealthCheck.class, "AwsHealthCheck" );
			// S3
			queryContextBuilder.registerSchemaObjectAlias( AwsBucket.class, "AwsBucket" );

			// Gitlab
			queryContextBuilder.registerSchemaObjectAlias( Project.class, "GitlabProject" );
			queryContextBuilder.registerSchemaObjectAlias( Group.class, "GitlabGroup" );
			queryContextBuilder.registerSchemaObjectAlias( ProjectMember.class, "GitlabProjectMember" );
			queryContextBuilder.registerSchemaObjectAlias( GroupMember.class, "GitlabGroupMember" );
			queryContextBuilder.registerSchemaObjectAlias( org.gitlab4j.api.models.User.class, "GitlabUser" );
			queryContextBuilder.registerSchemaObjectAlias( ProjectProtectedBranch.class,
					"GitlabProjectProtectedBranch" );
			queryContextBuilder.registerSchemaObjectAlias( GitlabUser.class, "GitlabGraphQlUser" );
			queryContextBuilder.registerSchemaObjectAlias( GitlabGroup.class, "GitlabGraphQlGroup" );
			queryContextBuilder.registerSchemaObjectAlias( GitlabProject.class, "GitlabGraphQlProject" );
			queryContextBuilder.registerSchemaObjectAlias( GitlabMergeRequest.class, "GitlabGraphQlMergeRequest" );

			// GitHub
			queryContextBuilder.registerSchemaObjectAlias( GHOrganization.class, "GitHubOrganization" );
			queryContextBuilder.registerSchemaObjectAlias( GHRepository.class, "GitHubRepository" );
			queryContextBuilder.registerSchemaObjectAlias( GHBranch.class, "GitHubBranch" );
			queryContextBuilder.registerSchemaObjectAlias( GHProject.class, "GitHubProject" );
			queryContextBuilder.registerSchemaObjectAlias( GHTeam.class, "GitHubTeam" );
			queryContextBuilder.registerSchemaObjectAlias( GitHubOrganization.class, "GraphQlGitHubOrganization" );
			queryContextBuilder.registerSchemaObjectAlias( GitHubRepository.class, "GraphQlGitHubRepository" );
			queryContextBuilder.registerSchemaObjectAlias( GitHubRuleset.class, "GraphQlGitHubRuleset" );
			queryContextBuilder.registerSchemaObjectAlias( GitHubBranchProtectionRule.class, "GraphQlGitHubBranchProtectionRule" );
			queryContextBuilder.registerSchemaObjectAlias( GitHubPullRequest.class, "GraphQlGitHubPullRequest" );

			// GitHub OpenAPI
			queryContextBuilder.registerSchemaObjectAlias( OrganizationSimple.class, "OpenAPIGitHubOrganization" );
			queryContextBuilder.registerSchemaObjectAlias(
					com.blazebit.query.connector.github.v0314.model.Repository.class, "OpenAPIGitHubRepository" );
			queryContextBuilder.registerSchemaObjectAlias( ShortBranch.class, "OpenAPIGitHubBranch" );
			queryContextBuilder.registerSchemaObjectAlias(
					com.blazebit.query.connector.github.v0314.model.Project.class, "OpenAPIGitHubProject" );
			queryContextBuilder.registerSchemaObjectAlias( Team.class, "OpenAPIGitHubTeam" );

			// Kandji
			queryContextBuilder.registerSchemaObjectAlias( ListDevices200ResponseInner.class, "KandjiDevice" );
			queryContextBuilder.registerSchemaObjectAlias( DeviceParameter.class, "KandjiDeviceParameter" );
			queryContextBuilder.registerSchemaObjectAlias( GetDeviceDetails200Response.class, "KandjiDeviceDetail" );

			// Google Workspace
			queryContextBuilder.registerSchemaObjectAlias( com.google.api.services.directory.model.User.class, "GoogleUser" );
			queryContextBuilder.registerSchemaObjectAlias( com.google.api.services.directory.model.Group.class, "GoogleGroup" );
			queryContextBuilder.registerSchemaObjectAlias( Member.class, "GoogleMember" );
			queryContextBuilder.registerSchemaObjectAlias( Role.class, "GoogleRole" );
			queryContextBuilder.registerSchemaObjectAlias( com.google.api.services.drive.model.Drive.class, "GoogleDrive" );

			// GCP
			queryContextBuilder.registerSchemaObjectAlias( com.google.cloud.compute.v1.Instance.class, "GcpInstance" );
			queryContextBuilder.registerSchemaObjectAlias( com.google.iam.admin.v1.Role.class, "GcpIamRole" );
			queryContextBuilder.registerSchemaObjectAlias( ServiceAccount.class, "GcpIamServiceAccount" );
			queryContextBuilder.registerSchemaObjectAlias( IamPolicySearchResult.class, "GcpIamPolicy" );
			queryContextBuilder.registerSchemaObjectAlias( com.google.cloud.resourcemanager.v3.Project.class, "GcpProject" );
			queryContextBuilder.registerSchemaObjectAlias( com.google.storage.v2.Bucket.class, "GcpBucket" );

			// Jira Datacenter
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.datacenter.model.ProjectBean.class, "JiraDatacenterProject" );
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.datacenter.model.UserBean.class, "JiraDatacenterUser" );
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.datacenter.model.GroupSuggestionBean.class, "JiraDatacenterGroup" );
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.datacenter.GroupMember.class, "JiraDatacenterMember" );
			queryContextBuilder.registerSchemaObjectAlias( PermissionGrantBean.class, "JiraDatacenterPermission" );

			// Jira Cloud
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.cloud.model.Project.class, "JiraCloudProject" );
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.cloud.model.User.class, "JiraCloudUser" );
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.cloud.model.FoundGroup.class, "JiraCloudGroup" );
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.cloud.GroupMember.class, "JiraCloudMember" );
			queryContextBuilder.registerSchemaObjectAlias( UserPermission.class, "JiraCloudPermission" );

			try (QueryContext queryContext = queryContextBuilder.build()) {
				try (EntityManager em = emf.createEntityManager();
					QuerySession session = queryContext.createSession(
							Map.of( EntityViewConnectorConfig.ENTITY_MANAGER.getPropertyName(), em ) )) {
//					testJiraDatacenter( session );
//					testJiraCloud( session );
//					testGcp( session );
//					testGoogleWorkspace( session );
//					testAws( session );
					testGitlab( session );
//					testGitHub( session );
//					testGitHubOpenAPI( session );
//					testKandji( session );
//					testEntityView( session );
//					testAzureGraph( session );
					testAzureResourceManager( session );
				}
			}
		}
	}

	private static void testAws(QuerySession session) {
		// IAM
		TypedQuery<Object[]> awsUserQuery = session.createQuery(
				"select u.* from AwsUser u" );
		List<Object[]> awsUserResult = awsUserQuery.getResultList();
		System.out.println( "AwsUsers" );
		print( awsUserResult );

		TypedQuery<Object[]> AwsAccessKeyMetaDataLastUsedQuery = session.createQuery(
				"select a.* from AwsAccessKeyMetaDataLastUsed a" );
		List<Object[]> awsAccessKeyMetaDataLastUsed = AwsAccessKeyMetaDataLastUsedQuery.getResultList();
		System.out.println( "AwsAccessKeyMetaDataLastUsed" );
		print( awsAccessKeyMetaDataLastUsed );

		TypedQuery<Object[]> awsPasswordPolicyQuery = session.createQuery(
				"select p.* from AwsIamPasswordPolicy p" );
		List<Object[]> awsPasswordPolicyResult = awsPasswordPolicyQuery.getResultList();
		System.out.println( "AwsPasswordPolicy" );
		print( awsPasswordPolicyResult );

		TypedQuery<Object[]> awsMFADeviceQuery = session.createQuery(
				"select d.* from AwsMFADevice d" );
		List<Object[]> awsMFADeviceResult = awsMFADeviceQuery.getResultList();
		System.out.println( "AwsMFADevices" );
		print( awsMFADeviceResult );

		TypedQuery<Object[]> awsAccountSummaryQuery = session.createQuery(
				"select a.* from AwsIamAccountSummary a" );
		List<Object[]> awsAccountSummaryResult = awsAccountSummaryQuery.getResultList();
		System.out.println( "AwsAccountSummary" );
		print( awsAccountSummaryResult );

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
		System.out.println( "GitlabProjects" );
		print( gitlabProjectResult );
		TypedQuery<Object[]> gitlabGroupQuery = session.createQuery(
				"select p.* from GitlabGroup p" );
		List<Object[]> gitlabGroupResult = gitlabGroupQuery.getResultList();
		System.out.println( "GitlabGroups" );
		print( gitlabGroupResult );
		TypedQuery<Object[]> gitlabProjectMemberQuery = session.createQuery(
				"select p.* from GitlabProjectMember p" );
		List<Object[]> gitlabProjectMemberResult = gitlabProjectMemberQuery.getResultList();
		System.out.println( "GitlabProjectMembers" );
		print( gitlabProjectMemberResult );
		TypedQuery<Object[]> gitlabGroupMemberQuery = session.createQuery(
				"select p.* from GitlabGroupMember p" );
		List<Object[]> gitlabGroupMemberResult = gitlabGroupMemberQuery.getResultList();
		System.out.println( "GitlabGroupMembers" );
		print( gitlabGroupMemberResult );
		TypedQuery<Object[]> gitlabUserQuery = session.createQuery(
				"select p.* from GitlabUser p" );
		List<Object[]> gitlabUserResult = gitlabUserQuery.getResultList();
		System.out.println( "GitlabUsers" );
		print( gitlabUserResult );
		TypedQuery<Object[]> gitlabProtectedBranchQuery = session.createQuery(
				"select p.* from GitlabProjectProtectedBranch p" );
		List<Object[]> gitlabProtectedBranchResult = gitlabProtectedBranchQuery.getResultList();
		System.out.println( "GitlabProtectedBranches" );
		print( gitlabProtectedBranchResult );

		List<Object[]> gitlabGraphqlUserResult = session.createQuery(
				"select g.* from GitlabGraphQlUser g" ).getResultList();
		System.out.println( "GitlabGraphQlUsers" );
		print( gitlabGraphqlUserResult );

		List<Object[]> gitlabGraphqlMergeRequestResult = session.createQuery(
				"select g.* from GitlabGraphQlMergeRequest g" ).getResultList();
		System.out.println( "GitlabGraphQlMergeRequests" );
		print( gitlabGraphqlMergeRequestResult );
	}

	private static void testGitHub(QuerySession session) {
		TypedQuery<Object[]> gitHubOrganizationQuery = session.createQuery(
				"select p.* from GitHubOrganization p" );
		List<Object[]> gitHubOrganizationResult = gitHubOrganizationQuery.getResultList();
		System.out.println( "GitHubOrganizations" );
		print( gitHubOrganizationResult );
		TypedQuery<Object[]> gitHubRepositoryQuery = session.createQuery(
				"select p.* from GitHubRepository p" );
		List<Object[]> gitHubRepositoryResult = gitHubRepositoryQuery.getResultList();
		System.out.println( "GitHubRepositories" );
		print( gitHubRepositoryResult );
		TypedQuery<Object[]> gitHubBranchQuery = session.createQuery(
				"select p.* from GitHubBranch p" );
		List<Object[]> gitHubBranchResult = gitHubBranchQuery.getResultList();
		System.out.println( "GitHubBranches" );
		print( gitHubBranchResult );
		TypedQuery<Object[]> gitHubProjectQuery = session.createQuery(
				"select p.* from GitHubProject p" );
		List<Object[]> gitHubProjectResult = gitHubProjectQuery.getResultList();
		System.out.println( "GitHubProjects" );
		print( gitHubProjectResult );
		TypedQuery<Object[]> gitHubTeamQuery = session.createQuery(
				"select p.* from GitHubTeam p" );
		List<Object[]> gitHubTeamResult = gitHubTeamQuery.getResultList();
		System.out.println( "GitHubTeams" );
		print( gitHubTeamResult );
		TypedQuery<Object[]> gitHubGraphQlOrganizationQuery = session.createQuery(
				"select o.* from GraphQlGitHubOrganization o" );
		List<Object[]> gitHubGraphQlOrganizationResult = gitHubGraphQlOrganizationQuery.getResultList();
		System.out.println( "GitHubOrganizations" );
		print( gitHubGraphQlOrganizationResult );
		TypedQuery<Object[]> gitHubGraphQlRepositoryQuery = session.createQuery(
				"select r.* from GraphQlGitHubRepository r" );
		List<Object[]> gitHubGraphQlRepositoryResult = gitHubGraphQlRepositoryQuery.getResultList();
		System.out.println( "GitHubRepositories" );
		print( gitHubGraphQlRepositoryResult );
		TypedQuery<Object[]> gitHubGraphQlRulesetQuery = session.createQuery(
				"select r.* from GraphQlGitHubRuleset r" );
		List<Object[]> gitHubGraphQlRulesetResult = gitHubGraphQlRulesetQuery.getResultList();
		System.out.println( "GitHubRulesets" );
		print( gitHubGraphQlRulesetResult );
		TypedQuery<Object[]> gitHubGraphQlPullRequestQuery = session.createQuery(
				"select p.* from GraphQlGitHubPullRequest p" );
		List<Object[]> gitHubGraphQlPullRequestResult = gitHubGraphQlPullRequestQuery.getResultList();
		System.out.println( "GitHubPullRequests" );
		print( gitHubGraphQlPullRequestResult );
		TypedQuery<Object[]> gitHubGraphQlBranchProtectionRuleQuery = session.createQuery(
				"select r.* from GraphQlGitHubBranchProtectionRule r" );
		List<Object[]> gitHubGraphQlBranchProtectionRuleResult = gitHubGraphQlBranchProtectionRuleQuery.getResultList();
		System.out.println( "GitHubBranchProtectionRules" );
		print( gitHubGraphQlBranchProtectionRuleResult );
	}

	private static void testGitHubOpenAPI(QuerySession session) {
		TypedQuery<Object[]> gitHubOrganizationQuery = session.createQuery(
				"select p.* from OpenAPIGitHubOrganization p" );
		List<Object[]> gitHubOrganizationResult = gitHubOrganizationQuery.getResultList();
		System.out.println( "OpenAPIGitHubOrganizations" );
		print( gitHubOrganizationResult );
		TypedQuery<Object[]> gitHubRepositoryQuery = session.createQuery(
				"select p.* from OpenAPIGitHubRepository p" );
		List<Object[]> gitHubRepositoryResult = gitHubRepositoryQuery.getResultList();
		System.out.println( "OpenAPIGitHubRepositories" );
		print( gitHubRepositoryResult );
		TypedQuery<Object[]> gitHubBranchQuery = session.createQuery(
				"select p.* from OpenAPIGitHubBranch p" );
		List<Object[]> gitHubBranchResult = gitHubBranchQuery.getResultList();
		System.out.println( "OpenAPIGitHubBranches" );
		print( gitHubBranchResult );
		TypedQuery<Object[]> gitHubProjectQuery = session.createQuery(
				"select p.* from OpenAPIGitHubProject p" );
		List<Object[]> gitHubProjectResult = gitHubProjectQuery.getResultList();
		System.out.println( "OpenAPIGitHubProjects" );
		print( gitHubProjectResult );
		TypedQuery<Object[]> gitHubTeamQuery = session.createQuery(
				"select p.* from OpenAPIGitHubTeam p" );
		List<Object[]> gitHubTeamResult = gitHubTeamQuery.getResultList();
		System.out.println( "OpenAPIGitHubTeams" );
		print( gitHubTeamResult );
	}

	private static void testKandji(QuerySession session) {
		TypedQuery<Object[]> kandjiDeviceQuery = session.createQuery(
				"select p.* from KandjiDevice p" );
		List<Object[]> kandjiDeviceResult = kandjiDeviceQuery.getResultList();
		System.out.println( "KandjiDevice" );
		print( kandjiDeviceResult );
		TypedQuery<Object[]> kandjiDeviceParameterQuery = session.createQuery(
				"select p.* from KandjiDeviceParameter p" );
		List<Object[]> kandjiDeviceParameterResult = kandjiDeviceParameterQuery.getResultList();
		System.out.println( "KandjiDeviceParameter" );
		print( kandjiDeviceParameterResult );
		TypedQuery<Object[]> kandjiDeviceDetailQuery = session.createQuery(
				"select p.* from KandjiDeviceDetail p" );
		List<Object[]> kandjiDeviceDetailResult = kandjiDeviceDetailQuery.getResultList();
		System.out.println( "KandjiDeviceDetail" );
		print( kandjiDeviceDetailResult );
	}

	private static void testGoogleWorkspace(QuerySession session) {
		TypedQuery<Object[]> userQuery = session.createQuery(
				"select u.* from GoogleUser u" );
		List<Object[]> userResult = userQuery.getResultList();
		System.out.println( "User" );
		print( userResult );
		TypedQuery<Object[]> groupQuery = session.createQuery(
				"select u.* from GoogleGroup u" );
		List<Object[]> groupResult = groupQuery.getResultList();
		System.out.println( "Group" );
		print( groupResult );
		TypedQuery<Object[]> memberQuery = session.createQuery(
				"select u.* from GoogleMember u" );
		List<Object[]> memberResult = memberQuery.getResultList();
		System.out.println( "Member" );
		print( memberResult );
		TypedQuery<Object[]> roleQuery = session.createQuery(
				"select u.* from GoogleRole u" );
		List<Object[]> roleResult = roleQuery.getResultList();
		System.out.println( "Role" );
		print( roleResult );
		TypedQuery<Object[]> roleAssignmentQuery = session.createQuery(
				"select u.* from GoogleRole u" );
		List<Object[]> roleAssignmentResult = roleAssignmentQuery.getResultList();
		System.out.println( "Role assignment" );
		print( roleAssignmentResult );

		TypedQuery<Object[]> driveQuery = session.createQuery(
				"select u.* from GoogleDrive u" );
		List<Object[]> driveResult = driveQuery.getResultList();
		System.out.println( "Drive" );
		print( driveResult );
	}

	private static void testGcp(QuerySession session) {
		TypedQuery<Object[]> projectQuery = session.createQuery(
				"select i.* from GcpProject i" );
		List<Object[]> projectResult = projectQuery.getResultList();
		System.out.println( "Project" );
		print( projectResult );

		TypedQuery<Object[]> instanceQuery = session.createQuery(
				"select i.* from GcpInstance i" );
		List<Object[]> instanceResult = instanceQuery.getResultList();
		System.out.println( "Instance" );
		print( instanceResult );

		TypedQuery<Object[]> policyQuery = session.createQuery(
				"select i.* from GcpIamPolicy i" );
		List<Object[]> policyResult = policyQuery.getResultList();
		System.out.println( "Policy" );
		print( policyResult );

		TypedQuery<Object[]> roleQuery = session.createQuery(
				"select i.* from GcpIamRole i" );
		List<Object[]> roleResult = roleQuery.getResultList();
		System.out.println( "Role" );
		print( roleResult );

		TypedQuery<Object[]> serviceAccountQuery = session.createQuery(
				"select i.* from GcpIamServiceAccount i" );
		List<Object[]> serviceAccountResult = serviceAccountQuery.getResultList();
		System.out.println( "Service Account" );
		print( serviceAccountResult );

		TypedQuery<Object[]> bucketQuery = session.createQuery(
				"select i.* from GcpBucket i" );
		List<Object[]> bucketResult = bucketQuery.getResultList();
		System.out.println( "Bucket" );
		print( bucketResult );
	}

	private static void testJiraDatacenter(QuerySession session) {
		TypedQuery<Object[]> driveQuery = session.createQuery(
				"select u.* from JiraDatacenterProject u" );
		List<Object[]> driveResult = driveQuery.getResultList();
		System.out.println( "Project" );
		print( driveResult );

		TypedQuery<Object[]> userQuery = session.createQuery(
				"select u.* from JiraDatacenterUser u" );
		List<Object[]> userResult = userQuery.getResultList();
		System.out.println( "User" );
		print( userResult );

		TypedQuery<Object[]> groupQuery = session.createQuery(
				"select u.* from JiraDatacenterGroup u" );
		List<Object[]> groupResult = groupQuery.getResultList();
		System.out.println( "Group" );
		print( groupResult );

		TypedQuery<Object[]> memberQuery = session.createQuery(
				"select u.* from JiraDatacenterMember u" );
		List<Object[]> memberResult = memberQuery.getResultList();
		System.out.println( "Group" );
		print( memberResult );

		TypedQuery<Object[]> permissionQuery = session.createQuery(
				"select u.* from JiraDatacenterPermission u" );
		List<Object[]> permissionResult = permissionQuery.getResultList();
		System.out.println( "Permission" );
		print( permissionResult );
	}

	private static void testJiraCloud(QuerySession session) {
		TypedQuery<Object[]> driveQuery = session.createQuery(
				"select u.* from JiraCloudProject u" );
		List<Object[]> driveResult = driveQuery.getResultList();
		System.out.println( "Project" );
		print( driveResult );

		TypedQuery<Object[]> userQuery = session.createQuery(
				"select u.* from JiraCloudUser u" );
		List<Object[]> userResult = userQuery.getResultList();
		System.out.println( "User" );
		print( userResult );

		TypedQuery<Object[]> groupQuery = session.createQuery(
				"select u.* from JiraCloudGroup u" );
		List<Object[]> groupResult = groupQuery.getResultList();
		System.out.println( "Group" );
		print( groupResult );

		TypedQuery<Object[]> memberQuery = session.createQuery(
				"select u.* from JiraCloudMember u" );
		List<Object[]> memberResult = memberQuery.getResultList();
		System.out.println( "Group" );
		print( memberResult );

		TypedQuery<Object[]> permissionQuery = session.createQuery(
				"select u.* from JiraCloudPermission u" );
		List<Object[]> permissionResult = permissionQuery.getResultList();
		System.out.println( "Permission" );
		print( permissionResult );
	}

	private static void testEntityView(QuerySession session) {
		TypedQuery<Object[]> entityViewQuery = session.createQuery(
				"select t.id, e.text1 from " + name( TestEntityView.class ) + " t, unnest(t.elements) e" );
		List<Object[]> entityViewResult = entityViewQuery.getResultList();
		print( entityViewResult, "id", "text1" );
	}

	private static void testAzureGraph(QuerySession session) {
		TypedQuery<Object[]> userQuery = session.createQuery(
				"select u.* from AzureUser u" );
		List<Object[]> userResult = userQuery.getResultList();
		System.out.println( "User" );
		print( userResult );

		TypedQuery<Object[]> conditionalAccessPolicyQuery = session.createQuery(
				"select c.* from AzureConditionalAccessPolicy c" );
		List<Object[]> conditionalAccessPolicyResult = conditionalAccessPolicyQuery.getResultList();
		System.out.println( "Conditional access policies" );
		print( conditionalAccessPolicyResult );

		TypedQuery<Object[]> applicationQuery = session.createQuery(
				"select a.* from AzureApplication a" );
		List<Object[]> applicationResult = applicationQuery.getResultList();
		System.out.println( "Applications" );
		print( applicationResult );

//		TypedQuery<Object[]> managedDevices = session.createQuery(
//				"select a.* from AzureManagedDevice a" );
//		List<Object[]> managedDevicesResult = managedDevices.getResultList();
//		System.out.println( "Managed Devices" );
//		print( managedDevicesResult );

		TypedQuery<Object[]> organizationQuery = session.createQuery(
				"select o.* from AzureOrganization o" );
		List<Object[]> organizationResult = organizationQuery.getResultList();
		System.out.println( "Organizations" );
		print( organizationResult );

//		TypedQuery<Object[]> servicePlanQuery = session.createQuery(
//				"select s.* from AzureAvailableServicePlan s" );
//		List<Object[]> subscribedSkuResult = servicePlanQuery.getResultList();
//		System.out.println( "Service plan" );
//		print( subscribedSkuResult );
	}

	private static void testAzureResourceManager(QuerySession session) {
		TypedQuery<Object[]> vmQuery1 = session.createQuery(
				"select vm.* from AzureVirtualMachine vm where vm.payload.storageProfile.osDisk.osType <> 'Linux'" );
		List<Object[]> vmResult1 = vmQuery1.getResultList();
		System.out.println( "Non Linux VMs" );
		print( vmResult1 );

		TypedQuery<Object[]> vmQuery2 = session.createQuery(
				"select vm.* from AzureVirtualMachine vm where vm.payload.osProfile.linuxConfiguration.disablePasswordAuthentication = false" );
		List<Object[]> vmResult2 = vmQuery2.getResultList();
		System.out.println( "VMs" );
		print( vmResult2 );

		TypedQuery<Object[]> storageAccountsQuery2 = session.createQuery(
				"select sa.* from AzureStorageAccount sa where exists (select 1 from AzureBlobServiceProperties bs where bs.payload.id like sa.payload.id || '/%' and bs.payload.versioningEnabled)" );
		List<Object[]> storageAccountsResult2 = storageAccountsQuery2.getResultList();
		System.out.println( "StorageAccounts" );
		print( storageAccountsResult2 );

		TypedQuery<Object[]> virtualNetworkQuery = session.createQuery(
				"select nm.* from AzureVirtualNetwork nm" );
		List<Object[]> virtualNetworkResult = virtualNetworkQuery.getResultList();
		System.out.println( "VirtualNetworks" );
		print( virtualNetworkResult );

		TypedQuery<Object[]> managedClusterQuery = session.createQuery(
				"select mc.* from AzureManagedCluster mc" );
		List<Object[]> managedClusterResult = managedClusterQuery.getResultList();
		System.out.println( "ManagedClusters" );
		print( managedClusterResult );

		TypedQuery<Object[]> vaultQuery = session.createQuery(
				"select v.* from AzureVault v" );
		List<Object[]> vaultResult = vaultQuery.getResultList();
		System.out.println( "Vaults" );
		print( vaultResult );

		TypedQuery<Object[]> postgreSqlFlexibleServerQuery = session.createQuery(
				"select s.* from AzurePostgreSqlFlexibleServer s" );
		List<Object[]> postgreSqlFlexibleServerResult = postgreSqlFlexibleServerQuery.getResultList();
		System.out.println( "PostgreSqlFlexibleServers" );
		print( postgreSqlFlexibleServerResult );

		TypedQuery<Object[]> postgreSqlFlexibleServerBackupQuery =
				session.createQuery("select b.* from AzurePostgreSqlFlexibleServerBackup b");
		List<Object[]> postgreSqlFlexibleServerBackupQueryResult =
				postgreSqlFlexibleServerBackupQuery.getResultList();
		System.out.println("PostgreSqlFlexibleServersBackups");
		print(postgreSqlFlexibleServerBackupQueryResult);

		TypedQuery<Object[]> postgreSqlFlexibleServerWithParametersQuery =
				session.createQuery("select s.* from AzurePostgreSqlFlexibleServerWithParameters s");
		List<Object[]> postgreSqlFlexibleServerWithParametersQueryResult =
				postgreSqlFlexibleServerWithParametersQuery.getResultList();
		System.out.println("PostgreSqlFlexibleServersWithParameters");
		print(postgreSqlFlexibleServerWithParametersQueryResult);
	}

	private static AwsConnectorConfig.Account createAwsAccount() {
		return new AwsConnectorConfig.Account(
				AWS_ACCOUNT_ID,
				Set.of( Region.of( AWS_REGION ) ),
				StaticCredentialsProvider.create(
						AwsBasicCredentials.create( AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY ) )
		);
	}

	private static GitLabApi createGitlabApi() {
		final GitLabApi gitLabApi = new GitLabApi( GITLAB_HOST, GITLAB_KEY );
		if ( !GITLAB_HOST.startsWith( "https://gitlab.com" ) ) {
			gitLabApi.setIgnoreCertificateErrors( true );
		}
		return gitLabApi;
	}

	private static GitlabGraphQlClient createGitlabGraphQLClient() {
		// Initialize GraphQL client with host and token
		return new GitlabGraphQlClient(GITLAB_HOST, GITLAB_KEY);
	}

	private static GitHubGraphQlClient createGitHubGraphQLClient() {
		// Initialize GraphQL client with host and token
		return new GitHubGraphQlClient(GITHUB_KEY);
	}

	private static GitHub createGithub() {
		try {
			return new GitHubBuilder().withOAuthToken( GITHUB_KEY ).build();
		}
		catch (IOException e) {
			throw new RuntimeException( e );
		}
	}

	private static com.blazebit.query.connector.github.v0314.invoker.ApiClient createGitHubApiClient() {
		com.blazebit.query.connector.github.v0314.invoker.ApiClient apiClient = new com.blazebit.query.connector.github.v0314.invoker.ApiClient();
		apiClient.addDefaultHeader( "Authorization", "Bearer " + GITHUB_KEY );
//        apiClient.getJSON().getMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return apiClient;
	}

	private static com.blazebit.query.connector.jira.datacenter.invoker.ApiClient createJiraDatacenterApiClient() {
		com.blazebit.query.connector.jira.datacenter.invoker.ApiClient apiClient = new com.blazebit.query.connector.jira.datacenter.invoker.ApiClient();
		apiClient.setBasePath( JIRA_DATACENTER_HOST );
		apiClient.addDefaultHeader( "Authorization", "Bearer " + JIRA_DATACENTER_TOKEN );
//        apiClient.getJSON().getMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return apiClient;
	}

	private static com.blazebit.query.connector.jira.cloud.invoker.ApiClient createJiraCloudApiClient() {
		com.blazebit.query.connector.jira.cloud.invoker.ApiClient apiClient = new com.blazebit.query.connector.jira.cloud.invoker.ApiClient();
		apiClient.setBasePath( JIRA_CLOUD_HOST );
		apiClient.addDefaultHeader( "Authorization", "Basic " + Base64.getEncoder().encodeToString( (JIRA_CLOUD_USER + ":" + JIRA_CLOUD_TOKEN ).getBytes( StandardCharsets.UTF_8 ) ) );
//        apiClient.getJSON().getMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return apiClient;
	}

	private static com.blazebit.query.connector.kandji.invoker.ApiClient createKandjiApiClient() {
		com.blazebit.query.connector.kandji.invoker.auth.HttpBearerAuth auth = new com.blazebit.query.connector.kandji.invoker.auth.HttpBearerAuth(
				"Bearer" );
		auth.setBearerToken( KANDJI_KEY );
		com.blazebit.query.connector.kandji.invoker.ApiClient apiClient = new com.blazebit.query.connector.kandji.invoker.ApiClient(
				Map.of( "bearerAuth", auth )
		);
		apiClient.setBasePath( KANDJI_BASE_PATH );
		apiClient.getJSON().getMapper().registerModule( new KandjiJavaTimeModule() );
//        apiClient.getJSON().getMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return apiClient;
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
			}
			else {
				sb.append( c );
			}
		}
		sb.append( '`' );
		return sb.toString();
	}

	private static void print(List<Object[]> tuples, String... columnHeaders) {
		if ( columnHeaders.length > 0 ) {
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
		for ( int i = 0; i < tuples.size(); i++ ) {
			Object[] tuple = tuples.get( i );
			if ( columnHeaders.length != 0 && tuple.length != columnHeaders.length ) {
				throw new IllegalArgumentException(
						"Inconsistent column header. Tuple length is " + tuple.length + " but only " + columnHeaders.length + " column header given" );
			}
			System.out.print( i + 1 );
			System.out.print( "\t" );
			for ( Object o : tuple ) {
				System.out.print( "| " );
				System.out.print( o );
				System.out.print( "\t" );
				System.out.print( "\t" );
			}
			System.out.println();
		}
	}

//    private static import com.blazebit.query.connector.azure.base.invoker.ApiClient createApiClient() {
//        String basePath = "https://login.microsoftonline.com/" + TENANT_ID;
//        OAuth oAuth = new OAuth(basePath, "/oauth2/v2.0/token")
//                .setCredentials(CLIENT_ID, CLIENT_SECRET, false)
//                // Default scope
//                .setScope("https://management.core.windows.net//.default");
//        import com.blazebit.query.connector.azure.base.invoker.ApiClient apiClient = new import com.blazebit.query.connector.azure.base.invoker.ApiClient(Map.of("azure_auth", oAuth));
//        apiClient.getJSON().getMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        return apiClient;
//    }

	private static List<AzureResourceManagerPostgreSqlManager> createPostgreSqlManagers(DataFetchContext context) {
		List<AzureResourceSubscription> azureResourceManagerSubscriptions = (List<AzureResourceSubscription>) context.getSession().getOrFetch( AzureResourceSubscription.class );
		List<AzureResourceManagerPostgreSqlManager> postgreSqlManagers = new ArrayList<>();

		for ( AzureResourceSubscription subscription : azureResourceManagerSubscriptions ) {
			AzureProfile profile = new AzureProfile( AZURE_TENANT_ID, subscription.getSubscriptionId(), AzureEnvironment.AZURE );
			ClientSecretCredential credentials = new ClientSecretCredentialBuilder()
					.clientId( AZURE_CLIENT_ID )
					.clientSecret( AZURE_CLIENT_SECRET )
					.tenantId( AZURE_TENANT_ID )
					.build();
			postgreSqlManagers.add(new AzureResourceManagerPostgreSqlManager( AZURE_TENANT_ID, subscription.getSubscriptionId(), PostgreSqlManager.authenticate( credentials, profile )  ));
		}
		return postgreSqlManagers;
	}

	private static AzureResourceManager createResourceManager() {
		AzureProfile profile = new AzureProfile( AZURE_TENANT_ID, null, AzureEnvironment.AZURE );
		ClientSecretCredential credentials = new ClientSecretCredentialBuilder()
				.clientId( AZURE_CLIENT_ID )
				.clientSecret( AZURE_CLIENT_SECRET )
				.tenantId( AZURE_TENANT_ID )
				.build();
		return AzureResourceManager.authenticate( credentials, profile ).withDefaultSubscription();
	}

	private static AzureGraphClientAccessor createGraphServiceClient() {
		ClientSecretCredential credentials = new ClientSecretCredentialBuilder()
				.clientId( AZURE_CLIENT_ID )
				.clientSecret( AZURE_CLIENT_SECRET )
				.tenantId( AZURE_TENANT_ID )
				.build();
		// Default scope
		return AzureGraphClientAccessor.create(
				AZURE_TENANT_ID,
				new GraphServiceClient( credentials, "https://graph.microsoft.com/.default" )
		);
	}

	private static Directory createGoogleDirectory() {
		try {
			final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
			final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			ArrayList<String> scopeList = new ArrayList<>();
			scopeList.add(DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY);
			scopeList.add(DirectoryScopes.ADMIN_DIRECTORY_GROUP_READONLY);
			scopeList.add(DirectoryScopes.ADMIN_DIRECTORY_GROUP_MEMBER_READONLY);
			scopeList.add(DirectoryScopes.ADMIN_DIRECTORY_ROLEMANAGEMENT_READONLY);
			return new Directory.Builder(
					httpTransport,
					jsonFactory,
					new HttpCredentialsAdapter(
							ServiceAccountCredentials.newBuilder()
									.setClientId( GOOGLE_WORKSPACE_CLIENT_ID )
									.setClientEmail( GOOGLE_WORKSPACE_CLIENT_EMAIL )
									.setProjectId( GOOGLE_WORKSPACE_PROJECT_ID )
									.setPrivateKeyId( GOOGLE_WORKSPACE_PRIVATE_KEY_ID )
									.setPrivateKeyString( GOOGLE_WORKSPACE_PRIVATE_KEY )
									.setServiceAccountUser( GOOGLE_WORKSPACE_SERVICE_ACCOUNT_USER )
									.setScopes( scopeList )
									.build()
					)
			).setApplicationName( "blaze-query-test" ).build();
		}
		catch (Exception e) {
			throw new RuntimeException( e );
		}
	}

	private static Drive createGoogleDrive() {
		try {
			final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
			final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			ArrayList<String> scopeList = new ArrayList<>();
			scopeList.add(DriveScopes.DRIVE_READONLY);
			return new Drive.Builder(
					httpTransport,
					jsonFactory,
					new HttpCredentialsAdapter(
							ServiceAccountCredentials.newBuilder()
									.setClientId( GOOGLE_WORKSPACE_CLIENT_ID )
									.setClientEmail( GOOGLE_WORKSPACE_CLIENT_EMAIL )
									.setProjectId( GOOGLE_WORKSPACE_PROJECT_ID )
									.setPrivateKeyId( GOOGLE_WORKSPACE_PRIVATE_KEY_ID )
									.setPrivateKeyString( GOOGLE_WORKSPACE_PRIVATE_KEY )
									.setServiceAccountUser( GOOGLE_WORKSPACE_SERVICE_ACCOUNT_USER )
									.setScopes( scopeList )
									.build()
					)
			).setApplicationName( "blaze-query-test" ).build();
		}
		catch (Exception e) {
			throw new RuntimeException( e );
		}
	}

	private static CredentialsProvider createGcpCredentialsProvider() {
		try {
			return FixedCredentialsProvider.create(
					ServiceAccountCredentials.newBuilder()
							.setClientId( GOOGLE_WORKSPACE_CLIENT_ID )
							.setClientEmail( GOOGLE_WORKSPACE_CLIENT_EMAIL )
							.setProjectId( GOOGLE_WORKSPACE_PROJECT_ID )
							.setPrivateKeyId( GOOGLE_WORKSPACE_PRIVATE_KEY_ID )
							.setPrivateKeyString( GOOGLE_WORKSPACE_PRIVATE_KEY )
							.build()
			);
		}
		catch (Exception e) {
			throw new RuntimeException( e );
		}
	}

}
