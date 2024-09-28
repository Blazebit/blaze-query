package com.blazebit.query.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.fluent.models.VirtualMachineInner;
import com.azure.resourcemanager.storage.fluent.models.BlobServicePropertiesInner;
import com.azure.resourcemanager.storage.fluent.models.StorageAccountInner;
import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.azure.graph.AzureGraphConnectorConfig;
import com.blazebit.query.connector.azure.resourcemanager.AzureResourceManagerConnectorConfig;
import com.blazebit.query.connector.gitlab.GitlabConnectorConfig;
import com.blazebit.query.connector.gitlab.GroupMember;
import com.blazebit.query.connector.gitlab.ProjectMember;
import com.blazebit.query.connector.gitlab.ProjectProtectedBranch;
import com.blazebit.query.impl.QueryContextImpl;
import com.blazebit.query.impl.calcite.CalciteDataSource;
import com.blazebit.query.spi.Queries;
import com.blazebit.query.spi.QueryContextBuilder;
import com.microsoft.graph.beta.models.Application;
import com.microsoft.graph.beta.models.ConditionalAccessPolicy;
import com.microsoft.graph.beta.models.User;
import com.microsoft.graph.beta.serviceclient.GraphServiceClient;
import org.apache.calcite.avatica.jdbc.JdbcMeta;
import org.apache.calcite.avatica.remote.Driver.Serialization;
import org.apache.calcite.avatica.server.HttpServer;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Project;
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

/**
 * An Avatica server for arbitrary JDBC drivers.
 */
public class StandaloneServer {
	private static final Logger LOG = LoggerFactory.getLogger(StandaloneServer.class);

	private static final String AZURE_TENANT_ID = "";
	private static final String AZURE_CLIENT_ID = "";
	private static final String AZURE_CLIENT_SECRET = "";
	private static final String AWS_REGION = "eu-west-1";
	private static final String AWS_ACCESS_KEY_ID = "";
	private static final String AWS_SECRET_ACCESS_KEY = "";
	private static final String GITLAB_HOST = "";
	private static final String GITLAB_KEY = "";

	private String url = "jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE";
	private int port = 8765;
	private Serialization serialization = Serialization.PROTOBUF;

	private QueryContextImpl queryContext;
	private HttpServer server;

	public void start() {
		if (null != server) {
			LOG.error("The server was already started");
			System.exit( 1 );
			return;
		}

		try {
			BlazeJdbcMeta meta = new BlazeJdbcMeta(url);
			BlazeQueryService service = new BlazeQueryService(meta);

			QueryContextBuilder queryContextBuilder = Queries.createQueryContextBuilder();
			queryContextBuilder.setPropertyProvider( "dataFetchContextSupplier", service );

//            queryContextBuilder.setProperty(AzureConnectorConfig.API_CLIENT.getPropertyName(), createApiClient());
			queryContextBuilder.setProperty( AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getPropertyName(), createResourceManager());
			queryContextBuilder.setProperty( AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getPropertyName(), createGraphServiceClient());
//            queryContextBuilder.setProperty(AwsConnectorConfig.ACCOUNT.getPropertyName(), createAwsAccount());
			queryContextBuilder.setProperty( GitlabConnectorConfig.GITLAB_API.getPropertyName(), createGitlabApi());
//            queryContextBuilder.registerSchemaObjectAlias(VirtualMachine.class, "OpenAPIVirtualMachine");
//            queryContextBuilder.registerSchemaObjectAlias(StorageAccount.class, "OpenAPIStorageAccount");
//            queryContextBuilder.registerSchemaObjectAlias(BlobServiceProperties.class, "OpenAPIBlobServiceProperties");

			// Azure Resource manager
			queryContextBuilder.registerSchemaObjectAlias( VirtualMachineInner.class, "AZURE.VIRTUAL_MACHINE");
			queryContextBuilder.registerSchemaObjectAlias( StorageAccountInner.class, "AZURE.STORAGE_ACCOUNT");
			queryContextBuilder.registerSchemaObjectAlias( BlobServicePropertiesInner.class, "AZURE.BLOB_SERVICE_PROPERTIES");

			// Azure Graph
			queryContextBuilder.registerSchemaObjectAlias( User.class, "AZURE.USER");
			queryContextBuilder.registerSchemaObjectAlias( ConditionalAccessPolicy.class, "AZURE.CONDITIONAL_ACCESS_POLICY");
			queryContextBuilder.registerSchemaObjectAlias( Application.class, "AZURE.APPLICATION");

			// IAM
			queryContextBuilder.registerSchemaObjectAlias(software.amazon.awssdk.services.iam.model.User.class, "AWS.USER");
			// EC2
			queryContextBuilder.registerSchemaObjectAlias( Instance.class, "AWS.INSTANCE");
			queryContextBuilder.registerSchemaObjectAlias( Volume.class, "AWS.VOLUME");
			queryContextBuilder.registerSchemaObjectAlias( Vpc.class, "AWS.VPC");
			queryContextBuilder.registerSchemaObjectAlias( SecurityGroup.class, "AWS.SECURITY_GROUP");
			queryContextBuilder.registerSchemaObjectAlias( NetworkAcl.class, "AWS.NETWORK_ACL");
			// RDS
			queryContextBuilder.registerSchemaObjectAlias( DBInstance.class, "AWS.DB_INSTANCE");
			// EFS
			queryContextBuilder.registerSchemaObjectAlias( FileSystemDescription.class, "AWS.FILE_SYSTEM");
			// ECR
			queryContextBuilder.registerSchemaObjectAlias( Repository.class, "AWS.REPOSITORY");
			// ECS
			queryContextBuilder.registerSchemaObjectAlias( Cluster.class, "AWS.CLUSTER");
			// ELB
			queryContextBuilder.registerSchemaObjectAlias( LoadBalancer.class, "AWS.LOAD_BALANCER");
			// Lambda
			queryContextBuilder.registerSchemaObjectAlias( FunctionConfiguration.class, "AWS.FUNCTION");
			// Route53
			queryContextBuilder.registerSchemaObjectAlias( HostedZone.class, "AWS.HOSTED_ZONE");
			queryContextBuilder.registerSchemaObjectAlias( HealthCheck.class, "AWS.HEALTH_CHECK");
			// S3
			queryContextBuilder.registerSchemaObjectAlias( Bucket.class, "AWS.BUCKET");

			// Gitlab
			queryContextBuilder.registerSchemaObjectAlias( Project.class, "GITLAB.PROJECT");
			queryContextBuilder.registerSchemaObjectAlias( Group.class, "GITLAB.GROUP");
			queryContextBuilder.registerSchemaObjectAlias( ProjectMember.class, "GITLAB.PROJECT_MEMBER");
			queryContextBuilder.registerSchemaObjectAlias( GroupMember.class, "GITLAB.GROUP_MEMBER");
			queryContextBuilder.registerSchemaObjectAlias(org.gitlab4j.api.models.User.class, "GITLAB.USER");
			queryContextBuilder.registerSchemaObjectAlias( ProjectProtectedBranch.class, "GITLAB.PROJECT_PROTECTED_BRANCH");

			queryContext = (QueryContextImpl) queryContextBuilder.build();
			meta.dataSource = queryContext.unwrap( CalciteDataSource.class );
			service.queryContext = queryContext;

			// Construct the server
			HttpServer.Builder builder = new HttpServer.Builder()
					.withHandler(service, serialization)
					.withPort(port);

			server = builder.build();

			// Then start it
			server.start();

			LOG.info("Started Avatica server on port {} with serialization {}", server.getPort(),
					 serialization);
		} catch (Exception e) {
			LOG.error("Failed to start Avatica server", e);
			if ( queryContext != null ) {
				queryContext.close();
			}
			System.exit( 1 );
		}
	}

	private static AwsConnectorConfig.Account createAwsAccount() {
		return new AwsConnectorConfig.Account(
				Region.of( AWS_REGION),
				StaticCredentialsProvider.create( AwsBasicCredentials.create( AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY))
		);
	}

	private static GitLabApi createGitlabApi() {
		final GitLabApi gitLabApi = new GitLabApi( GITLAB_HOST, GITLAB_KEY );
		if ( !GITLAB_HOST.startsWith( "https://gitlab.com" )) {
			gitLabApi.setIgnoreCertificateErrors( true );
		}
		return gitLabApi;
	}

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

	public void stop() {
		if (null != server) {
			server.stop();
			server = null;
		}
	}

	public void join() throws InterruptedException {
		server.join();
	}

	public static void main(String[] args) {
		final StandaloneServer server = new StandaloneServer();

		server.start();

		// Try to clean up when the server is stopped.
		Runtime.getRuntime().addShutdownHook(
				new Thread(new Runnable() {
					@Override public void run() {
						LOG.info("Stopping server");
						server.stop();
						LOG.info("Server stopped");
					}
				}));


		try {
			server.join();
		} catch (InterruptedException e) {
			// Reset interruption
			Thread.currentThread().interrupt();
			// And exit now.
			return;
		}
	}

	static class BlazeJdbcMeta extends JdbcMeta {
		CalciteDataSource dataSource;

		public BlazeJdbcMeta(String url) throws SQLException {
			super( url );
		}

		@Override
		protected Connection createConnection(String url, Properties info) throws SQLException {
			return dataSource.getConnection();
		}
	}
}

