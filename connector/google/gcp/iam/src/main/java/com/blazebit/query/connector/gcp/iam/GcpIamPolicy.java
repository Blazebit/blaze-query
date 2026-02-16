/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.iam;

import com.blazebit.query.connector.gcp.base.GcpWrapper;
import com.google.cloud.asset.v1.IamPolicySearchResult;

public class GcpIamPolicy extends GcpWrapper<IamPolicySearchResult> {
	public GcpIamPolicy(String resourceId, IamPolicySearchResult iamPolicySearchResult) {
		super(resourceId, iamPolicySearchResult);
	}

	@Override
	public IamPolicySearchResult getPayload() {
		return super.getPayload();
	}
}
