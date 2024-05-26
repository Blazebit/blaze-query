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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.blazebit.persistence.view.metamodel.BasicType;
import com.blazebit.persistence.view.metamodel.ManagedViewType;
import com.blazebit.persistence.view.metamodel.MethodAttribute;
import com.blazebit.persistence.view.metamodel.MethodMapAttribute;
import com.blazebit.persistence.view.metamodel.MethodPluralAttribute;
import com.blazebit.persistence.view.metamodel.MethodSingularAttribute;
import com.blazebit.persistence.view.metamodel.Type;
import com.blazebit.persistence.view.metamodel.ViewType;
import com.blazebit.query.connector.base.Accessor;
import com.blazebit.query.connector.base.CollectionAccessor;
import com.blazebit.query.connector.base.MapAccessor;
import com.blazebit.query.connector.base.MethodAccessor;
import com.blazebit.query.connector.base.ObjectArrayAccessor;
import com.blazebit.query.metamodel.SchemaObjectType;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import jakarta.persistence.EntityManager;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.util.Pair;

public abstract class EntityViewTable<EntityView> extends AbstractTable implements SchemaObjectType<EntityView>, DataFetcher<EntityView> {
    protected final EntityViewManager evm;
    protected final Supplier<EntityManager> entityManagerSupplier;
    protected final ViewType<EntityView> viewType;
    protected final Accessor[] accessors;
    private RelDataType rowType;

    public EntityViewTable(EntityViewManager evm, Supplier<EntityManager> entityManagerSupplier, ViewType<EntityView> viewType) {
        this.evm = evm;
        this.entityManagerSupplier = entityManagerSupplier;
        this.viewType = viewType;
        this.accessors = createAccessors( null, viewType );
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        if (rowType == null) {
            rowType = deduceRowType( (JavaTypeFactory) typeFactory, viewType);
        }
        return rowType;
    }

    @Override
    public Class<EntityView> getType() {
        return viewType.getJavaType();
    }

    @Override
    public DataFetcher<EntityView> getDataFetcher() {
        return this;
    }

    private RelDataType deduceRowType(JavaTypeFactory typeFactory, ManagedViewType<?> viewType) {
		//noinspection unchecked
		Set<MethodAttribute<?, ?>> attributes = (Set<MethodAttribute<?, ?>>) viewType.getAttributes();
        final List<RelDataType> types = new ArrayList<>(attributes.size());
        final List<String> names = new ArrayList<>(attributes.size());
        for ( MethodAttribute<?, ?> attribute : attributes ) {
            final String name = attribute.getName();
            final RelDataType fieldType = deduceType(typeFactory, attribute);
            names.add(name);
            types.add(fieldType);
        }
        return typeFactory.createStructType( Pair.zip( names, types));

//        final List<RelDataTypeField> list = new ArrayList<>(attributes.size());
//        for ( MethodAttribute<?, ?> attribute : attributes ) {
//            final RelDataType fieldType = deduceType(typeFactory, attribute);
//            list.add( new RelDataTypeFieldImpl( attribute.getName(), list.size(), fieldType));
//        }
//        return new JavaRecordType(list, viewType.getJavaType());
    }

    private RelDataType deduceType(JavaTypeFactory typeFactory, MethodAttribute<?, ?> attribute) {
        if ( attribute.isCollection() ) {
            MethodPluralAttribute<?, ?, ?> pluralAttribute = (MethodPluralAttribute<?, ?, ?>) attribute;
            RelDataType elementRelDataType = deduceType( typeFactory, pluralAttribute.getElementType() );
            if (pluralAttribute instanceof MethodMapAttribute<?, ?, ?> ) {
                MethodMapAttribute<?, ?, ?> mapAttribute = (MethodMapAttribute<?, ?, ?>) pluralAttribute;
                RelDataType keyRelDataType = deduceType( typeFactory, mapAttribute.getKeyType() );
                return typeFactory.createTypeWithNullability( typeFactory.createMapType( keyRelDataType, elementRelDataType ), true );
            }
            return typeFactory.createTypeWithNullability( typeFactory.createArrayType( elementRelDataType, -1L ), true );
        } else {
            MethodSingularAttribute<?, ?> singularAttribute = (MethodSingularAttribute<?, ?>) attribute;
            return deduceType( typeFactory, singularAttribute.getType() );
        }
    }

    private RelDataType deduceType(JavaTypeFactory typeFactory, Type<?> type) {
        if ( type.getMappingType() != Type.MappingType.BASIC) {
//            return deduceRowType( typeFactory, (ManagedViewType<?>) type );
            return typeFactory.createTypeWithNullability( deduceRowType( typeFactory, (ManagedViewType<?>) type ), true );
        }
        BasicType<?> basicType = (BasicType<?>) type;
        return typeFactory.createType( basicType.getJavaType() );
    }

    private static Accessor[] createAccessors(String basePath, ManagedViewType<?> viewType) {
		//noinspection unchecked
		Set<MethodAttribute<?, ?>> attributes = (Set<MethodAttribute<?, ?>>) viewType.getAttributes();
        Accessor[] accessors = new Accessor[attributes.size()];
        int i = 0;
        for ( MethodAttribute<?, ?> attribute : attributes ) {
            accessors[i++] = createAccessor( basePath, attribute );
        }
        return accessors;
    }

    private static Accessor createAccessor(String basePath, MethodAttribute<?, ?> attribute) {
        if ( attribute.isCollection() ) {
            MethodPluralAttribute<?, ?, ?> pluralAttribute = (MethodPluralAttribute<?, ?, ?>) attribute;
            MethodAccessor baseAccessor = createMethodAccessor( basePath, attribute );
            Accessor elementAccessor = createAccessor( basePath, pluralAttribute.getElementType() );
            if (pluralAttribute instanceof MethodMapAttribute<?, ?, ?> ) {
                MethodMapAttribute<?, ?, ?> mapAttribute = (MethodMapAttribute<?, ?, ?>) pluralAttribute;
                Accessor keyAccessor = createAccessor( basePath, mapAttribute.getKeyType() );
                return new MapAccessor( baseAccessor, keyAccessor, elementAccessor );
            }
            return new CollectionAccessor( baseAccessor, elementAccessor );
        } else {
            return createAccessor( basePath, (MethodSingularAttribute<?, ?>) attribute );
        }
    }

    private static Accessor createAccessor(String basePath, MethodSingularAttribute<?, ?> singularAttribute) {
        MethodAccessor baseAccessor = createMethodAccessor( basePath, singularAttribute );
        Type<?> type = singularAttribute.getType();
        if ( type.getMappingType() != Type.MappingType.BASIC) {
            return new ObjectArrayAccessor( baseAccessor, createAccessors( basePath, (ManagedViewType<?>) type ) );
        }
        return baseAccessor;
    }

    private static Accessor createAccessor(String basePath, Type<?> type) {
        if ( type.getMappingType() != Type.MappingType.BASIC) {
            return new ObjectArrayAccessor( null, createAccessors( basePath, (ManagedViewType<?>) type ) );
        }
        return null;
    }

    private static MethodAccessor createMethodAccessor(String basePath, MethodAttribute<?, ?> methodAttribute) {
        String attributePath;
        if ( basePath == null ) {
            attributePath = methodAttribute.getName();
        } else {
            attributePath = basePath + "." + methodAttribute.getName();
        }
        return new MethodAccessor( attributePath, methodAttribute.getJavaMethod() );
    }

    @Override
    public List<EntityView> fetch(DataFetchContext context) {
        EntityManager entityManager = EntityViewConnectorConfig.ENTITY_MANAGER.find( context );
        if (entityManager == null) {
            entityManager = entityManagerSupplier.get();
        }
        CriteriaBuilder<Object> cb = evm.getService( CriteriaBuilderFactory.class ).create( entityManager, Object.class );
        cb.from( viewType.getEntityClass(), "e" );
        EntityViewSetting<EntityView, CriteriaBuilder<EntityView>> setting = EntityViewSetting.create( viewType.getJavaType() );
        return evm.applySetting( setting, cb ).getResultList();
    }

}
