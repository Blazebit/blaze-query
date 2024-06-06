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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.blazebit.query.QuerySession;
import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.PropertyProvider;
import com.google.common.collect.ImmutableMap;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ConfigurationProviderImpl implements ConfigurationProvider, Supplier<DataFetchContext>, DataFetchContext {

    private final ImmutableMap<String, PropertyProvider> propertyProviders;
    private final Map<String, PropertyProvider> lazyPropertyProviders;
    private final ThreadLocal<TypedQueryImpl> currentQuery = new ThreadLocal<>();

    public ConfigurationProviderImpl(ImmutableMap<String, PropertyProvider> propertyProviders) {
        this.propertyProviders = propertyProviders;
        this.lazyPropertyProviders = new HashMap<>();
    }

    @Override
    public <X> X getProperty(String property) {
        PropertyProvider supplier = propertyProviders.get( property );
        //noinspection unchecked
        return supplier == null ? null : (X) supplier.provide(this);
    }

    @Override
    public <X> Supplier<X> getPropertyProvider(String property) {
        //noinspection unchecked
        return (Supplier<X>) lazyPropertyProviders.computeIfAbsent( property, LazyPropertyProvider::new );
    }

    @Override
    public DataFetchContext get() {
        return this;
    }

    @Override
    public <T> T findProperty(String propertyName) {
        TypedQueryImpl query = currentQuery.get();
        Object value = null;
        if (query != null) {
            value = query.findProperty(propertyName);
        }
        if (value == null) {
            PropertyProvider supplier = propertyProviders.get( propertyName );
            if ( supplier != null ) {
                value = supplier.provide(this);
            }
        }
        //noinspection unchecked
        return (T) value;
    }

    @Override
    public QuerySession getSession() {
        TypedQueryImpl query = currentQuery.get();
        if (query == null) {
            throw new IllegalStateException("No current query");
        }
        return query.getSession();
    }

    public void setQuery(TypedQueryImpl query) {
        currentQuery.set( query );
    }

    public void unsetQuery() {
        currentQuery.remove();
    }

    public boolean hasCurrentQuery() {
        return currentQuery.get() != null;
    }

    private class LazyPropertyProvider implements PropertyProvider {

        private final String propertyName;

        public LazyPropertyProvider(String propertyName) {
            this.propertyName = propertyName;
        }

        @Override
        public Object provide(DataFetchContext context) {
            TypedQueryImpl query = currentQuery.get();
            if (query != null) {
                return query.findProperty(propertyName);
            }
            PropertyProvider supplier = propertyProviders.get( propertyName );
            if (supplier == null) {
                throw new IllegalArgumentException("Could not resolve property " + propertyName);
            }
            return supplier.provide(context);
        }
    }
}
