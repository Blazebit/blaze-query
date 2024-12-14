/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.microsoft.graph.beta.serviceclient.GraphServiceClient;

/**
 * Accessor for Azure {@link GraphServiceClient} and tenant information.
 *
 * @author Christian Beikov
 * @since 1.0.2
 */
public interface AzureGraphClientAccessor {

	String getTenantId();

	GraphServiceClient getGraphServiceClient();

	static AzureGraphClientAccessor create(String tenantId, GraphServiceClient graphServiceClient) {
		return new AzureGraphClientAccessor() {
			@Override
			public String getTenantId() {
				return tenantId;
			}

			@Override
			public GraphServiceClient getGraphServiceClient() {
				return graphServiceClient;
			}
		};
	}
}
