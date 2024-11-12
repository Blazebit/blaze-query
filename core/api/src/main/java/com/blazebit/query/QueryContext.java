/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query;

import java.util.Collections;
import java.util.Map;

import com.blazebit.query.metamodel.Metamodel;

/**
 * Context object for creating query sessions.
 * When the application has finished using the {@linkplain QueryContext},
 * and/or at application shutdown, the application should
 * close the {@linkplain QueryContext}. Once a {@linkplain QueryContext} has been closed,
 * all its {@link QuerySession} objects are considered to be in the closed state.
 * The object is thread-safe and should be reused instead of rebuilt, since it is heavyweight.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface QueryContext extends AutoCloseable {

	/**
	 * Creates a new {@link QuerySession} with default configuration.
	 *
	 * @return a new {@link QuerySession}
	 * @throws IllegalStateException if the {@linkplain QueryContext} has already been closed
	 */
	default QuerySession createSession() {
		return createSession( Collections.emptyMap() );
	}

	/**
	 * Creates a new {@link QuerySession}. The given configuration properties will override the default configuration
	 * if possible.
	 *
	 * @param properties Additional configuration that overrides the default configuration if possible
	 * @return a new {@link QuerySession}
	 * @throws IllegalStateException if the {@linkplain QueryContext} has already been closed
	 */
	QuerySession createSession(Map<String, Object> properties);

	/**
	 * Returns the metamodel of this {@linkplain QueryContext}.
	 *
	 * @return The metamodel of this {@linkplain QueryContext}
	 */
	Metamodel getMetamodel();

	/**
	 * Return an object of the specified type to allow access to the provider-specific API.
	 * If the implementation does not support the specified class, the {@link IllegalArgumentException} is thrown.
	 *
	 * @param cls the class of the object to be returned
	 * @param <T> The return type
	 * @return The unwrapped object
	 * @throws IllegalArgumentException if the type is not supported
	 */
	<T> T unwrap(Class<T> cls);

	/**
	 * Returns whether the {@linkplain QueryContext} is open.
	 *
	 * @return Whether the {@linkplain QueryContext} is open
	 */
	boolean isOpen();

	/**
	 * Closes the {@linkplain QueryContext} and all associated {@link QuerySession} to free up resources.
	 *
	 * @throws IllegalStateException if the {@linkplain QueryContext} has already been closed
	 */
	@Override
	void close();
}
