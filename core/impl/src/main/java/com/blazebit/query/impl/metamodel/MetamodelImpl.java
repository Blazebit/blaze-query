/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.metamodel;

import java.util.HashSet;
import java.util.Set;

import com.blazebit.query.impl.SchemaObjectTypeImpl;
import com.blazebit.query.metamodel.Metamodel;
import com.blazebit.query.metamodel.SchemaObjectType;
import com.google.common.collect.ImmutableMap;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class MetamodelImpl implements Metamodel {

	private final ImmutableMap<String, SchemaObjectTypeImpl<?>> schemaObjects;

	public MetamodelImpl(ImmutableMap<String, SchemaObjectTypeImpl<?>> schemaObjects) {
		this.schemaObjects = schemaObjects;
	}

	@Override
	public <T> SchemaObjectTypeImpl<T> find(String qualifiedName) {
		//noinspection unchecked
		return (SchemaObjectTypeImpl<T>) schemaObjects.get( qualifiedName );
	}

	@Override
	public <T> SchemaObjectTypeImpl<T> get(String qualifiedName) {
		SchemaObjectTypeImpl<?> schemaObjectType = schemaObjects.get( qualifiedName );
		if ( schemaObjectType == null ) {
			throw new IllegalArgumentException( "No schema object type found for " + qualifiedName );
		}
		//noinspection unchecked
		return (SchemaObjectTypeImpl<T>) schemaObjectType;
	}

	@Override
	public <T> SchemaObjectTypeImpl<T> find(Class<T> schemaObjectType) {
		//noinspection unchecked
		return (SchemaObjectTypeImpl<T>) schemaObjects.get( schemaObjectType.getCanonicalName() );
	}

	@Override
	public <T> SchemaObjectTypeImpl<T> get(Class<T> schemaObjectTypeClass) {
		SchemaObjectTypeImpl<?> schemaObjectType = schemaObjects.get( schemaObjectTypeClass.getCanonicalName() );
		if ( schemaObjectType == null ) {
			throw new IllegalArgumentException(
					"No schema object type found for " + schemaObjectTypeClass.getCanonicalName() );
		}
		//noinspection unchecked
		return (SchemaObjectTypeImpl<T>) schemaObjectType;
	}

	@Override
	public Set<SchemaObjectType<?>> types() {
		return new HashSet<>( schemaObjects.values() );
	}
}
