package com.blazebit.query.connector.aws.iam;

import software.amazon.awssdk.services.iam.model.AccessKeyLastUsed;
import software.amazon.awssdk.services.iam.model.AccessKeyMetadata;

import java.io.Serializable;
import java.time.Instant;

public class AccessKeyMetaDataLastUsed implements Serializable {

    private final AccessKeyMetadata accessKeyMetadata;
    private final AccessKeyLastUsed accessKeyLastUsed;

    public AccessKeyMetaDataLastUsed(AccessKeyMetadata accessKeyMetadata, AccessKeyLastUsed accessKeyLastUsed) {
        this.accessKeyMetadata = accessKeyMetadata;
        this.accessKeyLastUsed = accessKeyLastUsed;
    }

    public String getUserName() {
        return accessKeyMetadata.userName();
    }

    public String getAccessKeyId() {
        return accessKeyMetadata.accessKeyId();
    }

    public String getStatus() {
        return accessKeyMetadata.statusAsString();
    }

    public Instant getCreateDate() {
        return accessKeyMetadata.createDate();
    }

    public Instant getLastUsedDate() {
        return accessKeyLastUsed.lastUsedDate();
    }

    public String getServiceName() {
        return accessKeyLastUsed.serviceName();
    }

    public String getRegion() {
        return accessKeyLastUsed.region();
    }
}
