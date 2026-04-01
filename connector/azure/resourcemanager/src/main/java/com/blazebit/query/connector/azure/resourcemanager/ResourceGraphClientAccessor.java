/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.resourcegraph.ResourceGraphManager;

import java.util.List;

/**
 * Accessor providing an authenticated {@link ResourceGraphManager} and the subscription IDs
 * to include in Azure Resource Graph queries.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public interface ResourceGraphClientAccessor {

	/**
	 * Returns the Azure tenant ID.
	 */
	String getTenantId();

	/**
	 * Returns the list of Azure subscription IDs to query.
	 */
	List<String> getSubscriptionIds();

	/**
	 * Returns the {@link ResourceGraphManager} used to execute KQL queries.
	 */
	ResourceGraphManager getManager();

	/**
	 * Creates a simple {@link ResourceGraphClientAccessor} from the given components.
	 */
	static ResourceGraphClientAccessor create(
			String tenantId,
			List<String> subscriptionIds,
			ResourceGraphManager manager) {
		return new ResourceGraphClientAccessor() {
			@Override
			public String getTenantId() {
				return tenantId;
			}

			@Override
			public List<String> getSubscriptionIds() {
				return subscriptionIds;
			}

			@Override
			public ResourceGraphManager getManager() {
				return manager;
			}
		};
	}
}
