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

import com.blazebit.query.QuerySession;

/**
 * The PropertyProvider interface is a functional interface that provides an object
 * based on the given DataFetchContext. It is used to lazily fetch property values
 * or property providers for a given context.
 *
 * @param <X> The type of object to be provided
 * @author Max Hovens
 * @since 1.0.0
 */
@FunctionalInterface
public interface PropertyProvider<X> {

  /**
   * Provides the Object. An {@link DataFetchContext} is supplied for the provider to use properties
   * from the context.
   *
   * @param context the {@link DataFetchContext} from the current {@link QuerySession}
   * @return the object to provide
   */
    X provide(DataFetchContext context);
}
