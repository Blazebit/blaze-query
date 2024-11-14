/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.subscription.v20221201;

import java.util.Map;

import com.blazebit.query.connector.azure.subscription.v20221201.model.Subscription;
import com.blazebit.query.connector.azure.subscription.v20221201.model.TenantIdDescription;
import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

/**
 * The schema provider for the Azure Subscription connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AzureSubscriptionSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public AzureSubscriptionSchemaProvider() {
	}

	@Override
	public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Map.<Class<?>, DataFetcher<?>>of(
				Subscription.class, SubscriptionDataFetcher.INSTANCE,
				TenantIdDescription.class, TenantIdDescriptionDataFetcher.INSTANCE
		);
	}
}
