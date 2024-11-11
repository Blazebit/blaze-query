/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query;

import java.util.List;
import java.util.stream.Stream;

/**
 * An executable query to query data stored in the associated {@link QuerySession}.
 * The object is not thread-safe.
 *
 * @param <T> The result type
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface TypedQuery<T> extends Query {

	/**
	 * Sets a value for the 1-based positional query parameter at the given position.
	 *
	 * @param position The 1-based parameter position
	 * @param value The parameter value to set
	 * @return {@code this} object for method chaining
	 * @throws IllegalArgumentException If the position is invalid or the given value has the wrong type
	 */
	TypedQuery<T> setParameter(int position, Object value);

	/**
	 * Executes this query and returns a single result or {@code null} if no results were found.
	 * Throws a {@link NonUniqueResultException} if the query returns more than one result.
	 *
	 * @return the single result or {@code null} if no results were found
	 * @throws NonUniqueResultException if the query returns more than one result
	 * @throws QueryException if the query execution fails
	 * @throws IllegalStateException if the {@linkplain QuerySession} is closed
	 */
	default T findSingleResult() {
		List<T> resultList = getResultList();
		if ( resultList.isEmpty() ) {
			return null;
		}
		if ( resultList.size() > 1 ) {
			throw new NonUniqueResultException(
					"Found " + resultList.size() + " results, but expected single result.",
					getQueryString()
			);
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
	default T getSingleResult() {
		List<T> resultList = getResultList();
		if ( resultList.isEmpty() ) {
			throw new NoResultException( "No results found", getQueryString() );
		}
		if ( resultList.size() > 1 ) {
			throw new NonUniqueResultException(
					"Found " + resultList.size() + " results, but expected single result.",
					getQueryString()
			);
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
	List<T> getResultList();

	/**
	 * Executes this query and returns the query results as stream.
	 * It is vital to invoke {@link Stream#close()} to prevent resource leaks.
	 *
	 * @return the stream of the results
	 * @throws QueryException if the query execution fails
	 * @throws IllegalStateException if the {@linkplain QuerySession} is closed
	 */
	Stream<T> getResultStream();
}
