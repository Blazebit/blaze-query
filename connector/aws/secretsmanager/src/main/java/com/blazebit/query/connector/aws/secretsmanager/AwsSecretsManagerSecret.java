/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.secretsmanager;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.secretsmanager.model.SecretListEntry;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsSecretsManagerSecret extends AwsWrapper<SecretListEntry> {
	public AwsSecretsManagerSecret(String arn, SecretListEntry payload) {
		super( arn, payload );
	}

	@Override
	public SecretListEntry getPayload() {
		return super.getPayload();
	}
}
