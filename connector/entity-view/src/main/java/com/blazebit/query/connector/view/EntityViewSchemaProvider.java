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

package com.blazebit.query.connector.view;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.metamodel.ViewType;
import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;
import com.google.common.collect.ImmutableMap;
import jakarta.persistence.EntityManager;

/**
 * The configuration properties for the Entity-View connector.
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
        EntityViewManager entityViewManager = configurationProvider.<EntityViewManager>getPropertyProvider(
                EntityViewConnectorConfig.ENTITY_VIEW_MANAGER.getPropertyName() ).get();
        Predicate<ViewType<?>> entityViewFilter = configurationProvider.getProperty(
                EntityViewConnectorConfig.ENTITY_VIEW_FILTER.getPropertyName() );
        Supplier<EntityManager> entityManagerProvider = configurationProvider.getPropertyProvider(
                EntityViewConnectorConfig.ENTITY_MANAGER.getPropertyName() );
        //noinspection unchecked
        Supplier<DataFetchContext> dataContextSupplier = (Supplier<DataFetchContext>) configurationProvider;
        final ImmutableMap.Builder<Class<?>, EntityViewProjectableFilterableTable<?>> builder = ImmutableMap.builder();
        for ( ViewType<?> viewType : entityViewManager.getMetamodel().getViews() ) {
            if ( entityViewFilter == null || entityViewFilter.test( viewType ) ) {
                builder.put(
                        viewType.getJavaType(),
                        new EntityViewProjectableFilterableTable<>(
                                entityViewManager,
                                entityManagerProvider,
                                dataContextSupplier,
                                viewType
                        )
                );
            }
        }
        return builder.build();
    }
}
