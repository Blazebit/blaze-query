/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.microsoft.graph.beta.models.ServicePlanInfo;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureGraphServicePlanInfo extends AzureGraphWrapper<ServicePlanInfo> {
	public AzureGraphServicePlanInfo(String tenantId, ServicePlanInfo payload) {
		super( tenantId, payload );
	}

	@Override
	public ServicePlanInfo getPayload() {
		return super.getPayload();
	}
}
