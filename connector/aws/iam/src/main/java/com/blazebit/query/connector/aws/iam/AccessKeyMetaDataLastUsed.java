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

package com.blazebit.query.connector.aws.iam;

import software.amazon.awssdk.services.iam.model.AccessKeyLastUsed;
import software.amazon.awssdk.services.iam.model.AccessKeyMetadata;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author Martijn Sprengers
 * @since 1.0.0
 */
public class AccessKeyMetaDataLastUsed implements Serializable {

    private final AccessKeyMetadata accessKeyMetadata;
    private final AccessKeyLastUsed accessKeyLastUsed;

    /**
     * Constructs an AccessKeyMetaDataLastUsed object with the specified metadata and last used information.
     *
     * @param accessKeyMetadata the metadata of the access key
     * @param accessKeyLastUsed the last used information of the access key
     */
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
