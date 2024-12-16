/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ecs;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ecs.model.Cluster;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsCluster extends AwsWrapper<Cluster> {
	public AwsCluster(String arn, Cluster payload) {
		super( arn, payload );
	}

	@Override
	public Cluster getPayload() {
		return super.getPayload();
	}
}
