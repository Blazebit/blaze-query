/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.blazebit.query.QueryContext;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class IncidentDataFetcherTest {
	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider(new AzureGraphSchemaProvider());
		builder.registerSchemaObjectAlias(AzureGraphIncident.class, "AzureIncident");
		CONTEXT = builder.build();
	}

	@Test
	void should_only_return_medium_severity_incident(){
		try (var session = CONTEXT.createSession()) {
			session.put(AzureGraphIncident.class, List.of(AzureTestObjects.incidentInformational(),
					AzureTestObjects.incidentMedium()));

			var typedQuery = session.createQuery("select a.payload.* from AzureIncident a where a.payload.severity = 'Medium'", AzureGraphAlert.class);

			assertThat(typedQuery.getResultList()).isNotEmpty();
		}
	}
}
