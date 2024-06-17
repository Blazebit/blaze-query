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

import com.blazebit.query.spi.DataFetcherException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.blazebit.persistence.view.SubGraph;
import com.blazebit.persistence.view.metamodel.ManagedViewType;
import com.blazebit.persistence.view.metamodel.MethodAttribute;
import com.blazebit.persistence.view.metamodel.MethodMapAttribute;
import com.blazebit.persistence.view.metamodel.MethodPluralAttribute;
import com.blazebit.persistence.view.metamodel.MethodSingularAttribute;
import com.blazebit.persistence.view.metamodel.Type;
import com.blazebit.persistence.view.metamodel.ViewType;
import com.blazebit.query.connector.base.MethodFieldAccessor;
import com.blazebit.query.spi.CollectionDataFormat;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFormat;
import com.blazebit.query.spi.DataFormatField;
import com.blazebit.query.spi.MapDataFormat;
import com.blazebit.query.spi.ProjectableDataFetcher;
import jakarta.persistence.EntityManager;

/**
 * A table implementation for entity views.
 *
 * @param <EntityView> The entity view type
 * @author Christian Beikov
 * @since 1.0.0
 */
public class EntityViewTable<EntityView> implements ProjectableDataFetcher<EntityView> {
    protected final EntityViewManager evm;
    protected final Supplier<EntityManager> entityManagerSupplier;
    protected final Supplier<DataFetchContext> dataContextSupplier;
    protected final ViewType<EntityView> viewType;
    private final DataFormat dataFormat;

    /**
     * Creates a table.
     *
     * @param evm The entity view manager
     * @param entityManagerSupplier The entity manager supplier
     * @param dataContextSupplier The data context supplier
     * @param viewType The view type
     */
    public EntityViewTable(
            EntityViewManager evm,
            Supplier<EntityManager> entityManagerSupplier,
            Supplier<DataFetchContext> dataContextSupplier,
            ViewType<EntityView> viewType) {
        this.evm = evm;
        this.entityManagerSupplier = entityManagerSupplier;
        this.dataContextSupplier = dataContextSupplier;
        this.viewType = viewType;
        this.dataFormat = createFormat( viewType );
    }

    @Override
    public DataFormat getDataFormat() {
        return dataFormat;
    }

    private static DataFormat createFormat(ManagedViewType<?> viewType) {
        //noinspection unchecked
        Set<MethodAttribute<?, ?>> attributes = (Set<MethodAttribute<?, ?>>) viewType.getAttributes();
        List<DataFormatField> fields = new ArrayList<>( attributes.size() );
        for ( MethodAttribute<?, ?> attribute : attributes ) {
            fields.add( createField( attribute ) );
        }
        return DataFormat.of( viewType.getJavaType(), fields );
    }

    private static DataFormatField createField(MethodAttribute<?, ?> attribute) {
        DataFormat format;
        if ( attribute.isCollection() ) {
            MethodPluralAttribute<?, ?, ?> pluralAttribute = (MethodPluralAttribute<?, ?, ?>) attribute;
            Class<?> javaType = pluralAttribute.getJavaType();
            DataFormat elementFormat = createFormat( pluralAttribute.getElementType() );
            if (pluralAttribute instanceof MethodMapAttribute<?, ?, ?> ) {
                MethodMapAttribute<?, ?, ?> mapAttribute = (MethodMapAttribute<?, ?, ?>) pluralAttribute;
                format = MapDataFormat.of( javaType, createFormat( mapAttribute.getKeyType() ), elementFormat );
            } else {
                format = CollectionDataFormat.of( javaType, elementFormat );
            }
        } else {
            MethodSingularAttribute<?, ?> singularAttribute = (MethodSingularAttribute<?, ?>) attribute;
            format = createFormat( singularAttribute.getType() );
        }
        return DataFormatField.of(
                attribute.getName(),
                new MethodFieldAccessor(attribute.getJavaMethod()),
                format
        );
    }

    private static DataFormat createFormat(Type<?> type) {
        return type.getMappingType() == Type.MappingType.BASIC
                ? DataFormat.of( type.getJavaType(), Collections.emptyList() )
                : createFormat( (ManagedViewType<?>) type );
    }

    @Override
    public List<EntityView> fetch(DataFetchContext context) throws DataFetcherException {
        return fetch( context, null );
    }

    @Override
    public List<EntityView> fetch(DataFetchContext context, int[][] projects) throws DataFetcherException {
        try {
            EntityManager entityManager = EntityViewConnectorConfig.ENTITY_MANAGER.find( context );
            if (entityManager == null) {
                entityManager = entityManagerSupplier.get();
            }
            CriteriaBuilder<Object> cb = evm.getService( CriteriaBuilderFactory.class ).create( entityManager, Object.class );
            cb.from( viewType.getEntityClass(), "e" );
            EntityViewSetting<EntityView, CriteriaBuilder<EntityView>> setting = EntityViewSetting.create( viewType.getJavaType() );
            if ( projects != null ) {
                for ( int[] fields : projects ) {
                    SubGraph<?> subGraph = setting;
                    DataFormat format = dataFormat;
                    for ( int field : fields ) {
                        if ( format instanceof CollectionDataFormat) {
                            format = ( (CollectionDataFormat) format ).getElementFormat();
                        } else if ( format instanceof MapDataFormat) {
                            format = ( (MapDataFormat) format ).getElementFormat();
                        }
                        DataFormatField dataFormatField = format.getFields().get( field );
                        subGraph = subGraph.fetch( dataFormatField.getName() );
                        format = dataFormatField.getFormat();
                    }
                }
            }
            return evm.applySetting( setting, cb ).getResultList();
        } catch (Exception e) {
            throw new DataFetcherException(e);
        }
    }

}
