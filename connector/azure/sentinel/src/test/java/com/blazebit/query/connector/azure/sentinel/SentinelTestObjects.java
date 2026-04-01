/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.sentinel;

import com.azure.json.JsonOptions;
import com.azure.json.JsonReader;
import com.azure.json.implementation.DefaultJsonReader;
import com.azure.resourcemanager.securityinsights.fluent.models.AlertRuleInner;
import com.azure.resourcemanager.securityinsights.fluent.models.DataConnectorInner;
import com.azure.resourcemanager.securityinsights.fluent.models.IncidentInner;

import java.io.FileInputStream;
import java.io.IOException;

public final class SentinelTestObjects {

	private SentinelTestObjects() {
	}

	private static JsonReader jsonReader(String file) throws IOException {
		return DefaultJsonReader.fromStream( new FileInputStream( file ), new JsonOptions() );
	}

	public static SentinelIncident incidentHigh() {
		try {
			IncidentInner inner = IncidentInner.fromJson(
					jsonReader( "src/test/resources/resource-definitions/azure/sentinel/incident-high.json" ) );
			return new SentinelIncident( "tenant1", "sub1", "rg1", "ws1", inner );
		}
		catch (IOException e) {
			throw new RuntimeException( e );
		}
	}

	public static SentinelIncident incidentLow() {
		try {
			IncidentInner inner = IncidentInner.fromJson(
					jsonReader( "src/test/resources/resource-definitions/azure/sentinel/incident-low.json" ) );
			return new SentinelIncident( "tenant1", "sub1", "rg1", "ws1", inner );
		}
		catch (IOException e) {
			throw new RuntimeException( e );
		}
	}

	public static SentinelAlertRule alertRule() {
		try {
			AlertRuleInner inner = AlertRuleInner.fromJson(
					jsonReader( "src/test/resources/resource-definitions/azure/sentinel/alert-rule1.json" ) );
			return new SentinelAlertRule( "tenant1", "sub1", "rg1", "ws1", inner );
		}
		catch (IOException e) {
			throw new RuntimeException( e );
		}
	}

	public static SentinelDataConnector dataConnector() {
		try {
			DataConnectorInner inner = DataConnectorInner.fromJson(
					jsonReader( "src/test/resources/resource-definitions/azure/sentinel/data-connector1.json" ) );
			return new SentinelDataConnector( "tenant1", "sub1", "rg1", "ws1", inner );
		}
		catch (IOException e) {
			throw new RuntimeException( e );
		}
	}
}
