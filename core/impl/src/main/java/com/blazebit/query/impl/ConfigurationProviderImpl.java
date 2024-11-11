/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
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

	private final ImmutableMap<String, PropertyProvider<?>> propertyProviders;
	private final Map<String, PropertyProvider<?>> lazyPropertyProviders;
	private final ThreadLocal<TypedQueryImpl> currentQuery = new ThreadLocal<>();

	public ConfigurationProviderImpl(ImmutableMap<String, PropertyProvider<?>> propertyProviders) {
		this.propertyProviders = propertyProviders;
		this.lazyPropertyProviders = new HashMap<>();
	}

	@Override
	public <X> X getProperty(String property) {
		PropertyProvider propertyProvider = propertyProviders.get( property );
		return propertyProvider == null ? null : (X) propertyProvider.provide( this );
	}

	@Override
	public <X> PropertyProvider<X> getPropertyProvider(String property) {
		//noinspection unchecked
		return (PropertyProvider<X>) lazyPropertyProviders.computeIfAbsent( property, LazyPropertyProvider::new );
	}

	@Override
	public DataFetchContext get() {
		return this;
	}

	@Override
	public <T> T findProperty(String propertyName) {
		TypedQueryImpl query = currentQuery.get();
		Object value = null;
		if ( query != null ) {
			value = query.findProperty( propertyName );
		}
		if ( value == null ) {
			PropertyProvider supplier = propertyProviders.get( propertyName );
			if ( supplier != null ) {
				value = supplier.provide( this );
			}
		}
		//noinspection unchecked
		return (T) value;
	}

	@Override
	public QuerySession getSession() {
		TypedQueryImpl query = currentQuery.get();
		if ( query == null ) {
			throw new IllegalStateException( "No current query" );
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

	@Override
	public DataFetchContext provide(DataFetchContext context) {
		return context;
	}

	private class LazyPropertyProvider implements PropertyProvider {

		private final String propertyName;

		public LazyPropertyProvider(String propertyName) {
			this.propertyName = propertyName;
		}

		@Override
		public Object provide(DataFetchContext context) {
			TypedQueryImpl query = currentQuery.get();
			if ( query != null ) {
				return query.findProperty( propertyName );
			}
			PropertyProvider supplier = propertyProviders.get( propertyName );
			if ( supplier == null ) {
				throw new IllegalArgumentException( "Could not resolve property " + propertyName );
			}
			return supplier.provide( context );
		}
	}
}
