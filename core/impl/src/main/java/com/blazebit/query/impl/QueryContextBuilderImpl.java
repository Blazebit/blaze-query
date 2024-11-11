/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import com.blazebit.query.QueryContext;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.PropertyProvider;
import com.blazebit.query.spi.QueryContextBuilder;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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
	public QueryContextBuilder setProperty(String property, Object value) {
		propertyProviders.put( property, new PropertyProviderImpl<>( value ) );
		return this;
	}

	@Override
	public QueryContextBuilder setPropertyProvider(String property, PropertyProvider provider) {
		propertyProviders.put( property, provider );
		return this;
	}

	@Override
	public <X> PropertyProvider<X> getPropertyProvider(String property) {
		PropertyProvider<?> propertyProvider = propertyProviders.get( property );
		if ( propertyProvider == null ) {
			throw new IllegalArgumentException( "No property provider found for property: " + property );
		}
		//noinspection unchecked
		return (PropertyProvider<X>) propertyProvider;
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
		schemaProviders.add( querySchemaProvider );
		return this;
	}

	@Override
	public QueryContextBuilder loadServices() {
		for ( QuerySchemaProvider querySchemaProvider : ServiceLoader.load( QuerySchemaProvider.class ) ) {
			registerSchemaProvider( querySchemaProvider );
		}
		return this;
	}

	@Override
	public QueryContext build() {
		return new QueryContextImpl( this );
	}

	public Properties getProperties() {
		Properties props = new Properties();
		for ( Map.Entry<String, PropertyProvider<?>> entry : propertyProviders.entrySet() ) {
			PropertyProvider<?> propertyProvider = entry.getValue();
			if ( propertyProvider instanceof PropertyProviderImpl ) {
				props.put( entry.getKey(), ((PropertyProviderImpl<?>) propertyProvider).value );
			}
		}
		return props;
	}

	/**
	 * @author Christian Beikov
	 * @since 1.0.0
	 */
	private static final class PropertyProviderImpl<X> implements PropertyProvider<X> {
		private final X value;

		/**
		 * Creates a new property provider.
		 *
		 * @param value The value
		 */
		public PropertyProviderImpl(X value) {
			this.value = value;
		}

		@Override
		public X provide(DataFetchContext context) {
			return value;
		}
	}
}
