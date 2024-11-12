/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.spi;

import com.blazebit.query.QuerySession;

/**
 * The PropertyProvider interface is a functional interface that provides an object
 * based on the given DataFetchContext. It is used to lazily fetch property values
 * or property providers for a given context.
 *
 * @param <X> The type of object to be provided
 * @author Max Hovens
 * @since 1.0.0
 */
@FunctionalInterface
public interface PropertyProvider<X> {

	/**
	 * Provides the Object. An {@link DataFetchContext} is supplied for the provider to use properties
	 * from the context.
	 *
	 * @param context the {@link DataFetchContext} from the current {@link QuerySession}
	 * @return the object to provide
	 */
	X provide(DataFetchContext context);
}
