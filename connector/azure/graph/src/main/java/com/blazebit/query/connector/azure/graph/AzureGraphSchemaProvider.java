/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

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
public final class AzureGraphSchemaProvider implements QuerySchemaProvider {
	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				ApplicationDataFetcher.INSTANCE,
				ConditionalAccessPolicyDataFetcher.INSTANCE,
				ManagedDeviceDataFetcher.INSTANCE,
				UserDataFetcher.INSTANCE,
				UserLastSignInActivityDataFetcher.INSTANCE,
				OrganizationDataFetcher.INSTANCE,
				ServicePlanDataFetcher.INSTANCE,
				ServicePlanInfoDataFetcher.INSTANCE
		);
	}
}
