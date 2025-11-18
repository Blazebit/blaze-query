/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
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
public class AwsIamAccessKeyMetaDataLastUsed implements Serializable {

	private final String accountId;
	private final String ownerUserName;
	private final AccessKeyMetadata accessKeyMetadata;
	private final AccessKeyLastUsed accessKeyLastUsed;

	/**
	 * Constructs an AccessKeyMetaDataLastUsed object with the specified metadata and last used information.
	 *
	 * @param accountId The account id
	 * @param ownerUserName The username of the owner of the access key
	 * @param accessKeyMetadata the metadata of the access key
	 * @param accessKeyLastUsed the last used information of the access key
	 */
	public AwsIamAccessKeyMetaDataLastUsed(String accountId, String ownerUserName, AccessKeyMetadata accessKeyMetadata, AccessKeyLastUsed accessKeyLastUsed) {
		this.accountId = accountId;
		this.ownerUserName = ownerUserName;
		this.accessKeyMetadata = accessKeyMetadata;
		this.accessKeyLastUsed = accessKeyLastUsed;
	}

	public String accountId() {
		return accountId;
	}

	public String ownerUserName() {
		return ownerUserName;
	}

	public String userName() {
		return accessKeyMetadata.userName();
	}

	public String accessKeyId() {
		return accessKeyMetadata.accessKeyId();
	}

	public String status() {
		return accessKeyMetadata.statusAsString();
	}

	public Instant createDate() {
		return accessKeyMetadata.createDate();
	}

	public Instant lastUsedDate() {
		return accessKeyLastUsed.lastUsedDate();
	}

	public String serviceName() {
		return accessKeyLastUsed.serviceName();
	}

	public String region() {
		return accessKeyLastUsed.region();
	}
}
