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
import java.util.function.Supplier;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.blazebit.persistence.view.metamodel.ViewType;
import com.blazebit.query.connector.base.AccessorListEnumerator;
import jakarta.persistence.EntityManager;
import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.ProjectableFilterableTable;
import org.checkerframework.checker.nullness.qual.Nullable;

public class EntityViewProjectableFilterableTable<EntityView> extends EntityViewTable<EntityView> implements
        ProjectableFilterableTable {

    public EntityViewProjectableFilterableTable(
            EntityViewManager evm,
            Supplier<EntityManager> entityManagerSupplier,
            ViewType<EntityView> viewType) {
        super( evm, entityManagerSupplier, viewType );
    }

    @Override
    public Enumerable<Object[]> scan(DataContext root, List<RexNode> filters, int @Nullable [] projects) {
        final EntityManager em = entityManagerSupplier.get();
        return new AbstractEnumerable<>() {
            @Override
            public Enumerator<Object[]> enumerator() {
                CriteriaBuilder<Object> cb = evm.getService( CriteriaBuilderFactory.class ).create( em, Object.class );
                cb.from( viewType.getEntityClass(), "e" );
                EntityViewSetting<EntityView, CriteriaBuilder<EntityView>> setting =
                        EntityViewSetting.create(viewType.getJavaType() );
                if ( projects != null ) {
                    for ( int field : projects ) {
                        setting.fetch( accessors[field].getAttributePath() );
                    }
                }
                List<EntityView> result = evm.applySetting( setting, cb ).getResultList();
                return new AccessorListEnumerator<>( result, AccessorListEnumerator.arrayConverter( accessors, projects ));
            }
        };
    }
}
