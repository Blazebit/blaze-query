/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.metamodel;

import com.blazebit.query.spi.DataFetcher;

/**
 * A schema object type.
 *
 * @param <T> The schema object type
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface SchemaObjectType<T> {

	/**
	 * Returns the type of the schema object.
	 *
	 * @return The type of the schema object
	 */
	Class<T> getType();

	/**
	 * Returns the data fetcher for the schema object.
	 *
	 * @return The data fetcher for the schema object
	 */
	DataFetcher<T> getDataFetcher();
}
