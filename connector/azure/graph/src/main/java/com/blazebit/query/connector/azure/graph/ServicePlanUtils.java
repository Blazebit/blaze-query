/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.microsoft.graph.beta.models.ServicePlanInfo;
import com.microsoft.graph.beta.models.SubscribedSku;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServicePlanUtils {
	/**
	 * Extracts all service plan names from a list of SubscribedSku objects.
	 *
	 * @param subscribedSkus the list of SubscribedSku
	 * @return a list of all service plan names across all SubscribedSku
	 */
	public static List<ServicePlanName> getAllServicePlanNames(List<SubscribedSku> subscribedSkus) {
		List<ServicePlanName> servicePlanNames = new ArrayList<>();

		for (SubscribedSku sku : subscribedSkus) {
			for (ServicePlanInfo servicePlan : Objects.requireNonNull(sku.getServicePlans())) {
				ServicePlanName.fromName(servicePlan.getServicePlanName()).ifPresent(servicePlanNames::add);
			}
		}

		return servicePlanNames;
	}
}
