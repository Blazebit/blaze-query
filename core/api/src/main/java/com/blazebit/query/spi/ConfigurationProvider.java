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

/**
 * Provides access to configuration values in a lazy fashion.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface ConfigurationProvider {

    /**
     * Returns a property value set for the property name, or {@code null} if no value was set.
     *
     * @param property The property name
     * @param <X> The property type
     * @return the property value or {@code null}
     */
    <X> X getProperty(String property);

    /**
     * Returns a property provider for the property name.
     * The property provider will be resolved lazily against the currently executing query,
     * or if no query is currently executing, will default to the values configured for a {@link com.blazebit.query.QueryContext}.
     *
     * @param property The property name
     * @param <X> The property type
     * @return the property provider for the property name
     */
    <X> Supplier<X> getPropertyProvider(String property);

}
