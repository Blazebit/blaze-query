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
	public AzureResourcePostgreSqlFlexibleServerBackup(String tenantId, String resourceId, ServerBackupInner payload) {
		super( tenantId, resourceId, payload );
	}

	@Override
	public ServerBackupInner getPayload() {
		return super.getPayload();
	}
}
