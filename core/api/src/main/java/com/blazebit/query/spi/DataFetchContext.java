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

import java.util.Map;

/**
 * The context object for {@link DataFetcher} invocations.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DataFetchContext {

    /**
     * Returns the value registered for the property in this {@linkplain DataFetchContext},
     * or {@code null} if no value was found.
     *
     * @param key The property name
     * @return The value registered for the property in this {@linkplain DataFetchContext} or {@code null}
     * @param <T> The value type
     */
    <T> T findProperty(String key);

    /**
     * Creates a simple {@linkplain DataFetchContext} for the given {@link Map}.
     *
     * @param map The map
     * @return A new {@linkplain DataFetchContext}
     */
    static DataFetchContext forMap(final Map<String, Object> map) {
        return new DataFetchContext() {
            @Override
            public <T> T findProperty(String key) {
				//noinspection unchecked
				return (T) map.get( key );
            }
        };
    }
}
