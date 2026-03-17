/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.container;

import com.blazebit.query.connector.gcp.base.GcpWrapper;
import com.google.container.v1.Cluster;

/**
 * Wrapper for a GCP GKE (Google Kubernetes Engine) cluster.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class GcpGkeCluster extends GcpWrapper<Cluster> {
	public GcpGkeCluster(String resourceId, Cluster cluster) {
		super( resourceId, cluster );
	}

	@Override
	public Cluster getPayload() {
		return super.getPayload();
	}
}
