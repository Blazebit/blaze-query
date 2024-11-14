/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import java.util.Map;

import com.azure.resourcemanager.compute.fluent.models.VirtualMachineInner;
import com.azure.resourcemanager.containerservice.fluent.models.ManagedClusterInner;
import com.azure.resourcemanager.keyvault.fluent.models.VaultInner;
import com.azure.resourcemanager.resources.fluent.models.ResourceGroupInner;
import com.azure.resourcemanager.resources.fluent.models.SubscriptionInner;
import com.azure.resourcemanager.resources.fluent.models.TenantIdDescriptionInner;
import com.azure.resourcemanager.storage.fluent.models.BlobServicePropertiesInner;
import com.azure.resourcemanager.storage.fluent.models.StorageAccountInner;
import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

/**
 * The schema provider for the Azure Subscription connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AzureResourceManagerSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public AzureResourceManagerSchemaProvider() {
	}

	@Override
	public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Map.<Class<?>, DataFetcher<?>>of(
				SubscriptionInner.class, SubscriptionDataFetcher.INSTANCE,
				TenantIdDescriptionInner.class, TenantDataFetcher.INSTANCE,
				VirtualMachineInner.class, VirtualMachineDataFetcher.INSTANCE,
				StorageAccountInner.class, StorageAccountDataFetcher.INSTANCE,
				ResourceGroupInner.class, ResourceGroupDataFetcher.INSTANCE,
				VaultInner.class, VaultDataFetcher.INSTANCE,
				BlobServicePropertiesInner.class, BlobServicePropertiesDataFetcher.INSTANCE,
				ManagedClusterInner.class, ManagedClusterDataFetcher.INSTANCE
		);
	}
}
