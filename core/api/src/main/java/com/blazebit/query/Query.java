/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query;

import java.util.Map;

/**
 * An executable query to query data stored in the associated {@link QuerySession}.
 * The object is not thread-safe.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface Query {

	/**
	 * Returns the associated {@link QuerySession} that created this {@linkplain Query}.
	 *
	 * @return the associated {@link QuerySession} that created this {@linkplain Query}
	 */
	QuerySession getSession();

	/**
	 * Sets a value for the 1-based positional query parameter at the given position.
	 *
	 * @param position The 1-based parameter position
	 * @param value The parameter value to set
	 * @return {@code this} object for method chaining
	 * @throws IllegalArgumentException If the position is invalid or the given value has the wrong type
	 */
	Query setParameter(int position, Object value);

	/**
	 * Returns the query string of this query.
	 *
	 * @return the query string of this query
	 */
	String getQueryString();

	/**
	 * Set a {@link Query} property or hint.
	 * If a vendor-specific property or hint is not recognized, it is silently ignored.
	 *
	 * @param propertyName name of property or hint
	 * @param value value for property or hint
	 * @throws IllegalArgumentException if the second argument is not valid for the implementation
	 * @throws IllegalStateException if the {@linkplain QuerySession} is closed
	 */
	void setProperty(String propertyName, Object value);

	/**
	 * Returns the properties and hints and associated values that are in effect
	 * for the {@linkplain Query}. Changing the contents of the map does
	 * not change the configuration in effect.
	 *
	 * @return map of properties and hints in effect for the {@linkplain Query}
	 * @throws IllegalStateException if the {@linkplain QuerySession} is closed
	 */
	Map<String, Object> getProperties();

	/**
	 * Return an object of the specified type to allow access to the provider-specific API.
	 * If the implementation does not support the specified class, the {@link IllegalArgumentException} is thrown.
	 *
	 * @param cls the class of the object to be returned
	 * @param <T> The return type
	 * @return The unwrapped object
	 * @throws IllegalArgumentException if the type is not supported
	 * @throws IllegalStateException if the {@linkplain QuerySession} is closed
	 */
	<T> T unwrap(Class<T> cls);
}
