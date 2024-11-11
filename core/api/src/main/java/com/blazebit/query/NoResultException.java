/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query;

/**
 * Thrown if a unique result was expected, but no results were found.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class NoResultException extends QueryException {

	/**
	 * Creates a new {@linkplain NoResultException}.
	 *
	 * @param message The exception message
	 * @param queryString The query string
	 */
	public NoResultException(String message, String queryString) {
		super( message, queryString );
	}

	/**
	 * Creates a new {@linkplain NoResultException}.
	 *
	 * @param message The exception message
	 * @param cause The underlying exception cause
	 * @param queryString The query string
	 */
	public NoResultException(String message, Throwable cause, String queryString) {
		super( message, cause, queryString );
	}

}
