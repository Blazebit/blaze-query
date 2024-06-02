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

package com.blazebit.query.metamodel;

import com.blazebit.query.spi.DataFetcher;

/**
 * A schema object type.
 *
 * @param <T> The schema object type
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface SchemaObjectType<T> {

    /**
     * Returns the type of the schema object.
     *
     * @return The type of the schema object
     */
    Class<T> getType();

    /**
     * Returns the data fetcher for the schema object.
     *
     * @return The data fetcher for the schema object
     */
    DataFetcher<T> getDataFetcher();
}
