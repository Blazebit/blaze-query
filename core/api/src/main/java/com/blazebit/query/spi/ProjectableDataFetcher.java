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
     */
    List<T> fetch(DataFetchContext context, int[][] projection) throws DataFetcherException;
}
