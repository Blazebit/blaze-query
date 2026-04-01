/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.sentinel;

import com.azure.resourcemanager.securityinsights.SecurityInsightsManager;

/**
 * Accessor for a Microsoft Sentinel workspace and its {@link SecurityInsightsManager}.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public interface SentinelClientAccessor {

	/**
	 * Returns the Azure tenant ID.
	 */
	String getTenantId();

	/**
	 * Returns the Azure subscription ID.
	 */
	String getSubscriptionId();

	/**
	 * Returns the resource group that contains the Sentinel workspace.
	 */
	String getResourceGroupName();

	/**
	 * Returns the Log Analytics workspace name used by Sentinel.
	 */
	String getWorkspaceName();

	/**
	 * Returns the {@link SecurityInsightsManager} for querying Sentinel resources.
	 */
	SecurityInsightsManager getManager();

	/**
	 * Creates a simple {@link SentinelClientAccessor} from the given components.
	 */
	static SentinelClientAccessor create(
			String tenantId,
			String subscriptionId,
			String resourceGroupName,
			String workspaceName,
			SecurityInsightsManager manager) {
		return new SentinelClientAccessor() {
			@Override
			public String getTenantId() {
				return tenantId;
			}

			@Override
			public String getSubscriptionId() {
				return subscriptionId;
			}

			@Override
			public String getResourceGroupName() {
				return resourceGroupName;
			}

			@Override
			public String getWorkspaceName() {
				return workspaceName;
			}

			@Override
			public SecurityInsightsManager getManager() {
				return manager;
			}
		};
	}
}
