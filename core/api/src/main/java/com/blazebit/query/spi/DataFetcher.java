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
public interface DataFetcher<T> {

	/**
	 * Returns the data format of the fetched data.
	 *
	 * @return the data format of the fetched data
	 */
	DataFormat getDataFormat();

	/**
	 * Returns freshly fetched data for a schema object.
	 *
	 * @param context The data fetching context
	 * @return the fetched data
	 * @throws DataFetcherException when an exception occurs during data fetching
	 */
	List<T> fetch(DataFetchContext context);
}
