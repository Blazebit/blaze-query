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

import java.util.Set;

/**
 * The metamodel of schema object types.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface Metamodel {

    /**
     * Returns the {@link SchemaObjectType} for the given class, or {@code null} if not found.
     *
     * @param qualifiedName The name for which to find the schema object type
     * @param <T> The schema object type
     * @return The {@link SchemaObjectType} for the given class, or {@code null} if not found
     */
    <T> SchemaObjectType<T> find(String qualifiedName);

    /**
     * Returns the {@link SchemaObjectType} for the given class.
     * Throws an {@link IllegalArgumentException} if the qualified name is not known.
     *
     * @param qualifiedName The name for which to find the schema object type
     * @param <T> The schema object type
     * @return The {@link SchemaObjectType} for the given class
     * @throws IllegalArgumentException if the schema object type is not known
     */
    <T> SchemaObjectType<T> get(String qualifiedName);

    /**
     * Returns the {@link SchemaObjectType} for the given class, or {@code null} if not found.
     *
     * @param schemaObjectType The schema object type class
     * @param <T> The schema object type
     * @return The {@link SchemaObjectType} for the given class, or {@code null} if not found
     */
    <T> SchemaObjectType<T> find(Class<T> schemaObjectType);

    /**
     * Returns the {@link SchemaObjectType} for the given class.
     * Throws an {@link IllegalArgumentException} if the class is not known.
     *
     * @param schemaObjectType The schema object type class
     * @param <T> The schema object type
     * @return The {@link SchemaObjectType} for the given class
     * @throws IllegalArgumentException if the schema object type is not known
     */
    <T> SchemaObjectType<T> get(Class<T> schemaObjectType);

    /**
     * Returns the schema object types.
     *
     * @return The schema object types
     */
    Set<SchemaObjectType<?>> types();
}
