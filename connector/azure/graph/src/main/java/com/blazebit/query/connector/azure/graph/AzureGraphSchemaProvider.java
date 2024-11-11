/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import java.util.Map;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;
import com.microsoft.graph.beta.models.Application;
import com.microsoft.graph.beta.models.ConditionalAccessPolicy;
import com.microsoft.graph.beta.models.ManagedDevice;
import com.microsoft.graph.beta.models.Organization;
import com.microsoft.graph.beta.models.SubscribedSku;
import com.microsoft.graph.beta.models.User;

/**
 * The schema provider for the Azure Subscription connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AzureGraphSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public AzureGraphSchemaProvider() {
	}

    @Override
    public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
        return Map.<Class<?>, DataFetcher<?>>of(
                Application.class, ApplicationDataFetcher.INSTANCE,
                ConditionalAccessPolicy.class, ConditionalAccessPolicyDataFetcher.INSTANCE,
                ManagedDevice.class, ManagedDeviceDataFetcher.INSTANCE,
                User.class, UserDataFetcher.INSTANCE,
                Organization.class, OrganizationDataFetcher.INSTANCE,
                SubscribedSku.class, SubscribedSkuDataFetcher.INSTANCE
        );
    }
}
