/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.observatory;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Martijn Sprengers
 * @since 1.0.25
 */
public class ObservatoryQueryTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider(new ObservatorySchemaProvider());
		builder.registerSchemaObjectAlias(ObservatoryScan.class, "ObservatoryScan");
		CONTEXT = builder.build();
	}

	@Test
	void should_return_observatory_scan() {
		try (var session = CONTEXT.createSession()) {
			ObservatoryScan scan = ObservatoryTestObjects.tidalcontrolScan();

			// Provide the test data to the session
			session.put(ObservatoryScan.class, List.of(scan));

			var typedQuery =
					session.createQuery("select s.* from ObservatoryScan s",
							new TypeReference<Map<String, Object>>() {});

			assertThat(typedQuery.getResultList())
					.extracting(result -> result.get("id"))
					.containsExactly(scan.id());
		}
	}
}
