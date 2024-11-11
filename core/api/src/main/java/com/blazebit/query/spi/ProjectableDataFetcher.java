/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.spi;

import java.util.List;

/**
 * An object to fetch schema object data.
 *
 * @param <T> The schema object type
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface ProjectableDataFetcher<T> extends DataFetcher<T> {

	/**
	 * Returns freshly fetched data for a schema object.
	 *
	 * @param context The data fetching context
	 * @param projection The fields that should be projected
	 * @return the fetched data
	 * @throws DataFetcherException when an exception occurs during data fetching
	 */
	List<T> fetch(DataFetchContext context, int[][] projection);
}
