/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.iam.model.ServerCertificateMetadata;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamServerCertificate extends AwsWrapper<ServerCertificateMetadata> {
	public AwsIamServerCertificate(String accountId, String resourceId, ServerCertificateMetadata payload) {
		super( accountId, null, resourceId, payload );
	}

	@Override
	public ServerCertificateMetadata getPayload() {
		return super.getPayload();
	}
}
