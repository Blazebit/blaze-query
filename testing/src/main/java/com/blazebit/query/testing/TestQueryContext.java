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

import com.blazebit.query.impl.QueryContextBuilderImpl;
import com.blazebit.query.spi.QueryContextBuilder;
import com.blazebit.query.spi.QuerySchemaProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a test context for executing queries with Blaze-Query. It provides methods
 * to configure the query context, set properties, objects, schema provider, and execute queries.
 *
 * @author Max Hovens
 * @since 1.0.0
 */
public final class TestQueryContext {

    private final QueryContextBuilder queryContextBuilder = new QueryContextBuilderImpl();
    private final Map<Class<?>, List<?>> testObjects = new HashMap<>();

    private TestQueryContext() {
    }

    /**
     * Returns a new instance of the TestQueryContext.
     *
     * @return A new instance of the TestQueryContext.
     */
    public static TestQueryContext testContext() {
        return new TestQueryContext();
    }

    /**
     * Adds a property to the TestQueryContext.
     *
     * @param property The name of the property
     * @param value    The value of the property
     * @return The TestQueryContext with the added property
     */
    public TestQueryContext withProperty(String property, Object value) {
        queryContextBuilder.setProperty(property, value);
        return this;
    }


    /**
     * Adds schema objects to the TestQueryContext.
     *
     * @param <T>             The type of the schema objects
     * @param schemaObjectType The class representing the schema object type
     * @param schemaObjects    The list of schema objects to add
     * @return The updated TestQueryContext object
     */
    public <T> TestQueryContext withSchemaObjects(Class<T> schemaObjectType, List<T> schemaObjects) {
        this.testObjects.put(schemaObjectType, schemaObjects);
        return this;
    }

    /**
     * Adds an alias for a schema object type to the TestQueryContext.
     *
     * @param schemaObjectType The schema object type to add an alias for
     * @param alias            The alias to register for the schema object type
     * @return The updated TestQueryContext
     */
    public TestQueryContext withSchemaObjectAlias(Class<?> schemaObjectType, String alias) {
        queryContextBuilder.registerSchemaObjectAlias(schemaObjectType, alias);
        return this;
    }

    /**
     * Adds a {@link QuerySchemaProvider} to the TestQueryContext.
     *
     * @param schemaProvider The QuerySchemaProvider to add
     * @return The updated TestQueryContext
     */
    public TestQueryContext withSchemaProvider(QuerySchemaProvider schemaProvider) {
        queryContextBuilder.registerSchemaProvider(schemaProvider);
        return this;
    }

    /**
     * Executes a query with Blaze-Query and provides an assertable result.
     *
     * @param <T>         The result type of the query
     * @param query       The Blaze-Query query string
     * @param resultClass The result class
     * @return A QueryAssertable object for performing assertions on the query result
     * @throws IllegalArgumentException If the query string is invalid
     * @throws IllegalStateException    If the QueryContext has already been closed
     */
    public <T> QueryAssertable<T> query(String query, Class<T> resultClass) {
        return QueryAssertable.ofQuery((assertion) -> {
            try (var context = queryContextBuilder.build()) {
                var session = context.createSession();
                this.testObjects.forEach((aClass, objects) -> session.put(
                    (Class<Object>) aClass, objects));
                var typedQuery = session.createQuery(query, resultClass);
                assertion.accept(typedQuery);
            }
        });
    }
}
