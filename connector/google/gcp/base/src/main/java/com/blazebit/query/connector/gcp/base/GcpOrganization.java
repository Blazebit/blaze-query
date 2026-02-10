/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.base;

import com.google.cloud.resourcemanager.v3.Organization;

public class GcpOrganization extends GcpWrapper<Organization> {
	public GcpOrganization(String resourceId, Organization organization) {
		super(resourceId, organization);
	}

	@Override
	public Organization getPayload() {
		return super.getPayload();
	}
}
