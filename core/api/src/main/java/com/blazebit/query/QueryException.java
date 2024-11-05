/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query;

/**
 * Thrown if an exception during query execution occurs.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class QueryException extends RuntimeException {

	private final String queryString;

	/**
	 * Creates a new {@linkplain QueryException}.
	 *
	 * @param message The exception message
	 * @param queryString The query string
	 */
	public QueryException(String message, String queryString) {
		super( message );
		this.queryString = queryString;
	}

	/**
	 * Creates a new {@linkplain QueryException}.
	 *
	 * @param message The exception message
	 * @param cause The underlying exception cause
	 * @param queryString The query string
	 */
	public QueryException(String message, Throwable cause, String queryString) {
		super( message, cause );
		this.queryString = queryString;
	}

	/**
	 * Returns the query string associated to this exception.
	 *
	 * @return the query string associated to this exception
	 */
	public String getQueryString() {
		return queryString;
	}
}
