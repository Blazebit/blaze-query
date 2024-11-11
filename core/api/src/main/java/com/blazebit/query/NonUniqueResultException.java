/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query;

/**
 * Thrown if a unique result was expected, but multiple results were found.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class NonUniqueResultException extends QueryException {

	/**
	 * Creates a new {@linkplain NonUniqueResultException}.
	 *
	 * @param message The exception message
	 * @param queryString The query string
	 */
	public NonUniqueResultException(String message, String queryString) {
		super( message, queryString );
	}

	/**
	 * Creates a new {@linkplain NonUniqueResultException}.
	 *
	 * @param message The exception message
	 * @param cause The underlying exception cause
	 * @param queryString The query string
	 */
	public NonUniqueResultException(String message, Throwable cause, String queryString) {
		super( message, cause, queryString );
	}

}
