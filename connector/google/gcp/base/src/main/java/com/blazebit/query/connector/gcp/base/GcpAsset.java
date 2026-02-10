/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.base;

import com.google.cloud.asset.v1.Asset;

public class GcpAsset extends GcpWrapper<Asset> {
	public GcpAsset(String resourceId, Asset asset) {
		super(resourceId, asset);
	}

	@Override
	public Asset getPayload() {
		return super.getPayload();
	}
}
