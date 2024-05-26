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

import com.blazebit.persistence.view.EntityViewManager;
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
    public static final DataFetcherConfig<EntityManager> ENTITY_MANAGER = DataFetcherConfig.forPropertyName( "entityManager" );

    /**
     * Specifies the {@link com.blazebit.persistence.view.EntityViewManager} to use for querying data.
     */
    public static final DataFetcherConfig<EntityViewManager> ENTITY_VIEW_MANAGER = DataFetcherConfig.forPropertyName( "entityViewManager" );

    private EntityViewConnectorConfig() {
    }
}
