/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.spi;

import com.blazebit.query.QuerySession;

/**
 * The context object for {@link DataFetcher} invocations.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DataFetchContext {

	/**
	 * Returns the value registered for the property in this {@linkplain DataFetchContext},
	 * or {@code null} if no value was found.
	 *
	 * @param key The property name
	 * @param <T> The value type
	 * @return The value registered for the property in this {@linkplain DataFetchContext} or {@code null}
	 */
	<T> T findProperty(String key);

	/**
	 * Returns the current query session.
	 *
	 * @return the current query session
	 */
	QuerySession getSession();

}
