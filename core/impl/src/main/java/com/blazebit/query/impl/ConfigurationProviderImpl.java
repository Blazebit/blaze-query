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
import com.google.common.collect.ImmutableMap;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ConfigurationProviderImpl implements ConfigurationProvider, Supplier<DataFetchContext>, DataFetchContext {

    private final ImmutableMap<String, Supplier<Object>> propertyProviders;
    private final Map<String, Supplier<Object>> lazyPropertyProviders;
    private final ThreadLocal<QueryImpl> currentQuery = new ThreadLocal<>();

    public ConfigurationProviderImpl(ImmutableMap<String, Supplier<Object>> propertyProviders) {
        this.propertyProviders = propertyProviders;
        this.lazyPropertyProviders = new HashMap<>();
    }

    @Override
    public <X> X getProperty(String property) {
        Supplier<Object> supplier = propertyProviders.get( property );
        //noinspection unchecked
        return supplier == null ? null : (X) supplier.get();
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
        QueryImpl query = currentQuery.get();
        Object value = null;
        if (query != null) {
            value = query.findProperty(propertyName);
        }
        if (value == null) {
            Supplier<Object> supplier = propertyProviders.get( propertyName );
            if ( supplier != null ) {
                value = supplier.get();
            }
        }
        //noinspection unchecked
        return (T) value;
    }

    @Override
    public QuerySession getSession() {
        QueryImpl query = currentQuery.get();
        if (query == null) {
            throw new IllegalStateException("No current query");
        }
        return query.getSession();
    }

    public void setQuery(QueryImpl query) {
        currentQuery.set( query );
    }

    public void unsetQuery() {
        currentQuery.remove();
    }

    private class LazyPropertyProvider implements Supplier<Object> {

        private final String propertyName;

        public LazyPropertyProvider(String propertyName) {
            this.propertyName = propertyName;
        }

        @Override
        public Object get() {
            QueryImpl query = currentQuery.get();
            if (query != null) {
                return query.findProperty(propertyName);
            }
            Supplier<Object> supplier = propertyProviders.get( propertyName );
            if (supplier == null) {
                throw new IllegalArgumentException("Could not resolve property " + propertyName);
            }
            return supplier.get();
        }
    }
}
