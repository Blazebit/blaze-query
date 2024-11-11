/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.view;

import java.util.function.Predicate;

import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.metamodel.ViewType;
import com.blazebit.query.spi.DataFetcherConfig;
import jakarta.persistence.EntityManager;

/**
 * The configuration properties for the Entity-View connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class EntityViewConnectorConfig {

	/**
	 * Specifies the {@link jakarta.persistence.EntityManager} to use for querying data.
	 */
	public static final DataFetcherConfig<EntityManager> ENTITY_MANAGER = DataFetcherConfig.forPropertyName(
			"entityManager" );

	/**
	 * Specifies the {@link com.blazebit.persistence.view.EntityViewManager} to use for querying data.
	 */
	public static final DataFetcherConfig<EntityViewManager> ENTITY_VIEW_MANAGER = DataFetcherConfig.forPropertyName(
			"entityViewManager" );

	/**
	 * A predicate that is used for filtering the entity view types which should be queryable.
	 */
	public static final DataFetcherConfig<Predicate<ViewType<?>>> ENTITY_VIEW_FILTER = DataFetcherConfig.forPropertyName(
			"entityViewFilter" );

	private EntityViewConnectorConfig() {
	}
}
