/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.compute;

import com.google.cloud.compute.v1.Instance;

public class GcpInstance extends GcpWrapper<Instance> {
	public  GcpInstance(String resourceId, Instance instance) {
		super(resourceId, instance);
	}

	@Override
	public Instance getPayload() {
		return super.getPayload();
	}

}
