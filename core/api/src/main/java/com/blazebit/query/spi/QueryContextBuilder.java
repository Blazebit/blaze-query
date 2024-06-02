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

import java.util.function.Supplier;

import com.blazebit.query.QueryContext;

/**
 * Builder for the Blaze-Query {@link QueryContext}.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface QueryContextBuilder {

    /**
     * Constructs a property provider for the value and registers it via {@link #setPropertyProvider(String, Supplier)}.
     *
     * @param property The property name
     * @param value The property value
     * @return {@code this} object for method chaining
     */
    default QueryContextBuilder setProperty(String property, Object value) {
        return setPropertyProvider( property, () -> value );
    }

    /**
     * Sets the given supplier as value provider for the given property name.
     *
     * @param property The property name
     * @param supplier The property value supplier
     * @return {@code this} object for method chaining
     */
    QueryContextBuilder setPropertyProvider(String property, Supplier<Object> supplier);

    /**
     * Returns the property provider for the property name.
     *
     * @param property The property name
     * @return the property provider for the property name
     * @throws IllegalArgumentException If no property provider exists
     */
    Supplier<Object> getPropertyProvider(String property);

    /**
     * Registers a fully qualified alias for a schema object type.
     *
     * @param schemaObjectType The schema object type
     * @param alias The qualified alias name
     * @return {@code this} object for method chaining
     */
    QueryContextBuilder registerSchemaObjectAlias(Class<?> schemaObjectType, String alias);

    /**
     * Registers the data fetcher for a schema object type.
     *
     * @param schemaObjectType The schema object type
     * @param dataFetcher The data fetcher
     * @return {@code this} object for method chaining
     * @param <T> The schema object type
     */
    <T> QueryContextBuilder registerSchemaObject(Class<T> schemaObjectType, DataFetcher<T> dataFetcher);

    /**
     * Loads the available services through the Java {@link java.util.ServiceLoader} API.
     *
     * @return {@code this} object for method chaining
     */
    QueryContextBuilder loadServices();

    /**
     * Builds a new {@link QueryContext} object based on the configuration provided by this builder.
     *
     * @return A new {@link QueryContext}
     */
    QueryContext build();
}