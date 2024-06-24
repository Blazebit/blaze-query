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

package com.blazebit.query;

import com.blazebit.query.spi.DataFetcherException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blazebit.query.metamodel.SchemaObjectType;

/**
 * A session within which queries can be executed against schema object data.
 * {@linkplain QuerySession} stores schema object data against which queries run, and if necessary,
 * fetches data by invoking the {@link com.blazebit.query.spi.DataFetcher} registered for a schema object.
 * When the application has finished using the {@linkplain QuerySession}, the application should close the {@linkplain QuerySession}.
 * Once a {@linkplain QuerySession} has been closed, all its {@link Query} objects become unusable.
 * The object is not thread-safe.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface QuerySession extends AutoCloseable {

    /**
     * Returns the {@link QueryContext} that created this {@linkplain QuerySession}.
     *
     * @return the {@link QueryContext} that created this {@linkplain QuerySession}
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    QueryContext getContext();

    /**
     * Creates an executable query associated to this {@linkplain QuerySession}.
     *
     * @param queryString A Blaze-Query query string
     * @return a new query instance
     * @throws IllegalArgumentException If the query string is invalid
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    default TypedQuery<Object[]> createQuery(String queryString) {
        return createQuery( queryString, Collections.emptyMap() );
    }

    /**
     * Creates an executable query associated to this {@linkplain QuerySession}.
     *
     * @param queryString A Blaze-Query query string
     * @param properties The properties for the query, which should override {@linkplain QuerySession} properties
     * @return a new query instance
     * @throws IllegalArgumentException If the query string is invalid
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    default TypedQuery<Object[]> createQuery(String queryString, Map<String, Object> properties) {
        return createQuery( queryString, Object[].class, properties );
    }

    /**
     * Creates an executable query associated to this {@linkplain QuerySession}.
     *
     * @param queryString A Blaze-Query query string
     * @param resultClass The result class
     * @param <T> The result type
     * @return a new query instance
     * @throws IllegalArgumentException If the query string is invalid
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    default <T> TypedQuery<T> createQuery(String queryString, Class<T> resultClass) {
        return createQuery( queryString, resultClass, Collections.emptyMap() );
    }

    /**
     * Creates an executable query associated to this {@linkplain QuerySession}.
     *
     * @param queryString A Blaze-Query query string
     * @param resultClass The result class
     * @param properties The properties for the query, which should override {@linkplain QuerySession} properties
     * @param <T> The result type
     * @return a new query instance
     * @throws IllegalArgumentException If the query string is invalid
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    <T> TypedQuery<T> createQuery(String queryString, Class<T> resultClass, Map<String, Object> properties);

    /**
     * Returns the schema object data for the given schema object type stored in this {@linkplain QuerySession},
     * or {@code null} if there is none.
     *
     * @param schemaObjectType The schema object type
     * @return the schema object data, or {@code null}
     * @param <T> The schema object type
     * @throws IllegalArgumentException If the schema object type is not known
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    <T> List<? extends T> get(Class<T> schemaObjectType);

    /**
     * Returns the schema object data for the given schema object type stored in this {@linkplain QuerySession},
     * fetching and storing the data by calling the underlying {@link com.blazebit.query.spi.DataFetcher} if necessary.
     *
     * @param schemaObjectType The schema object type
     * @return the schema object data
     * @param <T> The schema object type
     * @throws IllegalArgumentException If the schema object type is not known
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     * @throws DataFetcherException when an exception occurs during data fetching
     */
    <T> List<? extends T> getOrFetch(Class<T> schemaObjectType) throws DataFetcherException;

    /**
     * Stores the given schema objects for the given schema object type in this {@linkplain QuerySession}
     * and returns the previously stored schema object data stored, or {@code null} if there was none.
     *
     * @param schemaObjectType The schema object type
     * @param schemaObjects The new schema object data to store
     * @param <T> The schema object type
     * @return the previous schema object data, or {@code null}
     * @throws IllegalArgumentException If the schema object type is not known
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    <T> List<? extends T> put(Class<T> schemaObjectType, List<? extends T> schemaObjects);

    /**
     * Removes the schema objects for the given schema object type from this {@linkplain QuerySession}
     * and returns the previously stored schema object data stored, or {@code null} if there was none.
     *
     * @param schemaObjectType The schema object type
     * @param <T> The schema object type
     * @return the previous schema object data, or {@code null}
     * @throws IllegalArgumentException If the schema object type is not known
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    <T> List<? extends T> remove(Class<T> schemaObjectType);

    /**
     * Refreshes the schema object data for the given schema object type by fetching data again through
     * the underlying {@link com.blazebit.query.spi.DataFetcher}.
     *
     * @param schemaObjectType The schema object type
     * @param <T> The schema object type
     * @return the refreshed schema object data
     * @throws IllegalArgumentException If the schema object type is not known
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    default <T> List<? extends T> refresh(Class<T> schemaObjectType) {
        remove( schemaObjectType );
        try {
            return getOrFetch( schemaObjectType );
        } catch (DataFetcherException e) {
            return Collections.emptyList();
        }
    }

    /**
     * Returns the schema object types for which data is stored in this {@linkplain QuerySession}.
     *
     * @return the schema object types for which data is stored in this {@linkplain QuerySession}
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    Set<SchemaObjectType<?>> getFetchedSchemaObjectTypes();

    /**
     * Clears the schema object data fetched by {@link com.blazebit.query.spi.DataFetcher} instances.
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    void clear();

    /**
     * Set a {@link QuerySession} property or hint.
     * If a vendor-specific property or hint is not recognized, it is silently ignored.
     *
     * @param propertyName name of property or hint
     * @param value  value for property or hint
     * @throws IllegalArgumentException if the second argument is not valid for the implementation
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    void setProperty(String propertyName, Object value);

    /**
     * Returns the properties and hints and associated values that are in effect
     * for the {@linkplain QuerySession}. Changing the contents of the map does
     * not change the configuration in effect.
     *
     * @return map of properties and hints in effect for the {@linkplain QuerySession}
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    Map<String, Object> getProperties();

    /**
     * Return an object of the specified type to allow access to the provider-specific API.
     * If the implementation does not support the specified class, the {@link IllegalArgumentException} is thrown.
     *
     * @param cls the class of the object to be returned
     * @param <T> The return type
     * @return The unwrapped object
     * @throws IllegalArgumentException if the type is not supported
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    <T> T unwrap(Class<T> cls);

    /**
     * Determine whether the entity manager is open.
     * @return true until the entity manager has been closed
     */
    public boolean isOpen();

    /**
     * Closes the {@linkplain QuerySession} to free up resources.
     * @throws IllegalStateException if the {@linkplain QuerySession} is closed
     */
    @Override
    void close();
}