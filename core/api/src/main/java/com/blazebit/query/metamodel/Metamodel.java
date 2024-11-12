/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
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
