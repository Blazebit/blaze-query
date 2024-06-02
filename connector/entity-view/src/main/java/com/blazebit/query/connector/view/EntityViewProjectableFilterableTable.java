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

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.blazebit.persistence.view.metamodel.ViewType;
import com.blazebit.query.QuerySession;
import com.blazebit.query.connector.base.AccessorListEnumerator;
import com.blazebit.query.spi.DataFetchContext;
import jakarta.persistence.EntityManager;
import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.ProjectableFilterableTable;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A projectable table implementation for entity views.
 *
 * @param <EntityView> The entity view type
 * @author Christian Beikov
 * @since 1.0.0
 */
public class EntityViewProjectableFilterableTable<EntityView> extends EntityViewTable<EntityView> implements
        ProjectableFilterableTable {

    /**
     * Creates a projectable table.
     *
     * @param evm The entity view manager
     * @param entityManagerSupplier The entity manager supplier
     * @param dataContextSupplier The data context supplier
     * @param viewType The view type
     */
    public EntityViewProjectableFilterableTable(
            EntityViewManager evm,
            Supplier<EntityManager> entityManagerSupplier,
            Supplier<DataFetchContext> dataContextSupplier,
            ViewType<EntityView> viewType) {
        super( evm, entityManagerSupplier, dataContextSupplier, viewType );
    }

    @Override
    public Enumerable<Object[]> scan(DataContext root, List<RexNode> filters, int @Nullable [] projects) {
        final DataFetchContext dataFetchContext = dataContextSupplier.get();
        final EntityManager em = entityManagerSupplier.get();
        return new AbstractEnumerable<>() {
            @Override
            public Enumerator<Object[]> enumerator() {
                QuerySession session = dataFetchContext.getSession();
                List<? extends EntityView> objects = session.get( viewType.getJavaType() );
                if ( objects == null ) {
                    CriteriaBuilder<Object> cb = evm.getService( CriteriaBuilderFactory.class ).create( em, Object.class );
                    cb.from( viewType.getEntityClass(), "e" );
                    EntityViewSetting<EntityView, CriteriaBuilder<EntityView>> setting = EntityViewSetting.create(viewType.getJavaType());
                    Predicate<ViewType<?>> dynamicFetchPredicate = EntityViewConnectorConfig.DYNAMIC_FETCH.find( dataFetchContext );
                    boolean dynamicFetch = projects != null && !isIdentity( projects, accessors.length )
                            && ( dynamicFetchPredicate == null || dynamicFetchPredicate.test( viewType ) );
                    if ( dynamicFetch ) {
                        for ( int field : projects ) {
                            setting.fetch( accessors[field].getAttributePath() );
                        }
                    }
                    objects = evm.applySetting( setting, cb ).getResultList();
                    if ( !dynamicFetch ) {
                        // Only store data in session cache if projecting all the data
                        session.put( viewType.getJavaType(), objects );
                    }
                }
                return new AccessorListEnumerator<>( objects, AccessorListEnumerator.arrayConverter( accessors, projects ));
            }

            private boolean isIdentity(int[] projects, int length) {
                if ( projects.length != length ) {
                    return false;
                }
                for ( int i = 0; i < length; i++ ) {
                    if ( projects[i] != i ) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
}
