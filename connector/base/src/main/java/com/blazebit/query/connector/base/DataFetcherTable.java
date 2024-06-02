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

package com.blazebit.query.connector.base;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import com.blazebit.query.QuerySession;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.JavaToSqlTypeConversionRules;
import org.apache.calcite.util.Pair;

/**
 * {@link ScannableTable} implementation based on a {@link DataFetcher}.
 *
 * @param <T> The native data type
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DataFetcherTable<T> extends AbstractTable implements ScannableTable {

    private final Class<T> tableClass;
    private final DataFetcher<T> dataFetcher;
    private final Supplier<DataFetchContext> dataContextSupplier;
    private final Accessor[] accessors;
    private RelDataType rowType;

    /**
     * Creates new table.
     *
     * @param tableClass The table class
     * @param dataFetcher The data fetcher
     * @param dataFetchContextSupplier The data fetch context supplier
     */
    public DataFetcherTable(Class<T> tableClass, DataFetcher<T> dataFetcher, Supplier<DataFetchContext> dataFetchContextSupplier) {
        this.tableClass = tableClass;
        this.dataFetcher = dataFetcher;
        this.accessors = createAccessors( null, tableClass );
        this.dataContextSupplier = dataFetchContextSupplier;
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        if (rowType == null) {
            rowType = deduceRowType( (JavaTypeFactory) typeFactory, null, accessors);
        }
        return rowType;
    }

    private static RelDataType deduceRowType(JavaTypeFactory typeFactory, String basePath, Accessor[] accessors) {
        final List<RelDataType> types = new ArrayList<>( accessors.length);
        final List<String> names = new ArrayList<>(accessors.length);
        for ( Accessor accessor : accessors ) {
            final String name;
            if (basePath == null) {
                name = accessor.getAttributePath();
            } else {
                name = accessor.getAttributePath().substring(basePath.length() + 1);
            }
            final RelDataType fieldType = deduceType(typeFactory, accessor.getAttributePath(), accessor);
            names.add(name);
            types.add(fieldType);
        }
        return typeFactory.createStructType( Pair.zip( names, types));
    }

    private static RelDataType deduceType(JavaTypeFactory typeFactory, String basePath, Accessor accessor) {
        if ( accessor instanceof MapAccessor ) {
            MapAccessor mapAccessor = (MapAccessor) accessor;
            RelDataType keyRelDataType = deduceType( typeFactory, accessor.getAttributePath(), mapAccessor.getKeyAccessor() );
            if (keyRelDataType == null) {
                MethodAccessor baseAccessor = (MethodAccessor) mapAccessor.getBaseAccessor();
                ParameterizedType parameterizedType = (ParameterizedType) baseAccessor.getMethod().getGenericReturnType();
                keyRelDataType = typeFactory.createType( parameterizedType.getActualTypeArguments()[0] );
            }
            RelDataType elementRelDataType = deduceType( typeFactory, accessor.getAttributePath(), mapAccessor.getElementAccessor() );
            if (elementRelDataType == null) {
                MethodAccessor baseAccessor = (MethodAccessor) mapAccessor.getBaseAccessor();
                ParameterizedType parameterizedType = (ParameterizedType) baseAccessor.getMethod().getGenericReturnType();
                elementRelDataType = typeFactory.createType( parameterizedType.getActualTypeArguments()[1] );
            }
            return typeFactory.createTypeWithNullability( typeFactory.createMapType( keyRelDataType, elementRelDataType ), true );
        } else if (accessor instanceof CollectionAccessor) {
            CollectionAccessor collectionAccessor = (CollectionAccessor) accessor;
            RelDataType elementRelDataType = deduceType( typeFactory, accessor.getAttributePath(), collectionAccessor.getElementAccessor() );
            if (elementRelDataType == null) {
                MethodAccessor baseAccessor = (MethodAccessor) collectionAccessor.getBaseAccessor();
                ParameterizedType parameterizedType = (ParameterizedType) baseAccessor.getMethod().getGenericReturnType();
                elementRelDataType = typeFactory.createType( parameterizedType.getActualTypeArguments()[0] );
            }
            return typeFactory.createTypeWithNullability( typeFactory.createArrayType( elementRelDataType, -1L ), true );
        } else if (accessor instanceof ObjectArrayAccessor) {
            ObjectArrayAccessor objectArrayAccessor = (ObjectArrayAccessor) accessor;
            return typeFactory.createTypeWithNullability( deduceRowType( typeFactory, accessor.getAttributePath (), objectArrayAccessor.getAccessors() ), true );
        } else if (accessor != null) {
            MethodAccessor methodAccessor = (MethodAccessor) accessor;
            return typeFactory.createType( methodAccessor.getMethod().getReturnType() );
        } else {
            return null;
        }
    }

    private static TreeMap<String, Method> getAttributes(Class<?> clazz) {
        TreeMap<String, Method> attributeMap = new TreeMap<>();
        visitAttributes( attributeMap, clazz );
        return attributeMap;
    }

    private static void visitAttributes(TreeMap<String, Method> attributeMap, Class<?> clazz) {
        visitClassAttributes(attributeMap, clazz);
        visitInterfaceAttributes(attributeMap, clazz);
    }

    private static void visitClassAttributes(TreeMap<String, Method> attributeMap, Class<?> clazz) {
        do {
            visitDeclaredAttributes( attributeMap, clazz );
            clazz = clazz.getSuperclass();
        } while ( clazz != null && clazz != Object.class );
    }

    private static void visitInterfaceAttributes(TreeMap<String, Method> attributeMap, Class<?> clazz) {
        for ( Class<?> interfaceClazz : clazz.getInterfaces() ) {
            visitDeclaredAttributes(attributeMap, interfaceClazz);
        }
        if ( clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class ) {
            visitInterfaceAttributes(attributeMap, clazz.getSuperclass());
        }
        for ( Class<?> interfaceClazz : clazz.getInterfaces() ) {
            visitInterfaceAttributes(attributeMap, interfaceClazz);
        }
    }

    private static void visitDeclaredAttributes(TreeMap<String, Method> attributeMap, Class<?> clazz) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for ( Method method : declaredMethods ) {
            if ( !method.isSynthetic() && !method.isBridge() && Modifier.isPublic( method.getModifiers() ) ) {
                String attributeName = getAttributeName( method );
                if ( attributeName != null ) {
                    attributeMap.putIfAbsent( attributeName, method );
                }
            }
        }
    }

    private static String getAttributeName(Method method) {
        if (method.getParameterCount() == 0
                && method.getReturnType() != void.class) {
            String methodName = method.getName();
            if ( methodName.startsWith( "get" ) && methodName.length() > 3 ) {
                return Character.toLowerCase( methodName.charAt( 3 ) ) + methodName.substring( 4 );
            } else if ( methodName.startsWith( "is" ) && methodName.length() > 2 ) {
                return Character.toLowerCase( methodName.charAt( 2 ) ) + methodName.substring( 3 );
            }
        }
        return null;
    }

    private static Accessor[] createAccessors(String basePath, Class<?> clazz) {
        TreeMap<String, Method> attributes = getAttributes( clazz );
        Accessor[] accessors = new Accessor[attributes.size()];
        int i = 0;
        for ( Map.Entry<String, Method> entry : attributes.entrySet() ) {
            MethodAccessor baseAccessor = createMethodAccessor( basePath, entry.getKey(), entry.getValue() );
            accessors[i++] = createAccessor( baseAccessor, baseAccessor.getAttributePath(), entry.getValue().getGenericReturnType() );
        }
        return accessors;
    }

    private static Accessor createAccessor(MethodAccessor baseAccessor, String basePath, Type type) {
        if (type instanceof ParameterizedType ) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class)) {
                throw new UnsupportedOperationException("Unsupported type: " + rawType);
            }
            Class<?> rawTypeClass = (Class<?>) rawType;
            if ( Collection.class.isAssignableFrom( rawTypeClass )) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type elementType = actualTypeArguments[actualTypeArguments.length - 1];
                Accessor elementAccessor = createAccessor( null, basePath, elementType );
                return new CollectionAccessor( baseAccessor, elementAccessor );
            } else if ( Map.class.isAssignableFrom( rawTypeClass )) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type keyType = actualTypeArguments[actualTypeArguments.length - 1];
                Accessor keyAccessor = createAccessor( null, basePath, keyType );
                Type elementType = actualTypeArguments[actualTypeArguments.length - 1];
                Accessor elementAccessor = createAccessor( null, basePath, elementType );
                return new MapAccessor( baseAccessor, keyAccessor, elementAccessor );
            }
            type = rawTypeClass;
        }
        if (!(type instanceof Class)) {
            throw new UnsupportedOperationException("Unsupported type: " + type);
        }
        Class<?> typeClass = (Class<?>) type;

        if (isBaseType(typeClass)) {
            return baseAccessor;
        }
        return new ObjectArrayAccessor( baseAccessor, createAccessors( basePath, typeClass ) );
    }

    private static MethodAccessor createMethodAccessor(String basePath, String attributeName, Method method) {
        String attributePath;
        if ( basePath == null ) {
            attributePath = attributeName;
        } else {
            attributePath = basePath + "." + attributeName;
        }
        return new MethodAccessor( attributePath, method );
    }

    private static boolean isBaseType(Class<?> typeClass) {
        return typeClass.isEnum()
                || JavaToSqlTypeConversionRules.instance().lookup( typeClass ) != null
                || typeClass == Object.class
                || typeClass == OffsetDateTime.class
                ;
    }

    @Override
    public Enumerable<Object[]> scan(DataContext root) {
        final DataFetchContext dataFetchContext = dataContextSupplier.get();
        return new AbstractEnumerable<>() {
            @Override
            public Enumerator<Object[]> enumerator() {
                QuerySession session = dataFetchContext.getSession();
                List<? extends T> objects = session.get( tableClass );
                if ( objects == null ) {
                    objects = dataFetcher.fetch( dataFetchContext );
                    session.put( tableClass, objects );
                }
                return new AccessorListEnumerator<>( objects, AccessorListEnumerator.arrayConverter( accessors, null ));
            }
        };
    }
}
