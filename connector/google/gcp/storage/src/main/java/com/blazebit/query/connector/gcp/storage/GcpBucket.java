/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.storage;

import com.blazebit.query.connector.gcp.base.GcpWrapper;
import com.google.storage.v2.Bucket;

public class GcpBucket extends GcpWrapper<Bucket> {
	public GcpBucket(String resourceId, Bucket bucket) {
		super(resourceId, bucket);
	}

	@Override
	public Bucket getPayload() {
		return super.getPayload();
	}
}
