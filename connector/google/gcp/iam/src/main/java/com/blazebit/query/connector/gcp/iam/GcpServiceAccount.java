/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.iam;

import com.blazebit.query.connector.gcp.base.GcpWrapper;
import com.google.iam.admin.v1.ServiceAccount;

public class GcpServiceAccount extends GcpWrapper<ServiceAccount> {
	public GcpServiceAccount(String resourceId, ServiceAccount serviceAccount) {
		super(resourceId, serviceAccount);
	}

	@Override
	public ServiceAccount getPayload() {
		return super.getPayload();
	}
}
