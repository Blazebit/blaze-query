/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import com.blazebit.query.QueryContext;
import com.blazebit.query.QuerySession;
import com.blazebit.query.TypedQuery;
import com.blazebit.query.connector.base.ConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFormat;
import com.blazebit.query.spi.Queries;
import com.blazebit.query.spi.QueryContextBuilder;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Christian Beikov
 * @since 1.0.16
 */
public class ArrayInSelectTest {

	// Test for #69
	@Test
	public void test() {
		Control model = new Control(
				UUID.randomUUID(),
				"c1",
				false,
				Effectiveness.EFFECTIVE,
				List.of(
						new Risk(
								UUID.randomUUID(),
								RiskTreatment.REDUCE
						)
				)
		);
		QueryContextBuilder queryContextBuilder = Queries.createQueryContextBuilder();
		queryContextBuilder.registerSchemaObject( Control.class, new DataFetcher<>() {
			@Override
			public DataFormat getDataFormat() {
				return DataFormats.componentMethodConvention( Control.class, ConventionContext.NO_FILTER );
			}

			@Override
			public List<Control> fetch(DataFetchContext context) {
				return List.of( model );
			}
		} );
		queryContextBuilder.registerSchemaObjectAlias( Control.class, "Control" );
		try (QueryContext queryContext = queryContextBuilder.build()) {
			try (QuerySession session = queryContext.createSession()) {
				TypedQuery<Object[]> query = session.createQuery(
						"select c.id, c.name, (exists (select 1 from unnest(c.risks) as r where r.treatment = 'REDUCE')) as passed from Control c where c.archived = false"
				);
				List<Object[]> result = query.getResultList();
				assertEquals( 1, result.size() );
			}
		}
	}

	public record Control(
			UUID id,
			String name,
			boolean archived,
			Effectiveness effectiveness,
			Collection<Risk> risks) {
	}

	public record Risk(
			UUID id,
			RiskTreatment treatment) {
	}

	public enum RiskTreatment {
		ACCEPT,
		AVOID,
		TRANSFER,
		REDUCE
	}
	public enum Effectiveness {
		UNDETERMINED,
		INEFFECTIVE,
		EFFECTIVE
	}
}
