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

public class AlertDataFetcherTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider(new AzureGraphSchemaProvider());
		builder.registerSchemaObjectAlias(AzureGraphAlert.class, "AzureAlert");
		CONTEXT = builder.build();
	}

	@Test
	void should_only_return_medium_severity_alert(){
		try (var session = CONTEXT.createSession()) {
			session.put(AzureGraphAlert.class, List.of(AzureTestObjects.alertLow(),
					AzureTestObjects.alertMedium()));

			var typedQuery = session.createQuery("select a.payload.* from AzureAlert a where a.payload.severity = 'Medium'", AzureGraphAlert.class);

			assertThat(typedQuery.getResultList()).isNotEmpty();
		}
	}
}
