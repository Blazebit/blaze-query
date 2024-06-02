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

import java.util.Iterator;
import java.util.ServiceLoader;

import com.blazebit.query.QueryContext;

/**
 * Bootstrap class to obtain a {@link QueryContext} or builder for such.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class Queries {

    private static volatile QueryContextBuilderFactory builderFactory;

    private Queries() {
    }

    /**
     * Creates a new {@link QueryContext}, based on default configuration.
     *
     * @return a new {@link QueryContext}
     */
    public static QueryContext createQueryContext() {
        return createQueryContextBuilder().build();
    }

    /**
     * Creates a new {@link QueryContextBuilder} with all discoverable services loaded.
     *
     * @return a new {@link QueryContextBuilder}
     */
    public static QueryContextBuilder createQueryContextBuilder() {
        return createEmptyQueryContextBuilder().loadServices();
    }

    /**
     * Creates a new empty {@link QueryContextBuilder}. Services have not been loaded yet for this builder
     * and have to be loaded manually.
     *
     * @return a new empty {@link QueryContextBuilder}
     */
    public static QueryContextBuilder createEmptyQueryContextBuilder() {
        QueryContextBuilderFactory factory = builderFactory;
        if (factory == null) {
            ServiceLoader<QueryContextBuilderFactory> contextBuilders = ServiceLoader.load( QueryContextBuilderFactory.class );
            Iterator<QueryContextBuilderFactory> iterator = contextBuilders.iterator();
            if (iterator.hasNext()) {
                builderFactory = factory = iterator.next();
            } else {
                throw new IllegalStateException(
                        "No QueryContextBuilderFactory found on the class path. Please check if a valid implementation is on the class path." );
            }
        }
        return factory.creatBuilder();
    }
}
