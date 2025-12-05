/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.ServiceDetail;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsVpcEndpointService extends AwsWrapper<ServiceDetail> {
	public AwsVpcEndpointService(String accountId, String regionId, String resourceId, ServiceDetail payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public ServiceDetail getPayload() {
		return super.getPayload();
	}
}
