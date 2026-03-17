/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.compute;

import com.blazebit.query.connector.gcp.base.GcpWrapper;
import com.google.cloud.compute.v1.Firewall;

/**
 * Wrapper for a GCP VPC Firewall rule.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class GcpFirewallRule extends GcpWrapper<Firewall> {
	public GcpFirewallRule(String resourceId, Firewall firewall) {
		super( resourceId, firewall );
	}

	@Override
	public Firewall getPayload() {
		return super.getPayload();
	}
}
