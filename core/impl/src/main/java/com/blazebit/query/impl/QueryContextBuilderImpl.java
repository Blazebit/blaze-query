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
package com.blazebit.query.impl;

import com.blazebit.query.QueryContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.PropertyProvider;
import com.blazebit.query.spi.QueryContextBuilder;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class QueryContextBuilderImpl implements QueryContextBuilder {

    final Map<String, PropertyProvider<?>> propertyProviders = new HashMap<>();
    final ArrayList<QuerySchemaProvider> schemaProviders = new ArrayList<>();
    final Map<String, SchemaObjectTypeImpl<?>> schemaObjects = new HashMap<>();
    final Map<String, String> schemaObjectNames = new HashMap<>();

    @Override
    public QueryContextBuilder setPropertyProvider(String property, PropertyProvider provider) {
        propertyProviders.put(property, provider);
        return this;
    }

    @Override
    public PropertyProvider getPropertyProvider(String property) {
        PropertyProvider propertyProvider = propertyProviders.get(property);
        if (propertyProvider == null) {
            throw new IllegalArgumentException("No property provider found for property: " + property);
        }
        return propertyProvider;
    }

    @Override
    public QueryContextBuilder registerSchemaObjectAlias(Class<?> schemaObjectType, String alias) {
        schemaObjectNames.put( alias, schemaObjectType.getCanonicalName() );
        return this;
    }

    @Override
    public <T> QueryContextBuilder registerSchemaObject(Class<T> clazz, DataFetcher<T> dataFetcher) {
        schemaObjects.put( clazz.getCanonicalName(), new SchemaObjectTypeImpl<>( clazz, dataFetcher ) );
        return this;
    }

    @Override
    public QueryContextBuilder registerSchemaProvider(QuerySchemaProvider querySchemaProvider) {
        schemaProviders.add(querySchemaProvider);
        return this;
    }

    @Override
    public QueryContextBuilder loadServices() {
        for ( QuerySchemaProvider querySchemaProvider : ServiceLoader.load( QuerySchemaProvider.class ) ) {
            registerSchemaProvider(querySchemaProvider);
        }
        return this;
    }

    @Override
    public QueryContext build() {
        return new QueryContextImpl( this );
    }
}
