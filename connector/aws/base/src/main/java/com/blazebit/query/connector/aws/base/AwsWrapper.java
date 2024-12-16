/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.base;

import java.util.StringTokenizer;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public abstract class AwsWrapper<T> {

	private final String accountId;
	private final String regionId;
	private final String resourceId;
	private final T payload;

	public AwsWrapper(String accountId, String regionId, String resourceId, T payload) {
		this.accountId = accountId;
		this.regionId = regionId;
		this.resourceId = resourceId;
		this.payload = payload;
	}

	public AwsWrapper(String arn, T payload) {
		// arn:partition:service:region:account-id:resource-type/resource-id
		// arn:partition:service:region:account-id:resource-type:resource-id
		StringTokenizer tokenizer = new StringTokenizer(arn, ":");
		// arn
		tokenizer.nextToken();
		// partition
		tokenizer.nextToken();
		// service
		tokenizer.nextToken();
		this.regionId = tokenizer.nextToken();
		this.accountId = tokenizer.nextToken();
		String resourcePart = tokenizer.nextToken();
		int slashIndex = resourcePart.indexOf('/');
		if ( slashIndex != -1 ) {
			this.resourceId = resourcePart.substring(slashIndex);
		}
		else {
			this.resourceId = tokenizer.nextToken();
		}
		this.payload = payload;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getRegionId() {
		return regionId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public T getPayload() {
		return payload;
	}
}
