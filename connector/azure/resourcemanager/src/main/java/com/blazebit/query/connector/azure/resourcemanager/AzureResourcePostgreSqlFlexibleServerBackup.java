/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.postgresqlflexibleserver.fluent.models.ServerBackupInner;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.4
 */
public class AzureResourcePostgreSqlFlexibleServerBackup extends AzureResourceWrapper<ServerBackupInner> {

	private final String flexibleServerId;

	public AzureResourcePostgreSqlFlexibleServerBackup(String tenantId, String resourceId, ServerBackupInner payload, String flexibleServerId) {
		super( tenantId, resourceId, payload );
		this.flexibleServerId = flexibleServerId;
	}

	@Override
	public ServerBackupInner getPayload() {
		return super.getPayload();
	}

	public String getFlexibleServerId() {
		return flexibleServerId;
	}
}
