/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.elb;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsLoadBalancer extends AwsWrapper<LoadBalancer> {
	public AwsLoadBalancer(String arn, LoadBalancer payload) {
		super( arn, payload );
	}

	@Override
	public LoadBalancer getPayload() {
		return super.getPayload();
	}
}
