/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.resources.models.Subscription;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class SubscriptionDataFetcher implements DataFetcher<AzureResourceManagerSubscription>, Serializable {

	public static final SubscriptionDataFetcher INSTANCE = new SubscriptionDataFetcher();

	private SubscriptionDataFetcher() {
	}

	@Override
	public List<AzureResourceManagerSubscription> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll(
					context );
			List<AzureResourceManagerSubscription> list = new ArrayList<>();
			for ( AzureResourceManager resourceManager : resourceManagers ) {
				for ( Subscription subscription : resourceManager.subscriptions().list() ) {
					list.add( new AzureResourceManagerSubscription(
							resourceManager.tenantId(),
							subscription.subscriptionId(),
							subscription.innerModel()
					) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch subscription list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AzureResourceManagerSubscription.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}
}
