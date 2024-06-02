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

/**
 * A config for {@link DataFetcher}.
 *
 * @param <T> The config value type
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DataFetcherConfig<T> {

    /**
     * Returns the property name for the config.
     *
     * @return the property name for the config
     */
    String getPropertyName();

    /**
     * Returns the config value registered in the {@link DataFetchContext} or {@code null} if no value exists.
     *
     * @param context The data fetch context
     * @return The config value or {@code null}
     */
    T find(DataFetchContext context);

    /**
     * Returns the config value registered in the {@link DataFetchContext}.
     *
     * @param context The data fetch context
     * @return The config value
     * @throws IllegalStateException If no value was found for this {@linkplain DataFetcherConfig}
     */
    default T get(DataFetchContext context) {
        T value = find( context );
        if ( value == null) {
            throw new IllegalStateException("Value for " + this + " required, but not found.");
        }
        return value;
    }

    /**
     * Creates a new {@linkplain DataFetcherConfig} object,
     * that looks up a value in {@link DataFetchContext} by the given property name.
     *
     * @param propertyName The config property name
     * @param <T> The config value type
     * @return A new {@linkplain DataFetcherConfig} object
     */
    static <T> DataFetcherConfig<T> forPropertyName(String propertyName) {
        return new DataFetcherConfig<>() {
            @Override
            public String getPropertyName() {
                return propertyName;
            }

            @Override
            public T find(DataFetchContext context) {
                return context.findProperty( propertyName );
            }

            @Override
            public String toString() {
                return "DataFetcherConfig(" + propertyName + ")";
            }
        };
    }


}
