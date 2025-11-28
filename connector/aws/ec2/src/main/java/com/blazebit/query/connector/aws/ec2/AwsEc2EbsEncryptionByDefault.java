/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.GetEbsEncryptionByDefaultResponse;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2EbsEncryptionByDefault extends AwsWrapper<GetEbsEncryptionByDefaultResponse> {
	public AwsEc2EbsEncryptionByDefault(String accountId, String regionId, String resourceId,
									GetEbsEncryptionByDefaultResponse payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public GetEbsEncryptionByDefaultResponse getPayload() {
		return super.getPayload();
	}
}
