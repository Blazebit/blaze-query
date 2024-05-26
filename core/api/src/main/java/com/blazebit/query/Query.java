/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
	 * Executes this query and returns a single result or {@code null} if no results were found.
	 * Throws a {@link NonUniqueResultException} if the query returns more than one result.
	 *
	 * @return the single result or {@code null} if no results were found
	 * @throws NonUniqueResultException if the query returns more than one result
	 * @throws QueryException if the query execution fails
	 * @throws IllegalStateException if the {@linkplain QuerySession} is closed
	 */
	default Object[] findSingleResult() {
		List<Object[]> resultList = getResultList();
		if (resultList.isEmpty()) {
			return null;
		}
		if (resultList.size() > 1) {
			throw new NonUniqueResultException( "Found " + resultList.size() + " results, but expected single result.", getQueryString() );
		}
		return resultList.get( 0 );
	}

	/**
	 * Executes this query and returns the query results.
	 *
	 * @return the single result
	 * @throws NoResultException if the query returns no results
	 * @throws NonUniqueResultException if the query returns more than one result
	 * @throws QueryException if the query execution fails
	 * @throws IllegalStateException if the {@linkplain QuerySession} is closed
	 */
	default Object[] getSingleResult() {
		List<Object[]> resultList = getResultList();
		if (resultList.isEmpty()) {
			throw new NoResultException( "No results found", getQueryString() );
		}
		if (resultList.size() > 1) {
			throw new NonUniqueResultException( "Found " + resultList.size() + " results, but expected single result.", getQueryString() );
		}
		return resultList.get( 0 );
	}

	/**
	 * Executes this query and returns the query results.
	 *
	 * @return the list of the results
	 * @throws QueryException if the query execution fails
	 * @throws IllegalStateException if the {@linkplain QuerySession} is closed
	 */
	List<Object[]> getResultList();

	/**
	 * Executes this query and returns the query results as stream.
	 * It is vital to invoke {@link Stream#close()} to prevent resource leaks.
	 *
	 * @return the stream of the results
	 * @throws QueryException if the query execution fails
	 * @throws IllegalStateException if the {@linkplain QuerySession} is closed
	 */
	Stream<Object[]> getResultStream();

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
	 * @param value  value for property or hint
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
	 * @throws IllegalArgumentException if the type is not supported
	 * @throws IllegalStateException if the {@linkplain QuerySession} is closed
	 */
	<T> T unwrap(Class<T> cls);
}