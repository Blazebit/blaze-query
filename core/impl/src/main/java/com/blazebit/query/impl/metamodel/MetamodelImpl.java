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
        if (schemaObjectType == null) {
            throw new IllegalArgumentException("No schema object type found for " + qualifiedName);
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
        if (schemaObjectType == null) {
            throw new IllegalArgumentException("No schema object type found for " + schemaObjectTypeClass.getCanonicalName());
        }
        //noinspection unchecked
        return (SchemaObjectTypeImpl<T>) schemaObjectType;
    }

    @Override
    public Set<SchemaObjectType<?>> types() {
        return new HashSet<>(schemaObjects.values());
    }
}
