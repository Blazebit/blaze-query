/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.sentinel;

import com.azure.resourcemanager.securityinsights.fluent.models.AlertRuleInner;

/**
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class SentinelAlertRule extends SentinelWrapper<AlertRuleInner> {

	public SentinelAlertRule(
			String tenantId,
			String subscriptionId,
			String resourceGroupName,
			String workspaceName,
			AlertRuleInner payload) {
		super( tenantId, subscriptionId, resourceGroupName, workspaceName, payload );
	}

	@Override
	public AlertRuleInner getPayload() {
		return super.getPayload();
	}
}
