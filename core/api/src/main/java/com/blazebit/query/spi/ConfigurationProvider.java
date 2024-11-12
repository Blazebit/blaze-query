/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.spi;

/**
 * Provides access to configuration values in a lazy fashion.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface ConfigurationProvider extends PropertyProvider<DataFetchContext> {

	/**
	 * Returns a property value set for the property name, or {@code null} if no value was set.
	 *
	 * @param property The property name
	 * @param <X> The property type
	 * @return the property value or {@code null}
	 */
	<X> X getProperty(String property);

	/**
	 * Returns a property provider for the property name.
	 * The property provider will be resolved lazily against the currently executing query,
	 * or if no query is currently executing, will default to the values configured for a {@link com.blazebit.query.QueryContext}.
	 *
	 * @param property The property name
	 * @param <X> The type of the property that is provided
	 * @return the property provider for the property name
	 */
	<X> PropertyProvider<X> getPropertyProvider(String property);

}
