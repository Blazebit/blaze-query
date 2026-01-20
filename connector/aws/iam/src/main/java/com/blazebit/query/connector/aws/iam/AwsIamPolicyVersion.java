/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsPolicyWrapper;

import java.time.Instant;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamPolicyVersion extends AwsPolicyWrapper {

	private final String versionId;
	private final Boolean isDefaultVersion;
	private final Instant createDate;

	public AwsIamPolicyVersion(
			String accountId,
			String policyArn,
			String versionId,
			Boolean isDefaultVersion,
			Instant createDate,
			String policyDocument) {
		super( accountId, null, policyArn, policyDocument, true );
		this.versionId = versionId;
		this.isDefaultVersion = isDefaultVersion;
		this.createDate = createDate;
	}

	public String getPolicyArn() {
		return getResourceId();
	}

	public String getVersionId() {
		return versionId;
	}

	public Boolean getIsDefaultVersion() {
		return isDefaultVersion;
	}

	public Instant getCreateDate() {
		return createDate;
	}
}
