/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.kms;

import com.blazebit.query.connector.gcp.base.GcpWrapper;
import com.google.cloud.kms.v1.CryptoKey;

/**
 * Wrapper for a GCP Cloud KMS CryptoKey.
 *
 * @author Martijn Sprengers
 * @since 2.3.0
 */
public class GcpKmsCryptoKey extends GcpWrapper<CryptoKey> {
	public GcpKmsCryptoKey(String resourceId, CryptoKey cryptoKey) {
		super( resourceId, cryptoKey );
	}

	@Override
	public CryptoKey getPayload() {
		return super.getPayload();
	}
}
