/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.sql;

import com.blazebit.query.connector.gcp.base.GcpWrapper;
import com.google.api.services.sqladmin.model.DatabaseInstance;

/**
 * Wrapper for a GCP Cloud SQL database instance.
 *
 * @author Martijn Sprengers
 * @since 2.3.0
 */
public class GcpSqlInstance extends GcpWrapper<DatabaseInstance> {
	public GcpSqlInstance(String resourceId, DatabaseInstance instance) {
		super( resourceId, instance );
	}

	@Override
	public DatabaseInstance getPayload() {
		return super.getPayload();
	}
}
