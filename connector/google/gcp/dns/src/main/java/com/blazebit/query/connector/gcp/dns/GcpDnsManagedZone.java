/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.dns;

import com.blazebit.query.connector.gcp.base.GcpWrapper;
import com.google.cloud.dns.ZoneInfo;

/**
 * Wrapper for a GCP Cloud DNS managed zone.
 *
 * @author Martijn Sprengers
 * @since 2.3.0
 */
public class GcpDnsManagedZone extends GcpWrapper<ZoneInfo> {
	public GcpDnsManagedZone(String resourceId, ZoneInfo zoneInfo) {
		super( resourceId, zoneInfo );
	}

	@Override
	public ZoneInfo getPayload() {
		return super.getPayload();
	}
}
