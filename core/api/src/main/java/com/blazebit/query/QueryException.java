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