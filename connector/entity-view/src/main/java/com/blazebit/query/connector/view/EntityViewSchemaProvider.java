/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.view;

import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.metamodel.ViewType;
import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.PropertyProvider;
import com.blazebit.query.spi.QuerySchemaProvider;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * The schema provider for the Entity-View connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class EntityViewSchemaProvider implements QuerySchemaProvider {
	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		EntityViewManager entityViewManager = configurationProvider.getProperty(
				EntityViewConnectorConfig.ENTITY_VIEW_MANAGER.getPropertyName() );
		Predicate<ViewType<?>> entityViewFilter = configurationProvider.getProperty(
				EntityViewConnectorConfig.ENTITY_VIEW_FILTER.getPropertyName() );
		PropertyProvider<EntityManager> entityManagerProvider = configurationProvider.getPropertyProvider(
				EntityViewConnectorConfig.ENTITY_MANAGER.getPropertyName() );

		final Set<EntityViewTable<?>> dataFetchers = new HashSet<>(
				entityViewManager.getMetamodel().getViews().size() );

		for ( ViewType<?> viewType : entityViewManager.getMetamodel().getViews() ) {
			if ( entityViewFilter == null || entityViewFilter.test( viewType ) ) {
				dataFetchers.add(
						new EntityViewTable<>(
								entityViewManager,
								entityManagerProvider,
								configurationProvider,
								viewType
						)
				);
			}
		}

		return dataFetchers;
	}
}
