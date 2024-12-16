/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.route53;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.route53.model.HostedZone;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsHostedZone extends AwsWrapper<HostedZone> {
	public AwsHostedZone(String accountId, String resourceId, HostedZone payload) {
		super( accountId, null, resourceId, payload );
	}

	@Override
	public HostedZone getPayload() {
		return super.getPayload();
	}
}
