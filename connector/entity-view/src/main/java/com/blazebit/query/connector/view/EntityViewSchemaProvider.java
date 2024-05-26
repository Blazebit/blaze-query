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
import java.util.function.Supplier;

import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.metamodel.ViewType;
import com.blazebit.query.spi.ConfigurationProvider;
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
    public EntityViewSchemaProvider() {
    }

    @Override
    public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
        EntityViewManager entityViewManager = (EntityViewManager) configurationProvider.getPropertyProvider(
                EntityViewConnectorConfig.ENTITY_VIEW_MANAGER.getPropertyName() ).get();
		//noinspection unchecked
        Supplier<EntityManager> entityManagerProvider = (Supplier<EntityManager>) (Supplier<?>) configurationProvider.getPropertyProvider(
                EntityViewConnectorConfig.ENTITY_MANAGER.getPropertyName() );
        final ImmutableMap.Builder<Class<?>, EntityViewProjectableFilterableTable<?>> builder = ImmutableMap.builder();
        for ( ViewType<?> viewType : entityViewManager.getMetamodel().getViews() ) {
            builder.put( viewType.getJavaType(), new EntityViewProjectableFilterableTable<>( entityViewManager, entityManagerProvider, viewType ) );
        }
        return builder.build();
    }
}
