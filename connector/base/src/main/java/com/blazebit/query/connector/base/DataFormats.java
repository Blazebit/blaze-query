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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.blazebit.query.spi.CollectionDataFormat;
import com.blazebit.query.spi.DataFormat;
import com.blazebit.query.spi.DataFormatField;
import com.blazebit.query.spi.DataFormatFieldAccessor;
import com.blazebit.query.spi.MapDataFormat;
import org.apache.calcite.sql.type.JavaToSqlTypeConversionRules;

/**
 * An enumerator for producing converted objects.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class DataFormats {
    private DataFormats() {
    }

    /**
     * Creates a data format for the given class by using beans convention.
     *
     * @param clazz The class
     * @return The data format
     */
    public static DataFormat beansConvention(Class<?> clazz) {
        return DataFormat.of( clazz, beansConventionFields( clazz ) );
    }

    /**
     * Creates data format fields for the given class by using beans convention.
     *
     * @param clazz The class
     * @return The data format fields
     */
    public static List<DataFormatField> beansConventionFields(Class<?> clazz) {
        TreeMap<String, Method> attributes = getAttributes( clazz );
        List<DataFormatField> fields = new ArrayList<>(attributes.size());
        for ( Map.Entry<String, Method> entry : attributes.entrySet() ) {
            fields.add( DataFormatField.of( entry.getKey(), methodAccessor( entry.getValue() ), beansConvention( entry.getValue().getGenericReturnType() ) )  );
        }
        return fields;
    }

    private static DataFormat beansConvention(Type type) {
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
                return CollectionDataFormat.of( type, beansConvention( elementType ) );
            } else if ( Map.class.isAssignableFrom( rawTypeClass )) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type keyType = actualTypeArguments[0];
                Type elementType = actualTypeArguments[actualTypeArguments.length - 1];
                return MapDataFormat.of( type, beansConvention( keyType ), beansConvention( elementType ) );
            }
            type = rawTypeClass;
        }
        if (!(type instanceof Class)) {
            throw new UnsupportedOperationException("Unsupported type: " + type);
        }
        Class<?> typeClass = (Class<?>) type;

        if (isBaseType(typeClass)) {
            return DataFormat.of( type, Collections.emptyList() );
        }
        return beansConvention( typeClass );
    }

    private static boolean isBaseType(Class<?> typeClass) {
        return typeClass.isEnum()
                || JavaToSqlTypeConversionRules.instance().lookup( typeClass ) != null
                || typeClass == Object.class
                || typeClass == OffsetDateTime.class
                ;
    }

    private static DataFormatFieldAccessor methodAccessor(Method method) {
        return new MethodFieldAccessor( method );
    }

    private static DataFormatFieldAccessor fieldAccessor(Field field) {
        return new FieldFieldAccessor( field );
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

}
