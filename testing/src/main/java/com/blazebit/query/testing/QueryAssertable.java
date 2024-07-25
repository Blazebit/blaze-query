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

package com.blazebit.query.testing;

import com.blazebit.query.TypedQuery;
import java.util.function.Consumer;

/**
 * A utility class for performing assertions on a TypedQuery object.
 *
 * @param <T> The result type of the TypedQuery
 * @author Max Hovens
 * @since 1.0.0
 */
public class QueryAssertable<T> {

    private final Consumer<Consumer<TypedQuery<T>>> typedQuery;

    private QueryAssertable(Consumer<Consumer<TypedQuery<T>>> typedQuery) {
        this.typedQuery = typedQuery;
    }

    /**
     * Creates a {@link QueryAssertable} object for performing assertions on a {@link TypedQuery}.
     *
     * @param <T>        The result type of the TypedQuery
     * @param typedQuery The TypedQuery object to wrap
     * @return A QueryAssertable object
     */
    public static <T> QueryAssertable<T> ofQuery(Consumer<Consumer<TypedQuery<T>>> typedQuery) {
        return new QueryAssertable<>(typedQuery);
    }

    /**
     * Performs assertions on a TypedQuery object.
     *
     * @param result The consumer that performs the assertions
     */
    public void assertThat(Consumer<TypedQuery<T>> result) {
        typedQuery.accept(result);
    }
}
