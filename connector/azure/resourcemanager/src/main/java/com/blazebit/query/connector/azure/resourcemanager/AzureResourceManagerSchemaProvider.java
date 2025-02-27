/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the Azure Subscription connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AzureResourceManagerSchemaProvider implements QuerySchemaProvider {
	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				VirtualNetworkDataFetcher.INSTANCE,
				SubscriptionDataFetcher.INSTANCE,
				TenantDataFetcher.INSTANCE,
				VirtualMachineDataFetcher.INSTANCE,
				StorageAccountDataFetcher.INSTANCE,
				ResourceGroupDataFetcher.INSTANCE,
				ManagedClusterDataFetcher.INSTANCE,
				VaultDataFetcher.INSTANCE,
				BlobServicePropertiesDataFetcher.INSTANCE,
				DiskDataFetcher.INSTANCE,
				PostgreSqlFlexibleServerDataFetcher.INSTANCE
		);
	}
}
