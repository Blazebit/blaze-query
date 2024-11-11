/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A config for {@link DataFetcher}.
 *
 * @param <T> The config value type
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DataFetcherConfig<T> {

	/**
	 * Returns the property name for the config.
	 *
	 * @return the property name for the config
	 */
	String getPropertyName();

	/**
	 * Returns the config value registered in the {@link DataFetchContext} or {@code null} if no value exists.
	 *
	 * @param context The data fetch context
	 * @return The config value or {@code null}
	 */
	T find(DataFetchContext context);

	/**
	 * Returns the config value registered in the {@link DataFetchContext} or {@code null} if no value exists.
	 *
	 * @param context The data fetch context
	 * @return The config value or {@code null}
	 */
	default List<T> findAll(DataFetchContext context) {
		Object value = find( context );
		if ( value == null ) {
			return Collections.emptyList();
		}
		if ( value instanceof Collection ) {
			//noinspection unchecked
			return new ArrayList<>( (Collection<T>) value );
		}
		//noinspection unchecked
		return Collections.singletonList( (T) value );
	}

	/**
	 * Returns the config value registered in the {@link DataFetchContext}.
	 *
	 * @param context The data fetch context
	 * @return The config value
	 * @throws IllegalStateException If no value was found for this {@linkplain DataFetcherConfig}
	 */
	default T get(DataFetchContext context) {
		T value = find( context );
		if ( value == null ) {
			throw new IllegalStateException( "Value for " + this + " required, but not found." );
		}
		return value;
	}

	/**
	 * Returns the config value registered in the {@link DataFetchContext} or {@code null} if no value exists.
	 *
	 * @param context The data fetch context
	 * @return The config value or {@code null}
	 */
	default List<T> getAll(DataFetchContext context) {
		List<T> value = findAll( context );
		if ( value.isEmpty() ) {
			throw new IllegalStateException( "Value for " + this + " required, but not found." );
		}
		return value;
	}

	/**
	 * Creates a new {@linkplain DataFetcherConfig} object,
	 * that looks up a value in {@link DataFetchContext} by the given property name.
	 *
	 * @param propertyName The config property name
	 * @param <T> The config value type
	 * @return A new {@linkplain DataFetcherConfig} object
	 */
	static <T> DataFetcherConfig<T> forPropertyName(String propertyName) {
		return new DataFetcherConfig<>() {
			@Override
			public String getPropertyName() {
				return propertyName;
			}

			@Override
			public T find(DataFetchContext context) {
				return context.findProperty( propertyName );
			}

			@Override
			public String toString() {
				return "DataFetcherConfig(" + propertyName + ")";
			}
		};
	}


}
