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
import com.blazebit.query.connector.aws.ec2.AwsEc2Address;
import com.blazebit.query.connector.aws.ec2.AwsEc2ClientVpnEndpoint;
import com.blazebit.query.connector.aws.ec2.AwsEc2CustomerGateway;
import com.blazebit.query.connector.aws.ec2.AwsEc2DhcpOptions;
import com.blazebit.query.connector.aws.ec2.AwsEc2EbsEncryptionByDefault;
import com.blazebit.query.connector.aws.ec2.AwsEc2FlowLogs;
import com.blazebit.query.connector.aws.ec2.AwsEc2Instance;
import com.blazebit.query.connector.aws.ec2.AwsEc2InstanceStatus;
import com.blazebit.query.connector.aws.ec2.AwsEc2InternetGateway;
import com.blazebit.query.connector.aws.ec2.AwsEc2LaunchTemplate;
import com.blazebit.query.connector.aws.ec2.AwsEc2LaunchTemplateVersion;
import com.blazebit.query.connector.aws.ec2.AwsEc2ManagedPrefixList;
import com.blazebit.query.connector.aws.ec2.AwsEc2NatGateway;
import com.blazebit.query.connector.aws.ec2.AwsEc2NetworkAcl;
import com.blazebit.query.connector.aws.ec2.AwsEc2NetworkAclEntry;
import com.blazebit.query.connector.aws.ec2.AwsEc2NetworkInterface;
import com.blazebit.query.connector.aws.ec2.AwsEc2RouteTable;
import com.blazebit.query.connector.aws.ec2.AwsEc2SecurityGroup;
import com.blazebit.query.connector.aws.ec2.AwsEc2SecurityGroupIpPermission;
import com.blazebit.query.connector.aws.ec2.AwsEc2Snapshot;
import com.blazebit.query.connector.aws.ec2.AwsEc2SnapshotAttribute;
import com.blazebit.query.connector.aws.ec2.AwsEc2SpotFleetRequest;
import com.blazebit.query.connector.aws.ec2.AwsEc2SpotFleetRequestLaunchSpecification;
import com.blazebit.query.connector.aws.ec2.AwsEc2Subnet;
import com.blazebit.query.connector.aws.ec2.AwsEc2TrafficMirrorFilter;
import com.blazebit.query.connector.aws.ec2.AwsEc2TrafficMirrorSession;
import com.blazebit.query.connector.aws.ec2.AwsEc2TrafficMirrorTarget;
import com.blazebit.query.connector.aws.ec2.AwsEc2TransitGateway;
import com.blazebit.query.connector.aws.ec2.AwsEc2TransitGatewayAttachment;
import com.blazebit.query.connector.aws.ec2.AwsEc2TransitGatewayRouteTable;
import com.blazebit.query.connector.aws.ec2.AwsEc2Volume;
import com.blazebit.query.connector.aws.ec2.AwsEc2Vpc;
import com.blazebit.query.connector.aws.ec2.AwsEc2VpcBlockPublicAccessOptions;
import com.blazebit.query.connector.aws.ec2.AwsEc2VpcEndpoint;
import com.blazebit.query.connector.aws.ec2.AwsEc2VpcEndpointService;
import com.blazebit.query.connector.aws.ec2.AwsEc2VpcPeeringConnection;
import com.blazebit.query.connector.aws.ec2.AwsEc2VpnConnection;
import com.blazebit.query.connector.aws.ec2.AwsEc2VpnConnectionTunnelOption;
import com.blazebit.query.connector.aws.ec2.AwsEc2VpnGateway;
import com.blazebit.query.connector.aws.ecr.AwsRepository;
import com.blazebit.query.connector.aws.ecs.AwsEcsCluster;
import com.blazebit.query.connector.aws.ecs.AwsEcsContainerDefinition;
import com.blazebit.query.connector.aws.ecs.AwsEcsService;
import com.blazebit.query.connector.aws.ecs.AwsEcsTaskDefinition;
import com.blazebit.query.connector.aws.ecs.AwsEcsTaskSet;
import com.blazebit.query.connector.aws.efs.AwsFileSystem;
import com.blazebit.query.connector.aws.elb.AwsLoadBalancer;
import com.blazebit.query.connector.aws.accessanalyzer.AwsAccessAnalyzerAnalyzer;
import com.blazebit.query.connector.aws.iam.AwsIamAccessKeyMetaDataLastUsed;
import com.blazebit.query.connector.aws.iam.AwsIamAccountSummary;
import com.blazebit.query.connector.aws.iam.AwsIamGroup;
import com.blazebit.query.connector.aws.iam.AwsIamGroupAttachedPolicy;
import com.blazebit.query.connector.aws.iam.AwsIamGroupInlinePolicy;
import com.blazebit.query.connector.aws.iam.AwsIamLoginProfile;
import com.blazebit.query.connector.aws.iam.AwsIamMfaDevice;
import com.blazebit.query.connector.aws.iam.AwsIamPasswordPolicy;
import com.blazebit.query.connector.aws.iam.AwsIamPolicyVersion;
import com.blazebit.query.connector.aws.iam.AwsIamRole;
import com.blazebit.query.connector.aws.iam.AwsIamRoleAttachedPolicy;
import com.blazebit.query.connector.aws.iam.AwsIamRoleInlinePolicy;
import com.blazebit.query.connector.aws.iam.AwsIamServerCertificate;
import com.blazebit.query.connector.aws.iam.AwsIamUser;
import com.blazebit.query.connector.aws.iam.AwsIamUserAttachedPolicy;
import com.blazebit.query.connector.aws.iam.AwsIamUserInlinePolicy;
import com.blazebit.query.connector.aws.iam.AwsIamVirtualMfaDevice;
import com.blazebit.query.connector.aws.kms.AwsKmsKey;
import com.blazebit.query.connector.aws.kms.AwsKmsKeyAlias;
import com.blazebit.query.connector.aws.kms.AwsKmsKeyPolicy;
import com.blazebit.query.connector.aws.kms.AwsKmsKeyRotationStatus;
import com.blazebit.query.connector.aws.secretsmanager.AwsSecretsManagerSecret;
import com.blazebit.query.connector.aws.cloudwatch.AwsCloudWatchCompositeAlarm;
import com.blazebit.query.connector.aws.cloudwatch.AwsCloudWatchMetricAlarm;
import com.blazebit.query.connector.aws.cloudwatchlogs.AwsCloudWatchLogsLogGroup;
import com.blazebit.query.connector.aws.cloudwatchlogs.AwsCloudWatchLogsMetricFilter;
import com.blazebit.query.connector.aws.cloudtrail.AwsCloudTrailEventSelector;
import com.blazebit.query.connector.aws.cloudtrail.AwsCloudTrailTrail;
import com.blazebit.query.connector.aws.cloudtrail.AwsCloudTrailTrailStatus;
import com.blazebit.query.connector.aws.sns.AwsSnsSubscription;
import com.blazebit.query.connector.aws.sns.AwsSnsTopic;
import com.blazebit.query.connector.aws.lambda.AwsFunction;
import com.blazebit.query.connector.aws.rds.AwsDBCluster;
import com.blazebit.query.connector.aws.rds.AwsDBClusterSnapshot;
import com.blazebit.query.connector.aws.rds.AwsDBInstance;
import com.blazebit.query.connector.aws.rds.AwsDBSnapshot;
import com.blazebit.query.connector.aws.rds.AwsDBSnapshotAttribute;
import com.blazebit.query.connector.aws.rds.AwsEventSubscription;
import com.blazebit.query.connector.aws.route53.AwsHealthCheck;
import com.blazebit.query.connector.aws.route53.AwsHostedZone;
import com.blazebit.query.connector.aws.s3.AwsBucketAcl;
import com.blazebit.query.connector.aws.s3.AwsBucketVersioning;
import com.blazebit.query.connector.aws.s3.AwsBucketPolicy;
import com.blazebit.query.connector.aws.s3.AwsLoggingEnabled;
import com.blazebit.query.connector.aws.s3.AwsObjectLockConfiguration;
import com.blazebit.query.connector.aws.s3.AwsPolicyStatus;
import com.blazebit.query.connector.aws.s3.AwsLifeCycleRule;
import com.blazebit.query.connector.aws.s3.AwsPublicAccessBlockConfiguration;
import com.blazebit.query.connector.aws.s3.AwsBucket;
import com.blazebit.query.connector.aws.s3.AwsServerSideEncryptionRule;
import com.blazebit.query.connector.azure.graph.AzureGraphAlert;
import com.blazebit.query.connector.azure.graph.AzureGraphApplication;
import com.blazebit.query.connector.azure.graph.AzureGraphClientAccessor;
import com.blazebit.query.connector.azure.graph.AzureGraphConditionalAccessPolicy;
import com.blazebit.query.connector.azure.graph.AzureGraphIncident;
import com.blazebit.query.connector.azure.graph.AzureGraphManagedDevice;
import com.blazebit.query.connector.azure.graph.AzureGraphOrganization;
import com.blazebit.query.connector.azure.graph.AzureGraphRiskyUser;
import com.blazebit.query.connector.azure.graph.AzureGraphSecureScore;
import com.blazebit.query.connector.azure.graph.AzureGraphUserLastSignInActivity;
import com.blazebit.query.connector.azure.graph.ServicePlan;
import com.blazebit.query.connector.azure.graph.AzureGraphSecureScoreControlProfile;
import com.blazebit.query.connector.azure.graph.AzureGraphServicePlanInfo;
import com.blazebit.query.connector.azure.graph.AzureGraphUser;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceBlobServiceProperties;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourcePatchAssessmentResult;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceRoleAssignment;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceSecurityAssessment;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceManagedCluster;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourcePostgreSqlFlexibleServer;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceManagerPostgreSqlManager;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceManagerPostgreSqlManagerConnectorConfig;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourcePostgreSqlFlexibleServerBackup;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourcePostgreSqlFlexibleServerWithParameters;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceStorageAccount;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceDisk;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceManagerResourceGroup;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceNetworkSecurityGroup;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceSubscription;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceTenant;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceVault;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceVirtualMachine;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceVirtualNetwork;
import com.blazebit.query.connector.azure.graph.AzureGraphConnectorConfig;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceManagerConnectorConfig;
import com.blazebit.query.connector.azure.resourcemanager.ResourceGraphClientAccessor;
import com.azure.resourcemanager.resourcegraph.ResourceGraphManager;
import com.blazebit.query.connector.gcp.compute.GcpFirewallRule;
import com.blazebit.query.connector.gcp.compute.GcpInstance;
import com.blazebit.query.connector.gcp.container.GcpGkeCluster;
import com.blazebit.query.connector.gcp.dns.GcpDnsManagedZone;
import com.blazebit.query.connector.gcp.kms.GcpKmsCryptoKey;
import com.blazebit.query.connector.gcp.sql.GcpSqlInstance;
import com.blazebit.query.connector.google.workspace.endpointverification.GoogleChromeOsDevice;
import com.blazebit.query.connector.google.workspace.endpointverification.GoogleMobileDevice;
import com.blazebit.query.connector.github.graphql.GitHubBranchProtectionRule;
import com.blazebit.query.connector.github.graphql.GitHubGraphQlClient;
import com.blazebit.query.connector.github.graphql.GitHubOrganization;
import com.blazebit.query.connector.github.graphql.GitHubPullRequest;
import com.blazebit.query.connector.github.graphql.GitHubRepository;
import com.blazebit.query.connector.github.graphql.GitHubRuleset;
import com.blazebit.query.connector.github.v0314.model.OrganizationSimple;
import com.blazebit.query.connector.github.v0314.model.ShortBranch;
import com.blazebit.query.connector.github.v0314.model.Team;
import com.blazebit.query.connector.gitlab.GitlabGraphQlClient;
import com.blazebit.query.connector.gitlab.GitlabGroup;
import com.blazebit.query.connector.gitlab.GitlabMergeRequest;
import com.blazebit.query.connector.gitlab.GitlabProject;
import com.blazebit.query.connector.gitlab.GitlabUser;
import com.blazebit.query.connector.gitlab.GroupMember;
import com.blazebit.query.connector.gitlab.ProjectMember;
import com.blazebit.query.connector.gitlab.ProjectProtectedBranch;
import com.blazebit.query.connector.jira.cloud.IssueBeanWrapper;
import com.blazebit.query.connector.jira.cloud.ProjectWrapper;
import com.blazebit.query.connector.jira.cloud.model.ServerInformation;
import com.blazebit.query.connector.jira.cloud.admin.JiraCloudAdminDirectoryWrapper;
import com.blazebit.query.connector.jira.cloud.admin.JiraCloudAdminUserWrapper;
import com.blazebit.query.connector.jira.cloud.model.UserPermission;
import com.blazebit.query.connector.jira.cloud.admin.model.OrgModel;
import com.blazebit.query.connector.jira.datacenter.model.PermissionGrantBean;
import com.blazebit.query.connector.kandji.DeviceParameter;
import com.blazebit.query.connector.kandji.KandjiJavaTimeModule;
import com.blazebit.query.connector.kandji.model.GetDeviceDetails200Response;
import com.blazebit.query.connector.kandji.model.ListDevices200ResponseInner;
import com.blazebit.query.connector.datadog.DatadogApiKey;
import com.blazebit.query.connector.datadog.DatadogApplicationKey;
import com.blazebit.query.connector.datadog.DatadogAuditLog;
import com.blazebit.query.connector.datadog.DatadogCsmFinding;
import com.blazebit.query.connector.datadog.DatadogHost;
import com.blazebit.query.connector.datadog.DatadogLog;
import com.blazebit.query.connector.datadog.DatadogMonitor;
import com.blazebit.query.connector.datadog.DatadogMonitorDowntime;
import com.blazebit.query.connector.datadog.DatadogOrganizationSettings;
import com.blazebit.query.connector.datadog.DatadogPermission;
import com.blazebit.query.connector.datadog.DatadogRole;
import com.blazebit.query.connector.datadog.DatadogSecurityMonitoringRule;
import com.blazebit.query.connector.datadog.DatadogSecuritySignal;
import com.blazebit.query.connector.datadog.DatadogSyntheticsTest;
import com.blazebit.query.connector.datadog.DatadogUser;
import com.blazebit.query.connector.observatory.ObservatoryClient;
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
import com.blazebit.query.connector.gcp.base.GcpAsset;
import com.blazebit.query.connector.gcp.base.GcpFolder;
import com.blazebit.query.connector.gcp.base.GcpOrganization;
import com.blazebit.query.connector.gcp.base.GcpProject;
import com.blazebit.query.connector.gcp.iam.GcpIamPolicy;
import com.blazebit.query.connector.gcp.iam.GcpRole;
import com.blazebit.query.connector.gcp.iam.GcpServiceAccount;
import com.blazebit.query.connector.gcp.storage.GcpBucket;
import com.blazebit.query.connector.google.directory.GoogleGroup;
import com.blazebit.query.connector.google.directory.GoogleMember;
import com.blazebit.query.connector.google.directory.GoogleRole;
import com.blazebit.query.connector.google.directory.GoogleRoleAssignment;
import com.blazebit.query.connector.google.directory.GoogleUser;
import com.blazebit.query.connector.google.drive.GoogleDrive;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
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

	private static final String DEVOPS_PAT = "";
	private static final String DEVOPS_ORGANIZATION = "";
	private static final String DEVOPS_PROJECT = "";

	private static final String JIRA_DATACENTER_HOST = "";
	private static final String JIRA_DATACENTER_TOKEN = "";
	private static final String JIRA_CLOUD_HOST = "";
	private static final String JIRA_CLOUD_USER = "";
	private static final String JIRA_CLOUD_TOKEN = "";
	private static final String JIRA_CLOUD_ADMIN_API_KEY = "";

	private static final String OBSERVATORY_HOST = "";

	private static final String DATADOG_API_KEY = "";
	private static final String DATADOG_APP_KEY = "";
	private static final String DATADOG_SITE = "datadoghq.eu";

	private Main() {
	}

	public static void main(String[] args) throws Exception {
		try (EntityManagerFactory emf = Persistence.createEntityManagerFactory( "default" )) {
			SessionFactory sf = emf.unwrap( SessionFactory.class );
			sf.inTransaction( s -> {
				s.persist( new TestEntity( 1L, "Test", new TestEmbeddable( "text1", "text2" ), Set.of(TestEnum.A, TestEnum.B) ) );
			} );

			CriteriaBuilderFactory cbf = Criteria.getDefault().createCriteriaBuilderFactory( emf );
			EntityViewConfiguration defaultConfiguration = EntityViews.createDefaultConfiguration();
			defaultConfiguration.addEntityView( TestEntityView.class );
			defaultConfiguration.addEntityView( TestEmbeddableView.class );
			EntityViewManager evm = defaultConfiguration.createEntityViewManager( cbf );

			QueryContextBuilder queryContextBuilder = Queries.createQueryContextBuilder();
			AzureResourceManager resourceManager = createResourceManager();
			queryContextBuilder.setProperty( AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getPropertyName(), resourceManager);
			queryContextBuilder.setPropertyProvider( AzureResourceManagerPostgreSqlManagerConnectorConfig.POSTGRESQL_MANAGER.getPropertyName(),
					Main::createPostgreSqlManagers );
			queryContextBuilder.setProperty( "serverParameters", List.of("ssl_min_protocol_version", "authentication_timeout"));
			queryContextBuilder.setProperty( AzureResourceManagerConnectorConfig.RESOURCE_GRAPH_CLIENT.getPropertyName(), createResourceGraphClient( resourceManager ));
			queryContextBuilder.setProperty( AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getPropertyName(), createGraphServiceClient());
//			queryContextBuilder.setProperty( AwsConnectorConfig.ACCOUNT.getPropertyName(), createAwsAccount() );
//				queryContextBuilder.setProperty( GoogleDirectoryConnectorConfig.GOOGLE_DIRECTORY_SERVICE.getPropertyName(), createGoogleDirectory() );
//			queryContextBuilder.setProperty( GoogleDriveConnectorConfig.GOOGLE_DRIVE_SERVICE.getPropertyName(), createGoogleDrive() );
//			queryContextBuilder.setProperty( GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getPropertyName(), createGcpCredentialsProvider() );
//			queryContextBuilder.setProperty( JiraDatacenterConnectorConfig.API_CLIENT.getPropertyName(), createJiraDatacenterApiClient());
//			queryContextBuilder.setProperty( JiraCloudConnectorConfig.API_CLIENT.getPropertyName(), createJiraCloudApiClient());
//			queryContextBuilder.setProperty( "jqlQuery", "statusCategory != Done");
//			queryContextBuilder.setProperty( JiraCloudAdminConnectorConfig.API_CLIENT.getPropertyName(), createJiraCloudAdminOrganizationApiClient());
			queryContextBuilder.setProperty( EntityViewConnectorConfig.ENTITY_VIEW_MANAGER.getPropertyName(), evm );
//			queryContextBuilder.setProperty( ObservatoryConnectorConfig.OBSERVATORY_CLIENT.getPropertyName(), createObservatoryClient());
//			queryContextBuilder.setProperty( DatadogConnectorConfig.DATADOG_API_CLIENT.getPropertyName(), createDatadogApiClient());
//			queryContextBuilder.setProperty( GitlabConnectorConfig.GITLAB_API.getPropertyName(), createGitlabApi());
//			queryContextBuilder.setProperty( GitlabGraphQlConnectorConfig.GITLAB_GRAPHQL_CLIENT.getPropertyName(), createGitlabGraphQLClient());
//            queryContextBuilder.setProperty(KandjiConnectorConfig.API_CLIENT.getPropertyName(), createKandjiApiClient());
//            queryContextBuilder.setProperty(GithubConnectorConfig.GITHUB.getPropertyName(), createGithub());
//			queryContextBuilder.setProperty( GitHubConnectorConfig.GITHUB_GRAPHQL_CLIENT.getPropertyName(), createGitHubGraphQLClient());
//            queryContextBuilder.setProperty(com.blazebit.query.connector.github.v0314.GithubConnectorConfig.API_CLIENT.getPropertyName(), createGitHubApiClient());

			// Azure Resource manager
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceVirtualMachine.class, "AzureVirtualMachine" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceStorageAccount.class, "AzureStorageAccount" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceBlobServiceProperties.class,
					"AzureBlobServiceProperties" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceManagedCluster.class, "AzureManagedCluster" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceVirtualNetwork.class, "AzureVirtualNetwork" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceVault.class, "AzureVault" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceDisk.class, "AzureDisk" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceNetworkSecurityGroup.class, "AzureNetworkSecurityGroup" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceManagerResourceGroup.class, "AzureResourceGroup" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceSubscription.class, "AzureSubscription" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceTenant.class, "AzureTenant" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourcePostgreSqlFlexibleServer.class,"AzurePostgreSqlFlexibleServer" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourcePostgreSqlFlexibleServerBackup.class,"AzurePostgreSqlFlexibleServerBackup" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourcePostgreSqlFlexibleServerWithParameters.class,"AzurePostgreSqlFlexibleServerWithParameters" );

			// Azure Graph
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphUser.class, "AzureUser" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphUserLastSignInActivity.class, "AzureUserLastSignInActivity" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphConditionalAccessPolicy.class,
					"AzureConditionalAccessPolicy" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphApplication.class, "AzureApplication" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphManagedDevice.class, "AzureManagedDevice" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphOrganization.class, "AzureOrganization" );
			queryContextBuilder.registerSchemaObjectAlias( ServicePlan.class, "AzureServicePlan" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphServicePlanInfo.class, "AzureAvailableServicePlan" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphAlert.class, "AzureAlert" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphIncident.class, "AzureIncident" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphSecureScore.class, "AzureSecureScore" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphSecureScoreControlProfile.class, "AzureSecureScoreControlProfile" );
			queryContextBuilder.registerSchemaObjectAlias( AzureGraphRiskyUser.class, "AzureRiskyUser" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourcePatchAssessmentResult.class, "AzurePatchAssessmentResult" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceSecurityAssessment.class, "AzureSecurityAssessment" );
			queryContextBuilder.registerSchemaObjectAlias( AzureResourceRoleAssignment.class, "AzureRoleAssignment" );

			// Access Analyzer
			queryContextBuilder.registerSchemaObjectAlias( AwsAccessAnalyzerAnalyzer.class, "AwsAnalyzer" );

			// IAM
			queryContextBuilder.registerSchemaObjectAlias( AwsIamUser.class, "AwsUser" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamPasswordPolicy.class, "AwsIamPasswordPolicy" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamMfaDevice.class, "AwsMFADevice" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamLoginProfile.class, "AwsLoginProfile" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamAccountSummary.class, "AwsIamAccountSummary" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamAccessKeyMetaDataLastUsed.class,
					"AwsAccessKeyMetaDataLastUsed" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamPolicyVersion.class, "AwsIamPolicyVersion" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamGroup.class, "AwsGroup" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamGroupAttachedPolicy.class, "AwsGroupAttachedPolicy" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamGroupInlinePolicy.class, "AwsGroupInlinePolicy" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamRole.class, "AwsRole" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamRoleAttachedPolicy.class, "AwsRoleAttachedPolicy" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamRoleInlinePolicy.class, "AwsRoleInlinePolicy" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamServerCertificate.class, "AwsServerCertificate" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamUserAttachedPolicy.class, "AwsUserAttachedPolicy" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamUserInlinePolicy.class, "AwsUserInlinePolicy" );
			queryContextBuilder.registerSchemaObjectAlias( AwsIamVirtualMfaDevice.class, "AwsVirtualMfaDevice" );

			// EC2
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2Instance.class, "AwsEc2Instance" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2InstanceStatus.class, "AwsEc2InstanceStatus" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2Address.class, "AwsEc2Address" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2Subnet.class, "AwsEc2Subnet" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2CustomerGateway.class, "AwsEc2CustomerGateway" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2InternetGateway.class, "AwsEc2InternetGateway" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2NatGateway.class, "AwsEc2NatGateway" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2RouteTable.class, "AwsEc2RouteTable" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2VpcEndpointService.class, "AwsEc2VpcEndpointService" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2VpcPeeringConnection.class, "AwsEc2VpcPeeringConnection" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2VpnGateway.class, "AwsEc2VpnGateway" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2ClientVpnEndpoint.class, "AwsEc2ClientVpnEndpoint" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2VpnConnection.class, "AwsEc2VpnConnection" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2VpnConnectionTunnelOption.class, "AwsEc2VpnConnectionTunnelOption" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2VpcBlockPublicAccessOptions.class,
					"AwsEc2VpcBlockPublicAccessOptions" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2TransitGateway.class, "AwsEc2TransitGateway" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2TransitGatewayAttachment.class, "AwsEc2TransitGatewayAttachment" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2TransitGatewayRouteTable.class, "AwsEc2TransitGatewayRouteTable" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2NetworkInterface.class, "AwsEc2NetworkInterface" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2SpotFleetRequest.class, "AwsEc2SpotFleetRequest" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2SpotFleetRequestLaunchSpecification.class,
					"AwsEc2SpotFleetRequestLaunchSpecification" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2DhcpOptions.class, "AwsEc2DhcpOptions" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2LaunchTemplate.class, "AwsEc2LaunchTemplate" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2LaunchTemplateVersion.class, "AwsEc2LaunchTemplateVersion" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2ManagedPrefixList.class, "AwsEc2ManagedPrefixList" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2TrafficMirrorSession.class, "AwsEc2TrafficMirrorSession" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2TrafficMirrorFilter.class, "AwsEc2TrafficMirrorFilter" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2TrafficMirrorTarget.class, "AwsEc2TrafficMirrorTarget" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2Vpc.class, "AwsEc2Vpc" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2VpcEndpoint.class, "AwsEc2VpcEndpoint" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2SecurityGroup.class, "AwsEc2SecurityGroup" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2SecurityGroupIpPermission.class, "AwsEc2SecurityGroupIpPermission" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2Volume.class, "AwsEc2Volume" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2Snapshot.class, "AwsEc2Snapshot" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2SnapshotAttribute.class, "AwsEc2SnapshotAttribute" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2NetworkAcl.class, "AwsEc2NetworkAcl" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2NetworkAclEntry.class, "AwsEc2NetworkAclEntry" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2EbsEncryptionByDefault.class, "AwsEc2EbsEncryptionByDefault" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEc2FlowLogs.class, "AwsEc2FlowLogs" );
			// RDS
			queryContextBuilder.registerSchemaObjectAlias( AwsDBCluster.class, "AwsDBCluster" );
			queryContextBuilder.registerSchemaObjectAlias( AwsDBClusterSnapshot.class, "AwsDBClusterSnapshot" );
			queryContextBuilder.registerSchemaObjectAlias( AwsDBInstance.class, "AwsDBInstance" );
			queryContextBuilder.registerSchemaObjectAlias( AwsDBSnapshot.class, "AwsDBSnapshot" );
			queryContextBuilder.registerSchemaObjectAlias( AwsDBSnapshotAttribute.class, "AwsDBSnapshotAttribute" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEventSubscription.class, "AwsEventSubscription" );
			// EFS
			queryContextBuilder.registerSchemaObjectAlias( AwsFileSystem.class, "AwsFileSystem" );
			// ECR
			queryContextBuilder.registerSchemaObjectAlias( AwsRepository.class, "AwsRepository" );
			// ECS
			queryContextBuilder.registerSchemaObjectAlias( AwsEcsCluster.class, "AwsEcsCluster" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEcsContainerDefinition.class, "AwsEcsContainerDefinition" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEcsService.class, "AwsEcsService" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEcsTaskDefinition.class, "AwsEcsTaskDefinition" );
			queryContextBuilder.registerSchemaObjectAlias( AwsEcsTaskSet.class, "AwsEcsTaskSet" );
			// ELB
			queryContextBuilder.registerSchemaObjectAlias( AwsLoadBalancer.class, "AwsLoadBalancer" );
			// Lambda
			queryContextBuilder.registerSchemaObjectAlias( AwsFunction.class, "AwsFunction" );
			// Route53
			queryContextBuilder.registerSchemaObjectAlias( AwsHostedZone.class, "AwsHostedZone" );
			queryContextBuilder.registerSchemaObjectAlias( AwsHealthCheck.class, "AwsHealthCheck" );
			// S3
			queryContextBuilder.registerSchemaObjectAlias( AwsBucket.class, "AwsBucket" );
			queryContextBuilder.registerSchemaObjectAlias( AwsBucketAcl.class, "AwsBucketAcl" );
			queryContextBuilder.registerSchemaObjectAlias( AwsBucketVersioning.class, "AwsBucketVersioning" );
			queryContextBuilder.registerSchemaObjectAlias( AwsBucketPolicy.class, "AwsBucketPolicy" );
			queryContextBuilder.registerSchemaObjectAlias( AwsLifeCycleRule.class, "AwsLifeCycleRule" );
			queryContextBuilder.registerSchemaObjectAlias( AwsLoggingEnabled.class, "AwsLoggingEnabled" );
			queryContextBuilder.registerSchemaObjectAlias( AwsObjectLockConfiguration.class, "AwsObjectLockConfiguration" );
			queryContextBuilder.registerSchemaObjectAlias( AwsPolicyStatus.class, "AwsPolicyStatus" );
			queryContextBuilder.registerSchemaObjectAlias( AwsPublicAccessBlockConfiguration.class, "AwsPublicAccessBlockConfiguration" );
			queryContextBuilder.registerSchemaObjectAlias( AwsServerSideEncryptionRule.class, "AwsServerSideEncryptionRule" );
			// KMS
			queryContextBuilder.registerSchemaObjectAlias( AwsKmsKey.class, "AwsKmsKey" );
			queryContextBuilder.registerSchemaObjectAlias( AwsKmsKeyAlias.class, "AwsKmsKeyAlias" );
			queryContextBuilder.registerSchemaObjectAlias( AwsKmsKeyPolicy.class, "AwsKmsKeyPolicy" );
			queryContextBuilder.registerSchemaObjectAlias( AwsKmsKeyRotationStatus.class, "AwsKmsKeyRotationStatus" );
			// Secrets Manager
			queryContextBuilder.registerSchemaObjectAlias( AwsSecretsManagerSecret.class, "AwsSecretsManagerSecret" );
			// CloudWatch
			queryContextBuilder.registerSchemaObjectAlias( AwsCloudWatchCompositeAlarm.class, "AwsCloudWatchCompositeAlarm" );
			queryContextBuilder.registerSchemaObjectAlias( AwsCloudWatchMetricAlarm.class, "AwsCloudWatchMetricAlarm" );
			// CloudWatch Logs
			queryContextBuilder.registerSchemaObjectAlias( AwsCloudWatchLogsLogGroup.class, "AwsCloudWatchLogsLogGroup" );
			queryContextBuilder.registerSchemaObjectAlias( AwsCloudWatchLogsMetricFilter.class, "AwsCloudWatchLogsMetricFilter" );
			// CloudTrail
			queryContextBuilder.registerSchemaObjectAlias( AwsCloudTrailEventSelector.class, "AwsCloudTrailEventSelector" );
			queryContextBuilder.registerSchemaObjectAlias( AwsCloudTrailTrail.class, "AwsCloudTrailTrail" );
			queryContextBuilder.registerSchemaObjectAlias( AwsCloudTrailTrailStatus.class, "AwsCloudTrailTrailStatus" );
			// SNS
			queryContextBuilder.registerSchemaObjectAlias( AwsSnsSubscription.class, "AwsSnsSubscription" );
			queryContextBuilder.registerSchemaObjectAlias( AwsSnsTopic.class, "AwsSnsTopic" );


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
			queryContextBuilder.registerSchemaObjectAlias( GoogleUser.class, "GoogleUser" );
			queryContextBuilder.registerSchemaObjectAlias( GoogleGroup.class, "GoogleGroup" );
			queryContextBuilder.registerSchemaObjectAlias( GoogleMember.class, "GoogleMember" );
			queryContextBuilder.registerSchemaObjectAlias( GoogleRole.class, "GoogleRole" );
			queryContextBuilder.registerSchemaObjectAlias( GoogleRoleAssignment.class, "GoogleRoleAssignment" );
			queryContextBuilder.registerSchemaObjectAlias( GoogleDrive.class, "GoogleDrive" );
			queryContextBuilder.registerSchemaObjectAlias( GoogleMobileDevice.class, "GoogleMobileDevice" );
			queryContextBuilder.registerSchemaObjectAlias( GoogleChromeOsDevice.class, "GoogleChromeOsDevice" );

			// GCP
			queryContextBuilder.registerSchemaObjectAlias( GcpOrganization.class, "GcpOrganization" );
			queryContextBuilder.registerSchemaObjectAlias( GcpFolder.class, "GcpFolder" );
			queryContextBuilder.registerSchemaObjectAlias( GcpProject.class, "GcpProject" );
			queryContextBuilder.registerSchemaObjectAlias( GcpAsset.class, "GcpAsset" );
			queryContextBuilder.registerSchemaObjectAlias( GcpInstance.class, "GcpInstance" );
			queryContextBuilder.registerSchemaObjectAlias( GcpFirewallRule.class, "GcpFirewallRule" );
			queryContextBuilder.registerSchemaObjectAlias( GcpRole.class, "GcpIamRole" );
			queryContextBuilder.registerSchemaObjectAlias( GcpServiceAccount.class, "GcpIamServiceAccount" );
			queryContextBuilder.registerSchemaObjectAlias( GcpIamPolicy.class, "GcpIamPolicy" );
			queryContextBuilder.registerSchemaObjectAlias( GcpBucket.class, "GcpBucket" );
			queryContextBuilder.registerSchemaObjectAlias( GcpSqlInstance.class, "GcpSqlInstance" );
			queryContextBuilder.registerSchemaObjectAlias( GcpGkeCluster.class, "GcpGkeCluster" );
			queryContextBuilder.registerSchemaObjectAlias( GcpKmsCryptoKey.class, "GcpKmsCryptoKey" );
			queryContextBuilder.registerSchemaObjectAlias( GcpDnsManagedZone.class, "GcpDnsManagedZone" );

			// Jira Datacenter
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.datacenter.model.ProjectBean.class, "JiraDatacenterProject" );
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.datacenter.model.UserBean.class, "JiraDatacenterUser" );
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.datacenter.model.GroupSuggestionBean.class, "JiraDatacenterGroup" );
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.datacenter.GroupMember.class, "JiraDatacenterMember" );
			queryContextBuilder.registerSchemaObjectAlias( PermissionGrantBean.class, "JiraDatacenterPermission" );

			// Jira Cloud
			queryContextBuilder.registerSchemaObjectAlias( ProjectWrapper.class, "JiraCloudProject" );
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.cloud.model.User.class, "JiraCloudUser" );
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.cloud.model.FoundGroup.class, "JiraCloudGroup" );
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.jira.cloud.GroupMember.class, "JiraCloudMember" );
			queryContextBuilder.registerSchemaObjectAlias( UserPermission.class, "JiraCloudPermission" );
			queryContextBuilder.registerSchemaObjectAlias( IssueBeanWrapper.class, "JiraCloudIssue" );
			queryContextBuilder.registerSchemaObjectAlias( ServerInformation.class, "JiraCloudServerInfo" );

			// Jira Cloud Admin
			queryContextBuilder.registerSchemaObjectAlias( OrgModel.class, "JiraCloudAdminOrg" );
			queryContextBuilder.registerSchemaObjectAlias( JiraCloudAdminDirectoryWrapper.class, "JiraCloudAdminDirectory" );
			queryContextBuilder.registerSchemaObjectAlias( JiraCloudAdminUserWrapper.class, "JiraCloudAdminUser" );

			// Datadog
			queryContextBuilder.registerSchemaObjectAlias( DatadogLog.class, "DatadogLog" );
			queryContextBuilder.registerSchemaObjectAlias( DatadogSyntheticsTest.class, "DatadogSyntheticsTest" );
			queryContextBuilder.registerSchemaObjectAlias( DatadogMonitor.class, "DatadogMonitor" );
			queryContextBuilder.registerSchemaObjectAlias( DatadogHost.class, "DatadogHost" );
			queryContextBuilder.registerSchemaObjectAlias( DatadogUser.class, "DatadogUser" );
			queryContextBuilder.registerSchemaObjectAlias( DatadogSecurityMonitoringRule.class, "DatadogSecurityMonitoringRule" );
			queryContextBuilder.registerSchemaObjectAlias( DatadogOrganizationSettings.class, "DatadogOrganizationSettings" );
			queryContextBuilder.registerSchemaObjectAlias( DatadogCsmFinding.class, "DatadogCsmFinding" );
			queryContextBuilder.registerSchemaObjectAlias( DatadogSecuritySignal.class, "DatadogSecuritySignal" );
			queryContextBuilder.registerSchemaObjectAlias( DatadogAuditLog.class, "DatadogAuditLog" );
			queryContextBuilder.registerSchemaObjectAlias( DatadogApiKey.class, "DatadogApiKey" );
			queryContextBuilder.registerSchemaObjectAlias( DatadogRole.class, "DatadogRole" );
			queryContextBuilder.registerSchemaObjectAlias( DatadogApplicationKey.class, "DatadogApplicationKey" );
			queryContextBuilder.registerSchemaObjectAlias( DatadogMonitorDowntime.class, "DatadogMonitorDowntime" );
			queryContextBuilder.registerSchemaObjectAlias( DatadogPermission.class, "DatadogPermission" );

			// Observatory
			queryContextBuilder.registerSchemaObject(
					com.blazebit.query.connector.observatory.ObservatoryScan.class,
					com.blazebit.query.connector.observatory.ObservatoryScanDataFetcher.INSTANCE
			);
			queryContextBuilder.registerSchemaObjectAlias( com.blazebit.query.connector.observatory.ObservatoryScan.class, "ObservatoryScan" );

			try (QueryContext queryContext = queryContextBuilder.build()) {
				try (EntityManager em = emf.createEntityManager();
					QuerySession session = queryContext.createSession(
							Map.of( EntityViewConnectorConfig.ENTITY_MANAGER.getPropertyName(), em ) )) {
//					testJiraDatacenter( session );
//					testJiraCloud( session );
//					testJiraCloudAdmin( session );
//					testGcp( session );
//					testGoogleWorkspace( session );
//					testGoogleEndpointVerification( session );
//					testGcp( session );
//					testGoogleWorkspace( session );
//					testDatadog( session );
//					testAws( session );
//					testGitlab( session );
//					testGitHub( session );
//					testGitHubOpenAPI( session );
//					testKandji( session );
//					testEntityView( session );
//					testObservatory(  session );
					testAzureGraph( session );
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

		TypedQuery<Object[]> awsLoginProfileQuery = session.createQuery(
				"select l.* from AwsLoginProfile l" );
		List<Object[]> awsLoginProfileResult = awsLoginProfileQuery.getResultList();
		System.out.println( "AwsLoginProfiles" );
		print( awsLoginProfileResult );

		TypedQuery<Object[]> awsAccountSummaryQuery = session.createQuery(
				"select a.* from AwsIamAccountSummary a" );
		List<Object[]> awsAccountSummaryResult = awsAccountSummaryQuery.getResultList();
		System.out.println( "AwsAccountSummary" );
		print( awsAccountSummaryResult );

		TypedQuery<Object[]> awsIamPolicyVersionQuery = session.createQuery(
				"select p.* from AwsIamPolicyVersion p" );
		List<Object[]> awsIamPolicyVersionResult = awsIamPolicyVersionQuery.getResultList();
		System.out.println( "AwsIamPolicyVersions" );
		print( awsIamPolicyVersionResult );

		TypedQuery<Object[]> awsGroupQuery = session.createQuery(
				"select g.* from AwsGroup g" );
		List<Object[]> awsGroupResult = awsGroupQuery.getResultList();
		System.out.println( "AwsGroups" );
		print( awsGroupResult );

		TypedQuery<Object[]> awsGroupAttachedPolicyQuery = session.createQuery(
				"select p.* from AwsGroupAttachedPolicy p" );
		List<Object[]> awsGroupAttachedPolicyResult = awsGroupAttachedPolicyQuery.getResultList();
		System.out.println( "AwsGroupAttachedPolicies" );
		print( awsGroupAttachedPolicyResult );

		TypedQuery<Object[]> awsGroupInlinePolicyQuery = session.createQuery(
				"select p.* from AwsGroupInlinePolicy p" );
		List<Object[]> awsGroupInlinePolicyResult = awsGroupInlinePolicyQuery.getResultList();
		System.out.println( "AwsGroupInlinePolicies" );
		print( awsGroupInlinePolicyResult );

		TypedQuery<Object[]> awsRoleQuery = session.createQuery(
				"select r.* from AwsRole r" );
		List<Object[]> awsRoleResult = awsRoleQuery.getResultList();
		System.out.println( "AwsRoles" );
		print( awsRoleResult );

		TypedQuery<Object[]> awsRoleAttachedPolicyQuery = session.createQuery(
				"select p.* from AwsRoleAttachedPolicy p" );
		List<Object[]> awsRoleAttachedPolicyResult = awsRoleAttachedPolicyQuery.getResultList();
		System.out.println( "AwsRoleAttachedPolicies" );
		print( awsRoleAttachedPolicyResult );

		TypedQuery<Object[]> awsRoleInlinePolicyQuery = session.createQuery(
				"select p.* from AwsRoleInlinePolicy p" );
		List<Object[]> awsRoleInlinePolicyResult = awsRoleInlinePolicyQuery.getResultList();
		System.out.println( "AwsRoleInlinePolicies" );
		print( awsRoleInlinePolicyResult );

		TypedQuery<Object[]> awsServerCertificateQuery = session.createQuery(
				"select c.* from AwsServerCertificate c" );
		List<Object[]> awsServerCertificateResult = awsServerCertificateQuery.getResultList();
		System.out.println( "AwsServerCertificates" );
		print( awsServerCertificateResult );

		TypedQuery<Object[]> awsUserAttachedPolicyQuery = session.createQuery(
				"select p.* from AwsUserAttachedPolicy p" );
		List<Object[]> awsUserAttachedPolicyResult = awsUserAttachedPolicyQuery.getResultList();
		System.out.println( "AwsUserAttachedPolicies" );
		print( awsUserAttachedPolicyResult );

		TypedQuery<Object[]> awsUserInlinePolicyQuery = session.createQuery(
				"select p.* from AwsUserInlinePolicy p" );
		List<Object[]> awsUserInlinePolicyResult = awsUserInlinePolicyQuery.getResultList();
		System.out.println( "AwsUserInlinePolicies" );
		print( awsUserInlinePolicyResult );

		TypedQuery<Object[]> awsVirtualMfaDeviceQuery = session.createQuery(
				"select d.* from AwsVirtualMfaDevice d" );
		List<Object[]> awsVirtualMfaDeviceResult = awsVirtualMfaDeviceQuery.getResultList();
		System.out.println( "AwsVirtualMfaDevices" );
		print( awsVirtualMfaDeviceResult );

		// Access Analyzer
		TypedQuery<Object[]> awsAnalyzerQuery = session.createQuery(
				"select a.* from AwsAnalyzer a" );
		List<Object[]> awsAnalyzerResult = awsAnalyzerQuery.getResultList();
		System.out.println( "AwsAnalyzers" );
		print( awsAnalyzerResult );

		// EC2
		TypedQuery<Object[]> awsInstanceQuery = session.createQuery(
				"select i.* from AwsEc2Instance i" );
		List<Object[]> awsInstanceResult = awsInstanceQuery.getResultList();
		System.out.println("AwsEc2Instances");
		print(awsInstanceResult);

		TypedQuery<Object[]> awsVolumeQuery = session.createQuery(
				"select v.* from AwsEc2Volume v" );
		List<Object[]> awsVolumeResult = awsVolumeQuery.getResultList();
		System.out.println("AwsEc2Volumes");
		print(awsVolumeResult);

		TypedQuery<Object[]> awsVpcQuery = session.createQuery(
				"select v.* from AwsEc2Vpc v" );
		List<Object[]> awsVpcResult = awsVpcQuery.getResultList();
		System.out.println("AwsEc2Vpcs");
		print(awsVpcResult);

		TypedQuery<Object[]> awsSecurityGroupQuery = session.createQuery(
				"select g.* from AwsEc2SecurityGroup g" );
		List<Object[]> awsSecurityGroupResult = awsSecurityGroupQuery.getResultList();
		System.out.println("AwsEc2SecurityGroups");
		print(awsSecurityGroupResult);

		TypedQuery<Object[]> awsNetworkAclQuery = session.createQuery(
				"select g.* from AwsEc2NetworkAcl g" );
		List<Object[]> awsNetworkAclResult = awsNetworkAclQuery.getResultList();
		System.out.println("AwsEc2NetworkAcls");
		print(awsNetworkAclResult);

		TypedQuery<Object[]> awsNetworkAclEntryQuery = session.createQuery(
				"select e.* from AwsEc2NetworkAclEntry e" );
		List<Object[]> awsNetworkAclEntryResult = awsNetworkAclEntryQuery.getResultList();
		System.out.println("AwsEc2NetworkAclEntries");
		print(awsNetworkAclEntryResult);

		TypedQuery<Object[]> awsInstanceStatusQuery = session.createQuery(
				"select s.* from AwsEc2InstanceStatus s" );
		List<Object[]> awsInstanceStatusResult = awsInstanceStatusQuery.getResultList();
		System.out.println("AwsEc2InstanceStatuses");
		print(awsInstanceStatusResult);

		TypedQuery<Object[]> awsAddressQuery = session.createQuery(
				"select a.* from AwsEc2Address a" );
		List<Object[]> awsAddressResult = awsAddressQuery.getResultList();
		System.out.println("AwsEc2Addresses");
		print(awsAddressResult);

		TypedQuery<Object[]> awsSubnetQuery = session.createQuery(
				"select s.* from AwsEc2Subnet s" );
		List<Object[]> awsSubnetResult = awsSubnetQuery.getResultList();
		System.out.println("AwsEc2Subnets");
		print(awsSubnetResult);

		TypedQuery<Object[]> awsCustomerGatewayQuery = session.createQuery(
				"select g.* from AwsEc2CustomerGateway g" );
		List<Object[]> awsCustomerGatewayResult = awsCustomerGatewayQuery.getResultList();
		System.out.println("AwsEc2CustomerGateways");
		print(awsCustomerGatewayResult);

		TypedQuery<Object[]> awsInternetGatewayQuery = session.createQuery(
				"select g.* from AwsEc2InternetGateway g" );
		List<Object[]> awsInternetGatewayResult = awsInternetGatewayQuery.getResultList();
		System.out.println("AwsEc2InternetGateways");
		print(awsInternetGatewayResult);

		TypedQuery<Object[]> awsNatGatewayQuery = session.createQuery(
				"select g.* from AwsEc2NatGateway g" );
		List<Object[]> awsNatGatewayResult = awsNatGatewayQuery.getResultList();
		System.out.println("AwsEc2NatGateways");
		print(awsNatGatewayResult);

		TypedQuery<Object[]> awsRouteTableQuery = session.createQuery(
				"select r.* from AwsEc2RouteTable r" );
		List<Object[]> awsRouteTableResult = awsRouteTableQuery.getResultList();
		System.out.println("AwsEc2RouteTables");
		print(awsRouteTableResult);

		TypedQuery<Object[]> awsVpcEndpointServiceQuery = session.createQuery(
				"select v.* from AwsEc2VpcEndpointService v" );
		List<Object[]> awsVpcEndpointServiceResult = awsVpcEndpointServiceQuery.getResultList();
		System.out.println("AwsEc2VpcEndpointServices");
		print(awsVpcEndpointServiceResult);

		TypedQuery<Object[]> awsVpcPeeringConnectionQuery = session.createQuery(
				"select v.* from AwsEc2VpcPeeringConnection v" );
		List<Object[]> awsVpcPeeringConnectionResult = awsVpcPeeringConnectionQuery.getResultList();
		System.out.println("AwsEc2VpcPeeringConnections");
		print(awsVpcPeeringConnectionResult);

		TypedQuery<Object[]> awsVpcBlockPublicAccessOptionsQuery = session.createQuery(
				"select v.* from AwsEc2VpcBlockPublicAccessOptions v" );
		List<Object[]> awsVpcBlockPublicAccessOptionsResult = awsVpcBlockPublicAccessOptionsQuery.getResultList();
		System.out.println("AwsEc2VpcBlockPublicAccessOptions");
		print(awsVpcBlockPublicAccessOptionsResult);

		TypedQuery<Object[]> awsVpcEndpointQuery = session.createQuery(
				"select v.* from AwsEc2VpcEndpoint v" );
		List<Object[]> awsVpcEndpointResult = awsVpcEndpointQuery.getResultList();
		System.out.println("AwsEc2VpcEndpoints");
		print(awsVpcEndpointResult);

		TypedQuery<Object[]> awsVpnGatewayQuery = session.createQuery(
				"select v.* from AwsEc2VpnGateway v" );
		List<Object[]> awsVpnGatewayResult = awsVpnGatewayQuery.getResultList();
		System.out.println("AwsEc2VpnGateways");
		print(awsVpnGatewayResult);

		TypedQuery<Object[]> awsClientVpnEndpointQuery = session.createQuery(
				"select v.* from AwsEc2ClientVpnEndpoint v" );
		List<Object[]> awsClientVpnEndpointResult = awsClientVpnEndpointQuery.getResultList();
		System.out.println("AwsEc2ClientVpnEndpoints");
		print(awsClientVpnEndpointResult);

		TypedQuery<Object[]> awsVpnConnectionQuery = session.createQuery(
				"select v.* from AwsEc2VpnConnection v" );
		List<Object[]> awsVpnConnectionResult = awsVpnConnectionQuery.getResultList();
		System.out.println("AwsEc2VpnConnections");
		print(awsVpnConnectionResult);

		TypedQuery<Object[]> awsVpnConnectionTunnelOptionQuery = session.createQuery(
				"select v.* from AwsEc2VpnConnectionTunnelOption v" );
		List<Object[]> awsVpnConnectionTunnelOptionResult = awsVpnConnectionTunnelOptionQuery.getResultList();
		System.out.println("AwsEc2VpnConnectionTunnelOptions");
		print(awsVpnConnectionTunnelOptionResult);

		TypedQuery<Object[]> awsTransitGatewayQuery = session.createQuery(
				"select v.* from AwsEc2TransitGateway v" );
		List<Object[]> awsTransitGatewayResult = awsTransitGatewayQuery.getResultList();
		System.out.println("AwsEc2TransitGateways");
		print(awsTransitGatewayResult);

		TypedQuery<Object[]> awsTransitGatewayAttachmentQuery = session.createQuery(
				"select v.* from AwsEc2TransitGatewayAttachment v" );
		List<Object[]> awsTransitGatewayAttachmentResult = awsTransitGatewayAttachmentQuery.getResultList();
		System.out.println("AwsEc2TransitGatewayAttachments");
		print(awsTransitGatewayAttachmentResult);

		TypedQuery<Object[]> awsTransitGatewayRouteTableQuery = session.createQuery(
				"select v.* from AwsEc2TransitGatewayRouteTable v" );
		List<Object[]> awsTransitGatewayRouteTableResult = awsTransitGatewayRouteTableQuery.getResultList();
		System.out.println("AwsEc2TransitGatewayRouteTables");
		print(awsTransitGatewayRouteTableResult);

		TypedQuery<Object[]> awsNetworkInterfaceQuery = session.createQuery(
				"select n.* from AwsEc2NetworkInterface n" );
		List<Object[]> awsNetworkInterfaceResult = awsNetworkInterfaceQuery.getResultList();
		System.out.println("AwsEc2NetworkInterfaces");
		print(awsNetworkInterfaceResult);

		TypedQuery<Object[]> awsSpotFleetRequestQuery = session.createQuery(
				"select s.* from AwsEc2SpotFleetRequest s" );
		List<Object[]> awsSpotFleetRequestResult = awsSpotFleetRequestQuery.getResultList();
		System.out.println("AwsEc2SpotFleetRequests");
		print(awsSpotFleetRequestResult);

		TypedQuery<Object[]> awsSpotFleetRequestLaunchSpecificationQuery = session.createQuery(
				"select s.* from AwsEc2SpotFleetRequestLaunchSpecification s" );
		List<Object[]> awsSpotFleetRequestLaunchSpecificationResult =
				awsSpotFleetRequestLaunchSpecificationQuery.getResultList();
		System.out.println("AwsEc2SpotFleetRequestLaunchSpecifications");
		print(awsSpotFleetRequestLaunchSpecificationResult);

		TypedQuery<Object[]> awsDhcpOptionsQuery = session.createQuery(
				"select d.* from AwsEc2DhcpOptions d" );
		List<Object[]> awsDhcpOptionsResult = awsDhcpOptionsQuery.getResultList();
		System.out.println("AwsEc2DhcpOptions");
		print(awsDhcpOptionsResult);

		TypedQuery<Object[]> awsLaunchTemplateQuery = session.createQuery(
				"select l.* from AwsEc2LaunchTemplate l" );
		List<Object[]> awsLaunchTemplateResult = awsLaunchTemplateQuery.getResultList();
		System.out.println("AwsEc2LaunchTemplates");
		print(awsLaunchTemplateResult);

		TypedQuery<Object[]> awsLaunchTemplateVersionQuery = session.createQuery(
				"select l.* from AwsEc2LaunchTemplateVersion l" );
		List<Object[]> awsLaunchTemplateVersionResult = awsLaunchTemplateVersionQuery.getResultList();
		System.out.println("AwsEc2LaunchTemplateVersions");
		print(awsLaunchTemplateVersionResult);

		TypedQuery<Object[]> awsManagedPrefixListQuery = session.createQuery(
				"select m.* from AwsEc2ManagedPrefixList m" );
		List<Object[]> awsManagedPrefixListResult = awsManagedPrefixListQuery.getResultList();
		System.out.println("AwsEc2ManagedPrefixLists");
		print(awsManagedPrefixListResult);

		TypedQuery<Object[]> awsTrafficMirrorSessionQuery = session.createQuery(
				"select t.* from AwsEc2TrafficMirrorSession t" );
		List<Object[]> awsTrafficMirrorSessionResult = awsTrafficMirrorSessionQuery.getResultList();
		System.out.println("AwsEc2TrafficMirrorSessions");
		print(awsTrafficMirrorSessionResult);

		TypedQuery<Object[]> awsTrafficMirrorFilterQuery = session.createQuery(
				"select t.* from AwsEc2TrafficMirrorFilter t" );
		List<Object[]> awsTrafficMirrorFilterResult = awsTrafficMirrorFilterQuery.getResultList();
		System.out.println("AwsEc2TrafficMirrorFilters");
		print(awsTrafficMirrorFilterResult);

		TypedQuery<Object[]> awsTrafficMirrorTargetQuery = session.createQuery(
				"select t.* from AwsEc2TrafficMirrorTarget t" );
		List<Object[]> awsTrafficMirrorTargetResult = awsTrafficMirrorTargetQuery.getResultList();
		System.out.println("AwsEc2TrafficMirrorTargets");
		print(awsTrafficMirrorTargetResult);

		TypedQuery<Object[]> awsSecurityGroupIpPermissionQuery = session.createQuery(
				"select p.* from AwsEc2SecurityGroupIpPermission p" );
		List<Object[]> awsSecurityGroupIpPermissionResult = awsSecurityGroupIpPermissionQuery.getResultList();
		System.out.println("AwsEc2SecurityGroupIpPermissions");
		print(awsSecurityGroupIpPermissionResult);

		TypedQuery<Object[]> awsSnapshotQuery = session.createQuery(
				"select s.* from AwsEc2Snapshot s" );
		List<Object[]> awsSnapshotResult = awsSnapshotQuery.getResultList();
		System.out.println("AwsEc2Snapshots");
		print(awsSnapshotResult);

		TypedQuery<Object[]> awsSnapshotAttributeQuery = session.createQuery(
				"select s.* from AwsEc2SnapshotAttribute s" );
		List<Object[]> awsSnapshotAttributeResult = awsSnapshotAttributeQuery.getResultList();
		System.out.println("AwsEc2SnapshotAttributes");
		print(awsSnapshotAttributeResult);

		TypedQuery<Object[]> awsEbsEncryptionByDefaultQuery = session.createQuery(
				"select e.* from AwsEc2EbsEncryptionByDefault e" );
		List<Object[]> awsEbsEncryptionByDefaultResult = awsEbsEncryptionByDefaultQuery.getResultList();
		System.out.println("AwsEc2EbsEncryptionByDefaults");
		print(awsEbsEncryptionByDefaultResult);

		TypedQuery<Object[]> awsFlowLogsQuery = session.createQuery(
				"select f.* from AwsEc2FlowLogs f" );
		List<Object[]> awsFlowLogsResult = awsFlowLogsQuery.getResultList();
		System.out.println("AwsEc2FlowLogs");
		print(awsFlowLogsResult);

		// RDS
		TypedQuery<Object[]> awsDbClusterQuery = session.createQuery(
				"select i.* from AwsDBCluster i" );
		List<Object[]> awsDbClusterResult = awsDbClusterQuery.getResultList();
		System.out.println("AwsDBCluster");
		print(awsDbClusterResult);

		TypedQuery<Object[]> awsDbClusterSnapshotQuery = session.createQuery(
				"select i.* from AwsDBClusterSnapshot i" );
		List<Object[]> awsDbClusterSnapshotResult = awsDbClusterSnapshotQuery.getResultList();
		System.out.println("AwsDBClusterSnapshot");
		print(awsDbClusterSnapshotResult);

		TypedQuery<Object[]> awsDbInstanceQuery = session.createQuery(
				"select i.* from AwsDBInstance i" );
		List<Object[]> awsDbInstanceResult = awsDbInstanceQuery.getResultList();
		System.out.println("AwsDBInstance");
		print(awsDbInstanceResult);

		TypedQuery<Object[]> awsDBSnapshotQuery = session.createQuery(
				"select i.* from AwsDBSnapshot i" );
		List<Object[]> awsDBSnapshotResult = awsDBSnapshotQuery.getResultList();
		System.out.println("AwsDBSnapshot");
		print(awsDBSnapshotResult);

		TypedQuery<Object[]> awsDBSnapshotAttributeQuery = session.createQuery(
				"select i.* from AwsDBSnapshotAttribute i" );
		List<Object[]> awsDBSnapshotAttributeResult = awsDBSnapshotAttributeQuery.getResultList();
		System.out.println("AwsDBSnapshotAttribute");
		print(awsDBSnapshotAttributeResult);

		TypedQuery<Object[]> awsEventSubscriptionQuery = session.createQuery(
				"select i.* from AwsEventSubscription i" );
		List<Object[]> awsEventSubscriptionResult = awsEventSubscriptionQuery.getResultList();
		System.out.println("AwsEventSubscription");
		print(awsEventSubscriptionResult);


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
				"select f.* from AwsEcsCluster f" );
		List<Object[]> awsClusterResult = awsClusterQuery.getResultList();
		System.out.println("AwsEcsClusters");
		print(awsClusterResult);

		TypedQuery<Object[]> awsContainerDefinitionQuery = session.createQuery(
				"select f.* from AwsEcsContainerDefinition f" );
		List<Object[]> awsContainerDefinitionResult = awsContainerDefinitionQuery.getResultList();
		System.out.println("AwsEcsContainerDefinitions");
		print(awsContainerDefinitionResult);

		TypedQuery<Object[]> awsServiceQuery = session.createQuery(
				"select f.* from AwsEcsService f" );
		List<Object[]> awsServiceResult = awsServiceQuery.getResultList();
		System.out.println("AwsEcsServices");
		print(awsServiceResult);

		TypedQuery<Object[]> awsTaskDefinitionQuery = session.createQuery(
				"select f.* from AwsEcsTaskDefinition f" );
		List<Object[]> awsTaskDefinitionResult = awsTaskDefinitionQuery.getResultList();
		System.out.println("AwsEcsTaskDefinitions");
		print(awsTaskDefinitionResult);

		TypedQuery<Object[]> awsTaskSetQuery = session.createQuery(
				"select f.* from AwsEcsTaskSet f" );
		List<Object[]> awsTaskSetResult = awsTaskSetQuery.getResultList();
		System.out.println("AwsEcsTaskSets");
		print(awsTaskSetResult);

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

		TypedQuery<Object[]> awsBucketAclQuery = session.createQuery(
				"select f.* from AwsBucketAcl f" );
		List<Object[]> awsBucketAclResult = awsBucketAclQuery.getResultList();
		System.out.println("AwsBucketAcl");
		print(awsBucketAclResult);

		TypedQuery<Object[]> awsBucketVersioningQuery = session.createQuery(
				"select f.* from AwsBucketVersioning f" );
		List<Object[]> awsBucketVersioningResult = awsBucketVersioningQuery.getResultList();
		System.out.println("AwsBucketVersioning");
		print(awsBucketVersioningResult);

	TypedQuery<Object[]> awsBucketPolicyQuery = session.createQuery(
				"select f.* from AwsBucketPolicy f" );
		List<Object[]> awsBucketPolicyResult = awsBucketPolicyQuery.getResultList();
		System.out.println("AwsBucketPolicy");
		print(awsBucketPolicyResult);

		TypedQuery<Object[]> awsLoggingEnabledQuery = session.createQuery(
				"select f.* from AwsLoggingEnabled f" );
		List<Object[]> awsLoggingEnabledResult = awsLoggingEnabledQuery.getResultList();
		System.out.println("AwsLoggingEnabled");
		print(awsLoggingEnabledResult);

		TypedQuery<Object[]> awsObjectLockConfigurationQuery = session.createQuery(
				"select f.* from AwsObjectLockConfiguration f" );
		List<Object[]> awsObjectLockConfigurationResult = awsObjectLockConfigurationQuery.getResultList();
		System.out.println("AwsObjectLockConfiguration");
		print(awsObjectLockConfigurationResult);

		TypedQuery<Object[]> awsPolicyStatusQuery = session.createQuery(
				"select f.* from AwsPolicyStatus f" );
		List<Object[]> awsPolicyStatusResult = awsPolicyStatusQuery.getResultList();
		System.out.println("AwsPolicyStatus");
		print(awsPolicyStatusResult);

		TypedQuery<Object[]> awsLifeCycleRuleQuery = session.createQuery(
				"select f.* from AwsLifeCycleRule f" );
		List<Object[]> awsLifeCycleRuleResult = awsLifeCycleRuleQuery.getResultList();
		System.out.println("AwsLifeCycleRule");
		print(awsLifeCycleRuleResult);

		TypedQuery<Object[]> awsPublicAccessBlockConfigurationQuery = session.createQuery(
				"select f.* from AwsPublicAccessBlockConfiguration f" );
		List<Object[]> awsPublicAccessBlockConfigurationResult = awsPublicAccessBlockConfigurationQuery.getResultList();
		System.out.println("AwsPublicAccessBlockConfiguration");
		print(awsPublicAccessBlockConfigurationResult);

		TypedQuery<Object[]> awsServerSideEncryptionRuleQuery = session.createQuery(
				"select f.* from AwsServerSideEncryptionRule f" );
		List<Object[]> awsServerSideEncryptionRuleResult = awsServerSideEncryptionRuleQuery.getResultList();
		System.out.println("AwsServerSideEncryptionConfiguration");
		print(awsServerSideEncryptionRuleResult);

		// KMS
		TypedQuery<Object[]> awsKmsKeyQuery = session.createQuery(
				"select f.* from AwsKmsKey f" );
		List<Object[]> awsKmsKeyResult = awsKmsKeyQuery.getResultList();
		System.out.println("AwsKmsKey");
		print(awsKmsKeyResult);

		TypedQuery<Object[]> awsKmsKeyAliasQuery = session.createQuery(
				"select f.* from AwsKmsKeyAlias f" );
		List<Object[]> awsKmsKeyAliasResult = awsKmsKeyAliasQuery.getResultList();
		System.out.println("AwsKmsKeyAlias");
		print(awsKmsKeyAliasResult);

		TypedQuery<Object[]> awsKmsKeyPolicyQuery = session.createQuery(
				"select f.* from AwsKmsKeyPolicy f" );
		List<Object[]> awsKmsKeyPolicyResult = awsKmsKeyPolicyQuery.getResultList();
		System.out.println("AwsKmsKeyPolicy");
		print(awsKmsKeyPolicyResult);

		TypedQuery<Object[]> awsKmsKeyRotationStatusQuery = session.createQuery(
				"select f.* from AwsKmsKeyRotationStatus f" );
		List<Object[]> awsKmsKeyRotationStatusResult = awsKmsKeyRotationStatusQuery.getResultList();
		System.out.println("AwsKmsKeyRotationStatus");
		print(awsKmsKeyRotationStatusResult);

		// Secrets Manager
		TypedQuery<Object[]> awsSecretsManagerSecretQuery = session.createQuery(
				"select f.* from AwsSecretsManagerSecret f" );
		List<Object[]> awsSecretsManagerSecretResult = awsSecretsManagerSecretQuery.getResultList();
		System.out.println("AwsSecretsManagerSecret");
		print(awsSecretsManagerSecretResult);

		// CloudWatch
		TypedQuery<Object[]> awsCloudWatchCompositeAlarmQuery = session.createQuery(
				"select f.* from AwsCloudWatchCompositeAlarm f" );
		List<Object[]> awsCloudWatchCompositeAlarmResult = awsCloudWatchCompositeAlarmQuery.getResultList();
		System.out.println("AwsCloudWatchCompositeAlarm");
		print(awsCloudWatchCompositeAlarmResult);

		TypedQuery<Object[]> awsCloudWatchMetricAlarmQuery = session.createQuery(
				"select f.* from AwsCloudWatchMetricAlarm f" );
		List<Object[]> awsCloudWatchMetricAlarmResult = awsCloudWatchMetricAlarmQuery.getResultList();
		System.out.println("AwsCloudWatchMetricAlarm");
		print(awsCloudWatchMetricAlarmResult);

		// CloudWatch Logs
		TypedQuery<Object[]> awsCloudWatchLogsLogGroupQuery = session.createQuery(
				"select f.* from AwsCloudWatchLogsLogGroup f" );
		List<Object[]> awsCloudWatchLogsLogGroupResult = awsCloudWatchLogsLogGroupQuery.getResultList();
		System.out.println("AwsCloudWatchLogsLogGroup");
		print(awsCloudWatchLogsLogGroupResult);

		TypedQuery<Object[]> awsCloudWatchLogsMetricFilterQuery = session.createQuery(
				"select f.* from AwsCloudWatchLogsMetricFilter f" );
		List<Object[]> awsCloudWatchLogsMetricFilterResult = awsCloudWatchLogsMetricFilterQuery.getResultList();
		System.out.println("AwsCloudWatchLogsMetricFilter");
		print(awsCloudWatchLogsMetricFilterResult);

		// CloudTrail
		TypedQuery<Object[]> awsCloudTrailEventSelectorQuery = session.createQuery(
				"select f.* from AwsCloudTrailEventSelector f" );
		List<Object[]> awsCloudTrailEventSelectorResult = awsCloudTrailEventSelectorQuery.getResultList();
		System.out.println("AwsCloudTrailEventSelector");
		print(awsCloudTrailEventSelectorResult);

		TypedQuery<Object[]> awsCloudTrailTrailQuery = session.createQuery(
				"select f.* from AwsCloudTrailTrail f" );
		List<Object[]> awsCloudTrailTrailResult = awsCloudTrailTrailQuery.getResultList();
		System.out.println("AwsCloudTrailTrail");
		print(awsCloudTrailTrailResult);

		TypedQuery<Object[]> awsCloudTrailTrailStatusQuery = session.createQuery(
				"select f.* from AwsCloudTrailTrailStatus f" );
		List<Object[]> awsCloudTrailTrailStatusResult = awsCloudTrailTrailStatusQuery.getResultList();
		System.out.println("AwsCloudTrailTrailStatus");
		print(awsCloudTrailTrailStatusResult);

		// SNS
		TypedQuery<Object[]> awsSnsSubscriptionQuery = session.createQuery(
				"select f.* from AwsSnsSubscription f" );
		List<Object[]> awsSnsSubscriptionResult = awsSnsSubscriptionQuery.getResultList();
		System.out.println("AwsSnsSubscription");
		print(awsSnsSubscriptionResult);

		TypedQuery<Object[]> awsSnsTopicQuery = session.createQuery(
				"select f.* from AwsSnsTopic f" );
		List<Object[]> awsSnsTopicResult = awsSnsTopicQuery.getResultList();
		System.out.println("AwsSnsTopic");
		print(awsSnsTopicResult);
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
				"select u.resourceId, u.payload.primaryEmail, u.payload.lastLoginTime from GoogleUser u" );
		List<Object[]> userResult = userQuery.getResultList();
		System.out.println( "User" );
		print( userResult );
		TypedQuery<Object[]> groupQuery = session.createQuery(
				"select u.resourceId, u.payload.name, u.payload.email from GoogleGroup u" );
		List<Object[]> groupResult = groupQuery.getResultList();
		System.out.println( "Group" );
		print( groupResult );
		TypedQuery<Object[]> memberQuery = session.createQuery(
				"select u.resourceId, u.payload.email, u.payload.role, u.payload.type from GoogleMember u" );
		List<Object[]> memberResult = memberQuery.getResultList();
		System.out.println( "Member" );
		print( memberResult );
		TypedQuery<Object[]> roleQuery = session.createQuery(
				"select u.resourceId, u.payload.roleName from GoogleRole u" );
		List<Object[]> roleResult = roleQuery.getResultList();
		System.out.println( "Role" );
		print( roleResult );
		TypedQuery<Object[]> roleAssignmentQuery = session.createQuery(
				"select u.resourceId, u.payload.roleId, u.payload.assignedTo from GoogleRoleAssignment u" );
		List<Object[]> roleAssignmentResult = roleAssignmentQuery.getResultList();
		System.out.println( "Role assignment" );
		print( roleAssignmentResult );

		TypedQuery<Object[]> driveQuery = session.createQuery(
				"select u.resourceId, u.payload.name from GoogleDrive u" );
		List<Object[]> driveResult = driveQuery.getResultList();
		System.out.println( "Drive" );
		print( driveResult );
	}

	private static void testGoogleEndpointVerification(QuerySession session) {
		TypedQuery<Object[]> mobileQuery = session.createQuery(
				"select d.resourceId, d.payload.name, d.payload.email, d.payload.type, d.payload.status from GoogleMobileDevice d" );
		List<Object[]> mobileResult = mobileQuery.getResultList();
		System.out.println( "Mobile Device" );
		print( mobileResult );

		TypedQuery<Object[]> chromeQuery = session.createQuery(
				"select d.resourceId, d.payload.deviceId, d.payload.serialNumber, d.payload.status, d.payload.osVersion from GoogleChromeOsDevice d" );
		List<Object[]> chromeResult = chromeQuery.getResultList();
		System.out.println( "ChromeOS Device" );
		print( chromeResult );

		TypedQuery<Object[]> chromeAutoRenewQuery = session.createQuery(
				"""
						SELECT d.resourceId, d.payload.serialNumber, d.payload.willAutoRenew
						FROM GoogleChromeOsDevice d
						WHERE d.payload.willAutoRenew = false
						""" );
		List<Object[]> chromeAutoRenewResult = chromeAutoRenewQuery.getResultList();
		System.out.println( "ChromeOS Device - will not auto renew" );
		print( chromeAutoRenewResult );
	}

	private static void testGcp(QuerySession session) {
		TypedQuery<Object[]> organizationQuery = session.createQuery(
				"select i.resourceId, i.payload.displayName from GcpOrganization i" );
		List<Object[]> organizationResult = organizationQuery.getResultList();
		System.out.println( "Organization" );
		print( organizationResult );

		TypedQuery<Object[]> folderQuery = session.createQuery(
				"select i.resourceId, i.payload.displayName from GcpFolder i" );
		List<Object[]> folderResult = folderQuery.getResultList();
		System.out.println( "Folder" );
		print( folderResult );

		TypedQuery<Object[]> projectQuery = session.createQuery(
				"select i.resourceId, i.payload.projectId, i.payload.displayName from GcpProject i" );
		List<Object[]> projectResult = projectQuery.getResultList();
		System.out.println( "Project" );
		print( projectResult );

		TypedQuery<Object[]> assetQuery = session.createQuery(
				"select i.resourceId, i.payload.assetType from GcpAsset i" );
		List<Object[]> assetResult = assetQuery.getResultList();
		System.out.println( "Asset" );
		print( assetResult );

		TypedQuery<Object[]> instanceQuery = session.createQuery(
				"""
							SELECT i.resourceId as resourceId,
								i.payload.name,
								COALESCE(i.payload.shieldedInstanceConfig.enableSecureBoot, false)
									AND COALESCE(i.payload.shieldedInstanceConfig.enableVtpm, false)
									AND COALESCE(i.payload.shieldedInstanceConfig.enableIntegrityMonitoring, false) AS passed
							FROM GcpInstance i
						""" );
		List<Object[]> instanceResult = instanceQuery.getResultList();
		System.out.println( "Instance" );
		print( instanceResult );

		TypedQuery<Object[]> instance2Query = session.createQuery(
				"""
						SELECT i.resourceId,
							i.payload.name,
							NOT EXISTS (
								SELECT 1
								FROM UNNEST(i.payload.networkInterfacesList) AS ni
								JOIN UNNEST(ni.accessConfigsList) AS ac ON true
								WHERE ac.natIP IS NOT NULL
							) AS passed
						FROM GcpInstance i
						""" );
		List<Object[]> instance2Result = instance2Query.getResultList();
		System.out.println( "Instance - not publicly accessible" );
		print( instance2Result );

		TypedQuery<Object[]> policyQuery = session.createQuery(
				"select i.resourceId, i.payload.resource, i.payload.project from GcpIamPolicy i" );
		List<Object[]> policyResult = policyQuery.getResultList();
		System.out.println( "Policy" );
		print( policyResult );

		TypedQuery<Object[]> roleQuery = session.createQuery(
				"select i.resourceId, i.payload.title from GcpIamRole i" );
		List<Object[]> roleResult = roleQuery.getResultList();
		System.out.println( "Role" );
		print( roleResult );

		TypedQuery<Object[]> serviceAccountQuery = session.createQuery(
				"select i.resourceId, i.payload.email, i.payload.displayName from GcpIamServiceAccount i" );
		List<Object[]> serviceAccountResult = serviceAccountQuery.getResultList();
		System.out.println( "Service Account" );
		print( serviceAccountResult );

		TypedQuery<Object[]> bucketQuery = session.createQuery(
				"select i.resourceId, i.payload.location from GcpBucket i" );
		List<Object[]> bucketResult = bucketQuery.getResultList();
		System.out.println( "Bucket" );
		print( bucketResult );

		TypedQuery<Object[]> firewallQuery = session.createQuery(
				"select f.resourceId, f.payload.name, f.payload.direction, f.payload.disabled from GcpFirewallRule f" );
		List<Object[]> firewallResult = firewallQuery.getResultList();
		System.out.println( "Firewall Rule" );
		print( firewallResult );

		TypedQuery<Object[]> firewallSshQuery = session.createQuery(
				"""
						SELECT f.resourceId, f.payload.name, f.payload.direction
						FROM GcpFirewallRule f
						JOIN UNNEST(f.payload.allowedList) AS a ON true
						JOIN UNNEST(a.portsList) AS p ON true
						WHERE f.payload.direction = 'INGRESS'
						AND (a.iPProtocol = 'tcp' OR a.iPProtocol = 'all')
						AND (p = '22' OR p = '0-65535')
						AND EXISTS (
							SELECT 1 FROM UNNEST(f.payload.sourceRangesList) AS sr WHERE sr = '0.0.0.0/0'
						)
						""" );
		List<Object[]> firewallSshResult = firewallSshQuery.getResultList();
		System.out.println( "Firewall Rule - unrestricted SSH ingress" );
		print( firewallSshResult );

		TypedQuery<Object[]> sqlQuery = session.createQuery(
				"select s.resourceId, s.payload.name, s.payload.databaseVersion, s.payload.state from GcpSqlInstance s" );
		List<Object[]> sqlResult = sqlQuery.getResultList();
		System.out.println( "SQL Instance" );
		print( sqlResult );

		TypedQuery<Object[]> sqlPublicIpQuery = session.createQuery(
				"""
						SELECT s.resourceId, s.payload.name,
						s.payload.settings.ipConfiguration.ipv4Enabled AS publicIpEnabled
						FROM GcpSqlInstance s
						WHERE s.payload.settings.ipConfiguration.ipv4Enabled = true
						""" );
		List<Object[]> sqlPublicIpResult = sqlPublicIpQuery.getResultList();
		System.out.println( "SQL Instance - public IP enabled" );
		print( sqlPublicIpResult );

		TypedQuery<Object[]> gkeQuery = session.createQuery(
				"select c.resourceId, c.payload.name, c.payload.location, c.payload.status from GcpGkeCluster c" );
		List<Object[]> gkeResult = gkeQuery.getResultList();
		System.out.println( "GKE Cluster" );
		print( gkeResult );

		TypedQuery<Object[]> gkeNetworkPolicyQuery = session.createQuery(
				"""
						SELECT c.resourceId, c.payload.name,
						c.payload.networkPolicy.enabled AS networkPolicyEnabled
						FROM GcpGkeCluster c
						""" );
		List<Object[]> gkeNetworkPolicyResult = gkeNetworkPolicyQuery.getResultList();
		System.out.println( "GKE Cluster - network policy" );
		print( gkeNetworkPolicyResult );

		TypedQuery<Object[]> kmsQuery = session.createQuery(
				"select k.resourceId, k.payload.name, k.payload.purpose from GcpKmsCryptoKey k" );
		List<Object[]> kmsResult = kmsQuery.getResultList();
		System.out.println( "KMS CryptoKey" );
		print( kmsResult );

		TypedQuery<Object[]> kmsRotationQuery = session.createQuery(
				"""
						SELECT k.resourceId, k.payload.name,
						k.payload.rotationPeriod IS NOT NULL AS rotationConfigured
						FROM GcpKmsCryptoKey k
						WHERE k.payload.purpose = 'ENCRYPT_DECRYPT'
						""" );
		List<Object[]> kmsRotationResult = kmsRotationQuery.getResultList();
		System.out.println( "KMS CryptoKey - rotation configured" );
		print( kmsRotationResult );

		TypedQuery<Object[]> dnsQuery = session.createQuery(
				"select z.resourceId, z.payload.name, z.payload.dnsName from GcpDnsManagedZone z" );
		List<Object[]> dnsResult = dnsQuery.getResultList();
		System.out.println( "DNS Managed Zone" );
		print( dnsResult );

		TypedQuery<Object[]> dnssecQuery = session.createQuery(
				"""
						SELECT z.resourceId, z.payload.name,
						z.payload.dnsSecConfig.state AS dnssecState
						FROM GcpDnsManagedZone z
						""" );
		List<Object[]> dnssecResult = dnssecQuery.getResultList();
		System.out.println( "DNS Managed Zone - DNSSEC state" );
		print( dnssecResult );
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

		TypedQuery<Object[]> issueQuery = session.createQuery(
				"SELECT u.* FROM JiraCloudIssue u");
		List<Object[]> issueResult = issueQuery.getResultList();
		System.out.println( "Issue" );
		print( issueResult );

		TypedQuery<Object[]> serverInfoQuery = session.createQuery(
				"select u.* from JiraCloudServerInfo u" );
		List<Object[]> serverInfoResult = serverInfoQuery.getResultList();
		System.out.println( "Server info" );
		print( serverInfoResult );
	}

	private static void testJiraCloudAdmin(QuerySession session) {
		TypedQuery<Object[]> orgQuery = session.createQuery(
				"select o.* from JiraCloudAdminOrg o" );
		List<Object[]> orgResult = orgQuery.getResultList();
		System.out.println( "Org" );
		print( orgResult );

		TypedQuery<Object[]> directoryQuery = session.createQuery(
				"select o.* from JiraCloudAdminDirectory o" );
		List<Object[]> directoryResult = directoryQuery.getResultList();
		System.out.println( "Directory" );
		print( directoryResult );

		TypedQuery<Object[]> userQuery = session.createQuery(
				"select u.* from JiraCloudAdminUser u" );
		List<Object[]> userResult = userQuery.getResultList();
		System.out.println( "User" );
		print( userResult );
	}

	private static void testEntityView(QuerySession session) {
		TypedQuery<Object[]> entityViewQuery = session.createQuery(
				"select t.id, e.text1 from " + name( TestEntityView.class ) + " t, unnest(t.elements) e" );
		List<Object[]> entityViewResult = entityViewQuery.getResultList();
		print( entityViewResult, "id", "text1" );
		TypedQuery<Object[]> entityViewQuery2 = session.createQuery(
				"select t.id, array_contains(t.enums, 'A') from " + name( TestEntityView.class ) + " t" );
		List<Object[]> entityViewResult2 = entityViewQuery2.getResultList();
		print( entityViewResult2, "id", "enums" );
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

		TypedQuery<Object[]> managedDevices = session.createQuery(
				"select a.* from AzureManagedDevice a" );
		List<Object[]> managedDevicesResult = managedDevices.getResultList();
		System.out.println( "Managed Devices" );
		print( managedDevicesResult );

		TypedQuery<Object[]> organizationQuery = session.createQuery(
				"select o.* from AzureOrganization o" );
		List<Object[]> organizationResult = organizationQuery.getResultList();
		System.out.println( "Organizations" );
		print( organizationResult );

		TypedQuery<Object[]> alertQuery = session.createQuery( "select a.payload.severity from AzureAlert a" );
		List<Object[]> alertResult = alertQuery.getResultList();
		System.out.println( "Alerts" );
		print( alertResult );

		TypedQuery<Object[]> incidentQuery = session.createQuery("select i.payload.* from AzureIncident i");
		List<Object[]> incidentResult = incidentQuery.getResultList();
		System.out.println( "Incidents" );
		print( incidentResult );

		TypedQuery<Object[]> riskyUserQuery = session.createQuery(
				"select u.payload.userPrincipalName, u.payload.userDisplayName, u.payload.riskLastUpdatedDateTime"
				+ " from AzureRiskyUser u" );
		List<Object[]> riskyUserResult = riskyUserQuery.getResultList();
		System.out.println( "Risky users" );
		print( riskyUserResult );

		TypedQuery<Object[]> servicePlanQuery = session.createQuery(
				"select s.* from AzureAvailableServicePlan s" );
		List<Object[]> subscribedSkuResult = servicePlanQuery.getResultList();
		System.out.println( "Service plan" );
		print( subscribedSkuResult );

		TypedQuery<Object[]> servicePlanDetailQuery = session.createQuery(
				"select s.* from AzureServicePlan s" );
		List<Object[]> servicePlanDetailResult = servicePlanDetailQuery.getResultList();
		System.out.println( "Service plan details" );
		print( servicePlanDetailResult );

		TypedQuery<Object[]> userLastSignInQuery = session.createQuery(
				"select u.tenantId, u.payload.signInActivity from AzureUserLastSignInActivity u" );
		List<Object[]> userLastSignInResult = userLastSignInQuery.getResultList();
		System.out.println( "User last sign-in activity" );
		print( userLastSignInResult );

		TypedQuery<Object[]> secureScoreQuery = session.createQuery(
				"select s.* from AzureSecureScore s" );
		List<Object[]> secureScoreResult = secureScoreQuery.getResultList();
		System.out.println( "Secure scores" );
		print( secureScoreResult );

		TypedQuery<Object[]> secureScoreControlProfileQuery = session.createQuery(
				"select s.* from AzureSecureScoreControlProfile s" );
		List<Object[]> secureScoreControlProfileResult = secureScoreControlProfileQuery.getResultList();
		System.out.println( "Secure score control profiles" );
		print( secureScoreControlProfileResult );
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

		TypedQuery<Object[]> patchAssessmentQuery = session.createQuery(
				"select r.vmName, r.criticalAndSecurityPatchCount, r.statusCode"
				+ " from AzurePatchAssessmentResult r"
				+ " where r.criticalAndSecurityPatchCount > 0" );
		List<Object[]> patchAssessmentResult = patchAssessmentQuery.getResultList();
		System.out.println( "VMs with open critical/security patches" );
		print( patchAssessmentResult );

		TypedQuery<Object[]> securityAssessmentQuery = session.createQuery(
				"select a.displayName, a.severity, a.resourceId"
				+ " from AzureSecurityAssessment a"
				+ " where a.statusCode = 'Unhealthy'"
				+ " order by a.severity" );
		List<Object[]> securityAssessmentResult = securityAssessmentQuery.getResultList();
		System.out.println( "Unhealthy security assessments" );
		print( securityAssessmentResult );

		TypedQuery<Object[]> roleAssignmentQuery = session.createQuery(
				"select r.payload.principalId, r.payload.principalType, r.payload.roleDefinitionId, r.subscriptionId"
				+ " from AzureRoleAssignment r" );
		List<Object[]> roleAssignmentResult = roleAssignmentQuery.getResultList();
		System.out.println( "Role assignments" );
		print( roleAssignmentResult );

		TypedQuery<Object[]> diskQuery = session.createQuery(
				"select d.* from AzureDisk d" );
		List<Object[]> diskResult = diskQuery.getResultList();
		System.out.println( "Disks" );
		print( diskResult );

		TypedQuery<Object[]> nsgQuery = session.createQuery(
				"select n.* from AzureNetworkSecurityGroup n" );
		List<Object[]> nsgResult = nsgQuery.getResultList();
		System.out.println( "Network security groups" );
		print( nsgResult );

		TypedQuery<Object[]> resourceGroupQuery = session.createQuery(
				"select rg.* from AzureResourceGroup rg" );
		List<Object[]> resourceGroupResult = resourceGroupQuery.getResultList();
		System.out.println( "Resource groups" );
		print( resourceGroupResult );

		TypedQuery<Object[]> subscriptionQuery = session.createQuery(
				"select s.* from AzureSubscription s" );
		List<Object[]> subscriptionResult = subscriptionQuery.getResultList();
		System.out.println( "Subscriptions" );
		print( subscriptionResult );

		TypedQuery<Object[]> tenantQuery = session.createQuery(
				"select t.* from AzureTenant t" );
		List<Object[]> tenantResult = tenantQuery.getResultList();
		System.out.println( "Tenants" );
		print( tenantResult );
	}

	private static void testObservatory(QuerySession session) {
		TypedQuery<Object[]> observatoryScanQuery = session.createQuery(
				"select c.* from ObservatoryScan c" );
		List<Object[]> observatoryCheckResult = observatoryScanQuery.getResultList();
		System.out.println( "ObservatoryScan" );
		print( observatoryCheckResult );
	}

	private static void testDatadog(QuerySession session) {
		// Logs: find recent error logs
		TypedQuery<Object[]> logQuery = session.createQuery(
				"SELECT l.id, l.host, l.service, l.status, l.message FROM DatadogLog l WHERE l.status = 'error'" );
		List<Object[]> logResult = logQuery.getResultList();
		System.out.println( "Datadog Error Logs" );
		print( logResult );

		// Synthetics: list all live web uptime tests
		TypedQuery<Object[]> syntheticsQuery = session.createQuery(
				"SELECT t.publicId, t.name, t.type, t.status FROM DatadogSyntheticsTest t WHERE t.status = 'live'" );
		List<Object[]> syntheticsResult = syntheticsQuery.getResultList();
		System.out.println( "Datadog Live Synthetics Tests" );
		print( syntheticsResult );

		// Monitors: show alerting infrastructure monitors
		TypedQuery<Object[]> monitorQuery = session.createQuery(
				"SELECT m.id, m.name, m.overallState, m.priority FROM DatadogMonitor m WHERE m.overallState = 'Alert'" );
		List<Object[]> monitorResult = monitorQuery.getResultList();
		System.out.println( "Datadog Alerting Monitors" );
		print( monitorResult );

		// Hosts: list all hosts
		TypedQuery<Object[]> hostQuery = session.createQuery(
				"SELECT h.id, h.name, h.hostName, h.up, h.muted, h.lastReportedTime FROM DatadogHost h" );
		List<Object[]> hostResult = hostQuery.getResultList();
		System.out.println( "Datadog Hosts" );
		print( hostResult );

		// Hosts: find hosts that are down
		TypedQuery<Object[]> hostDownQuery = session.createQuery(
				"SELECT h.id, h.name, h.hostName, h.up, h.muted FROM DatadogHost h WHERE h.up = false" );
		List<Object[]> hostDownResult = hostDownQuery.getResultList();
		System.out.println( "Datadog Down Hosts" );
		print( hostDownResult );

		// Users: list all users with MFA and disabled status
		TypedQuery<Object[]> userQuery = session.createQuery(
				"SELECT u.id, u.name, u.email, u.status, u.mfaEnabled, u.disabled, u.serviceAccount FROM DatadogUser u" );
		List<Object[]> userResult = userQuery.getResultList();
		System.out.println( "Datadog Users" );
		print( userResult );

		// Users: find users without MFA enabled
		TypedQuery<Object[]> userNoMfaQuery = session.createQuery(
				"""
						SELECT u.id, u.name, u.email, u.status
						FROM DatadogUser u
						WHERE (u.mfaEnabled = false OR u.mfaEnabled IS NULL)
						AND u.disabled = false
						AND (u.serviceAccount = false OR u.serviceAccount IS NULL)
						""" );
		List<Object[]> userNoMfaResult = userNoMfaQuery.getResultList();
		System.out.println( "Datadog Users - MFA not enabled (active, non-service accounts)" );
		print( userNoMfaResult );

		// Security Monitoring Rules: list all rules
		TypedQuery<Object[]> ruleQuery = session.createQuery(
				"SELECT r.id, r.name, r.type, r.enabled FROM DatadogSecurityMonitoringRule r" );
		List<Object[]> ruleResult = ruleQuery.getResultList();
		System.out.println( "Datadog Security Monitoring Rules" );
		print( ruleResult );

		// Security Monitoring Rules: find disabled rules
		TypedQuery<Object[]> ruleDisabledQuery = session.createQuery(
				"SELECT r.id, r.name, r.type FROM DatadogSecurityMonitoringRule r WHERE r.enabled = false" );
		List<Object[]> ruleDisabledResult = ruleDisabledQuery.getResultList();
		System.out.println( "Datadog Security Monitoring Rules - disabled" );
		print( ruleDisabledResult );

		// Organization settings: SAML and security posture
		TypedQuery<Object[]> orgQuery = session.createQuery(
				"""
						SELECT o.publicId, o.name,
						o.samlEnabled, o.samlStrictMode,
						o.samlIdpInitiatedLoginEnabled, o.samlAutocreateUsersEnabled,
						o.samlIdpMetadataUploaded, o.privateWidgetShare
						FROM DatadogOrganizationSettings o
						""" );
		List<Object[]> orgResult = orgQuery.getResultList();
		System.out.println( "Datadog Organization Settings" );
		print( orgResult );

		// Organization settings: flag orgs where SAML strict mode is not enabled
		TypedQuery<Object[]> orgNoSamlStrictQuery = session.createQuery(
				"""
						SELECT o.publicId, o.name, o.samlEnabled, o.samlStrictMode
						FROM DatadogOrganizationSettings o
						WHERE o.samlStrictMode = false OR o.samlStrictMode IS NULL
						""" );
		List<Object[]> orgNoSamlStrictResult = orgNoSamlStrictQuery.getResultList();
		System.out.println( "Datadog Organization Settings - SAML strict mode not enforced" );
		print( orgNoSamlStrictResult );

		// CSM Findings: all findings
		TypedQuery<Object[]> findingQuery = session.createQuery(
				"SELECT f.id, f.resource, f.resourceType, f.ruleName, f.status, f.evaluation, f.muted FROM DatadogCsmFinding f" );
		List<Object[]> findingResult = findingQuery.getResultList();
		System.out.println( "Datadog CSM Findings" );
		print( findingResult );

		// CSM Findings: failing, non-muted findings
		TypedQuery<Object[]> findingFailQuery = session.createQuery(
				"""
						SELECT f.id, f.resource, f.resourceType, f.ruleName, f.status
						FROM DatadogCsmFinding f
						WHERE f.evaluation = 'fail'
						AND (f.muted = false OR f.muted IS NULL)
						""" );
		List<Object[]> findingFailResult = findingFailQuery.getResultList();
		System.out.println( "Datadog CSM Findings - failing and not muted" );
		print( findingFailResult );

		// Security Signals: all signals in last 24h
		TypedQuery<Object[]> signalQuery = session.createQuery(
				"SELECT s.id, s.message, s.timestamp FROM DatadogSecuritySignal s" );
		List<Object[]> signalResult = signalQuery.getResultList();
		System.out.println( "Datadog Security Signals (last 24h)" );
		print( signalResult );

		// Audit Logs: all events in last 24h
		TypedQuery<Object[]> auditQuery = session.createQuery(
				"SELECT a.id, a.message, a.service, a.timestamp FROM DatadogAuditLog a" );
		List<Object[]> auditResult = auditQuery.getResultList();
		System.out.println( "Datadog Audit Logs (last 24h)" );
		print( auditResult );

		// API Keys: all keys with last used date
		TypedQuery<Object[]> apiKeyQuery = session.createQuery(
				"SELECT k.id, k.name, k.last4, k.category, k.createdAt, k.dateLastUsed FROM DatadogApiKey k" );
		List<Object[]> apiKeyResult = apiKeyQuery.getResultList();
		System.out.println( "Datadog API Keys" );
		print( apiKeyResult );

		// API Keys: keys never used
		TypedQuery<Object[]> apiKeyUnusedQuery = session.createQuery(
				"SELECT k.id, k.name, k.last4, k.createdAt FROM DatadogApiKey k WHERE k.dateLastUsed IS NULL" );
		List<Object[]> apiKeyUnusedResult = apiKeyUnusedQuery.getResultList();
		System.out.println( "Datadog API Keys - never used" );
		print( apiKeyUnusedResult );

		// Roles: all roles with user counts
		TypedQuery<Object[]> roleQuery2 = session.createQuery(
				"SELECT r.id, r.name, r.userCount FROM DatadogRole r" );
		List<Object[]> roleResult2 = roleQuery2.getResultList();
		System.out.println( "Datadog Roles" );
		print( roleResult2 );

		// Application Keys: all keys
		TypedQuery<Object[]> appKeyQuery = session.createQuery(
				"SELECT k.id, k.name, k.last4, k.createdAt, k.lastUsedAt FROM DatadogApplicationKey k" );
		List<Object[]> appKeyResult = appKeyQuery.getResultList();
		System.out.println( "Datadog Application Keys" );
		print( appKeyResult );

		// Application Keys: keys with no OAuth scopes (unrestricted)
		TypedQuery<Object[]> appKeyNoScopesQuery = session.createQuery(
				"SELECT k.id, k.name, k.createdAt FROM DatadogApplicationKey k WHERE CARDINALITY(k.scopes) = 0" );
		List<Object[]> appKeyNoScopesResult = appKeyNoScopesQuery.getResultList();
		System.out.println( "Datadog Application Keys - no OAuth scopes (unrestricted)" );
		print( appKeyNoScopesResult );

		// Monitor Downtimes: all scheduled downtimes
		TypedQuery<Object[]> downtimeQuery = session.createQuery(
				"SELECT d.id, d.scope, d.status, d.message, d.created, d.modified FROM DatadogMonitorDowntime d" );
		List<Object[]> downtimeResult = downtimeQuery.getResultList();
		System.out.println( "Datadog Monitor Downtimes" );
		print( downtimeResult );

		// Monitor Downtimes: active downtimes (not canceled)
		TypedQuery<Object[]> downtimeActiveQuery = session.createQuery(
				"SELECT d.id, d.scope, d.status, d.created FROM DatadogMonitorDowntime d WHERE d.status = 'active'" );
		List<Object[]> downtimeActiveResult = downtimeActiveQuery.getResultList();
		System.out.println( "Datadog Monitor Downtimes - active" );
		print( downtimeActiveResult );

		// Permissions: full RBAC permission catalog
		TypedQuery<Object[]> permissionQuery = session.createQuery(
				"SELECT p.id, p.name, p.displayName, p.groupName, p.restricted FROM DatadogPermission p" );
		List<Object[]> permissionResult = permissionQuery.getResultList();
		System.out.println( "Datadog Permissions" );
		print( permissionResult );

		// Permissions: restricted permissions only
		TypedQuery<Object[]> permissionRestrictedQuery = session.createQuery(
				"SELECT p.id, p.name, p.displayName, p.groupName FROM DatadogPermission p WHERE p.restricted = true" );
		List<Object[]> permissionRestrictedResult = permissionRestrictedQuery.getResultList();
		System.out.println( "Datadog Permissions - restricted" );
		print( permissionRestrictedResult );
	}

	private static com.datadog.api.client.ApiClient createDatadogApiClient() {
		com.datadog.api.client.ApiClient client = new com.datadog.api.client.ApiClient();
		client.addDefaultHeader( "DD-API-KEY", DATADOG_API_KEY );
		client.addDefaultHeader( "DD-APPLICATION-KEY", DATADOG_APP_KEY );
		if ( !DATADOG_SITE.equals( "datadoghq.com" ) ) {
			client.setServerVariables( new java.util.HashMap<>( java.util.Map.of( "site", DATADOG_SITE ) ) );
		}
		return client;
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

	private static ObservatoryClient createObservatoryClient() {
		return new ObservatoryClient(OBSERVATORY_HOST);
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

	private static com.blazebit.query.connector.jira.cloud.admin.invoker.ApiClient createJiraCloudAdminOrganizationApiClient() {
		com.blazebit.query.connector.jira.cloud.admin.invoker.ApiClient apiClient = new com.blazebit.query.connector.jira.cloud.admin.invoker.ApiClient();
		apiClient.addDefaultHeader( "Authorization", "Bearer " + JIRA_CLOUD_ADMIN_API_KEY );
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

	private static ResourceGraphClientAccessor createResourceGraphClient(AzureResourceManager resourceManager) {
		AzureProfile profile = new AzureProfile( AZURE_TENANT_ID, null, AzureEnvironment.AZURE );
		ClientSecretCredential credentials = new ClientSecretCredentialBuilder()
				.clientId( AZURE_CLIENT_ID )
				.clientSecret( AZURE_CLIENT_SECRET )
				.tenantId( AZURE_TENANT_ID )
				.build();
		ResourceGraphManager manager = ResourceGraphManager.authenticate( credentials, profile );
		return ResourceGraphClientAccessor.create( AZURE_TENANT_ID, List.of( resourceManager.subscriptionId() ), manager );
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
