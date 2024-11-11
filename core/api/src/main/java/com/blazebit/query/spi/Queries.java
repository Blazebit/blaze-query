/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.blazebit.query.QueryContext;

/**
 * Bootstrap class to obtain a {@link QueryContext} or builder for such.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class Queries {

	private static volatile QueryContextBuilderFactory builderFactory;

	private Queries() {
	}

	/**
	 * Creates a new {@link QueryContext}, based on default configuration.
	 *
	 * @return a new {@link QueryContext}
	 */
	public static QueryContext createQueryContext() {
		return createQueryContextBuilder().build();
	}

	/**
	 * Creates a new {@link QueryContextBuilder} with all discoverable services loaded.
	 *
	 * @return a new {@link QueryContextBuilder}
	 */
	public static QueryContextBuilder createQueryContextBuilder() {
		return createEmptyQueryContextBuilder().loadServices();
	}

	/**
	 * Creates a new empty {@link QueryContextBuilder}. Services have not been loaded yet for this builder
	 * and have to be loaded manually.
	 *
	 * @return a new empty {@link QueryContextBuilder}
	 */
	public static QueryContextBuilder createEmptyQueryContextBuilder() {
		QueryContextBuilderFactory factory = builderFactory;
		if ( factory == null ) {
			ServiceLoader<QueryContextBuilderFactory> contextBuilders = ServiceLoader.load(
					QueryContextBuilderFactory.class );
			Iterator<QueryContextBuilderFactory> iterator = contextBuilders.iterator();
			if ( iterator.hasNext() ) {
				builderFactory = factory = iterator.next();
			}
			else {
				throw new IllegalStateException(
						"No QueryContextBuilderFactory found on the class path. Please check if a valid implementation is on the class path." );
			}
		}
		return factory.creatBuilder();
	}
}
