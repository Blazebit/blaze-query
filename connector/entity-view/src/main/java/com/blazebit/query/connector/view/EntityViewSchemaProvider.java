/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.view;

import com.blazebit.query.spi.PropertyProvider;

import java.util.Map;
import java.util.function.Predicate;

import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.metamodel.ViewType;
import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;
import com.google.common.collect.ImmutableMap;
import jakarta.persistence.EntityManager;

/**
 * The schema provider for the Entity-View connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class EntityViewSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public EntityViewSchemaProvider() {
	}

	@Override
	public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		EntityViewManager entityViewManager = configurationProvider.getProperty(
				EntityViewConnectorConfig.ENTITY_VIEW_MANAGER.getPropertyName() );
		Predicate<ViewType<?>> entityViewFilter = configurationProvider.getProperty(
				EntityViewConnectorConfig.ENTITY_VIEW_FILTER.getPropertyName() );
		PropertyProvider<EntityManager> entityManagerProvider = configurationProvider.getPropertyProvider(
				EntityViewConnectorConfig.ENTITY_MANAGER.getPropertyName() );

		final ImmutableMap.Builder<Class<?>, EntityViewTable<?>> builder = ImmutableMap.builder();

		for ( ViewType<?> viewType : entityViewManager.getMetamodel().getViews() ) {
			if ( entityViewFilter == null || entityViewFilter.test( viewType ) ) {
				builder.put(
						viewType.getJavaType(),
						new EntityViewTable<>(
								entityViewManager,
								entityManagerProvider,
								configurationProvider,
								viewType
						)
				);
			}
		}
		return builder.build();
	}
}
