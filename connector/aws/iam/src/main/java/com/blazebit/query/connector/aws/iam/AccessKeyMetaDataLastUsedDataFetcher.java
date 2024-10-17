package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.IamClientBuilder;
import software.amazon.awssdk.services.iam.model.AccessKeyMetadata;
import software.amazon.awssdk.services.iam.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AccessKeyMetaDataLastUsedDataFetcher implements DataFetcher<AccessKeyMetaDataLastUsed>, Serializable {

    public static final AccessKeyMetaDataLastUsedDataFetcher INSTANCE = new AccessKeyMetaDataLastUsedDataFetcher();

    private AccessKeyMetaDataLastUsedDataFetcher(){
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.componentMethodConvention(AccessKeyMetaDataLastUsed.class, AwsConventionContext.INSTANCE);
    }

    @Override
    public List<AccessKeyMetaDataLastUsed> fetch(DataFetchContext context) {
        try {
            List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
            SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
            List<AccessKeyMetaDataLastUsed> list = new ArrayList<>();
            for ( AwsConnectorConfig.Account account : accounts) {
                IamClientBuilder ec2ClientBuilder = IamClient.builder()
                        .region( account.getRegion() )
                        .credentialsProvider( account.getCredentialsProvider() );
                if ( sdkHttpClient != null ) {
                    ec2ClientBuilder.httpClient( sdkHttpClient );
                }
                try (IamClient client = ec2ClientBuilder.build()) {
                    for(User user : context.getSession().get(User.class)){
                        for(AccessKeyMetadata accessKeyMetadata : client.listAccessKeys(builder -> builder.userName(user.userName())).accessKeyMetadata()){
                            list.add( new AccessKeyMetaDataLastUsed(accessKeyMetadata, client.getAccessKeyLastUsed(builder -> builder.accessKeyId(accessKeyMetadata.accessKeyId())).accessKeyLastUsed()) );
                        }
                    }
                    return list;
                }
            }
            return list;
        } catch (RuntimeException e) {
            throw new DataFetcherException("Could not fetch access key list", e);
        }
    }
}
