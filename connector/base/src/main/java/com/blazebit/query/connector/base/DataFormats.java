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
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.blazebit.query.spi.CollectionDataFormat;
import com.blazebit.query.spi.DataFormat;
import com.blazebit.query.spi.DataFormatField;
import com.blazebit.query.spi.DataFormatFieldAccessor;
import com.blazebit.query.spi.MapDataFormat;

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
        return beansConvention(clazz, ConventionContext.NO_FILTER);
    }

    /**
     * Creates a data format for the given class by using beans convention,
     * only considering methods for which the given method filter predicate returns {@code true}.
     *
     * @param clazz The class
     * @param conventionContext Filter for excluding methods from bean fields
     * @return The data format
     */
    public static DataFormat beansConvention(Class<?> clazz, ConventionContext conventionContext) {
        return DataFormat.of(clazz, beansConventionFields(clazz, conventionContext));
    }

    /**
     * Creates data format fields for the given class by using beans convention,
     * only considering methods for which the given method filter predicate returns {@code true}.
     *
     * @param clazz The class
     * @param conventionContext Filter for excluding methods from the fields
     * @return The data format fields
     */
    public static List<DataFormatField> beansConventionFields(Class<?> clazz, ConventionContext conventionContext) {
        Map<Class<?>, ConventionContext> visitedTypes = new HashMap<>();
        visitedTypes.put(clazz, conventionContext);
        return beansConventionFields( clazz, conventionContext, visitedTypes, new HashMap<>());
    }

    /**
     * Creates a data format for the given class by using fields via method convention.
     *
     * @param clazz The class
     * @return The data format
     */
    public static DataFormat fieldsViaMethodConvention(Class<?> clazz) {
        return DataFormat.of(clazz, fieldsViaMethodConventionFields(clazz, ConventionContext.NO_FILTER));
    }

    /**
     * Creates a data format for the given class by using fields via method convention,
     * only considering methods for which the given method filter predicate returns {@code true}.
     *
     * @param clazz The class
     * @param conventionContext Filter for excluding methods from bean fields
     * @return The data format
     */
    public static DataFormat fieldsViaMethodConvention(Class<?> clazz, ConventionContext conventionContext) {
        return DataFormat.of(clazz, fieldsViaMethodConventionFields(clazz, conventionContext));
    }

    /**
     * Creates data format fields for the given class by using fields via method convention,
     * only considering methods for which the given method filter predicate returns {@code true}.
     *
     * @param clazz The class
     * @param conventionContext Filter for excluding methods from the fields
     * @return The data format fields
     */
    public static List<DataFormatField> fieldsViaMethodConventionFields(Class<?> clazz, ConventionContext conventionContext) {
        Map<Class<?>, ConventionContext> visitedTypes = new HashMap<>();
        visitedTypes.put( clazz, conventionContext );
        return fieldsViaMethodConventionFields(clazz, conventionContext, visitedTypes, new HashMap<>());
    }

    /**
     * Creates a data format for the given class by using fields via method convention.
     *
     * @param clazz The class
     * @return The data format
     */
    public static DataFormat componentMethodConvention(Class<?> clazz) {
        return DataFormat.of(clazz, componentMethodConventionFields(clazz, ConventionContext.NO_FILTER));
    }

    /**
     * Creates a data format for the given class by using fields via method convention,
     * only considering methods for which the given method filter predicate returns {@code true}.
     *
     * @param clazz The class
     * @param conventionContext Filter for excluding methods from bean fields
     * @return The data format
     */
    public static DataFormat componentMethodConvention(Class<?> clazz, ConventionContext conventionContext) {
        return DataFormat.of(clazz, componentMethodConventionFields(clazz, conventionContext));
    }

    /**
     * Creates data format fields for the given class by using fields via method convention,
     * only considering methods for which the given method filter predicate returns {@code true}.
     *
     * @param clazz The class
     * @param conventionContext Filter for excluding methods from the fields
     * @return The data format fields
     */
    public static List<DataFormatField> componentMethodConventionFields(Class<?> clazz, ConventionContext conventionContext) {
        Map<Class<?>, ConventionContext> visitedTypes = new HashMap<>();
        visitedTypes.put( clazz, conventionContext );
        return componentMethodConventionFields(clazz, conventionContext, visitedTypes, new HashMap<>());
    }

    private static List<DataFormatField> beansConventionFields(Class<?> clazz, ConventionContext conventionContext, Map<Class<?>, ConventionContext> visitedTypes, Map<Class<?>, DataFormat> registry) {
        TreeMap<String, Method> attributes = getAttributesBeansConvention( clazz );
        List<DataFormatField> fields = new ArrayList<>(attributes.size());
        for (Map.Entry<String, Method> entry : attributes.entrySet()) {
            ConventionContext subFilter = conventionContext.getSubFilter(clazz , entry.getValue());
            if (subFilter != null) {
                DataFormatField dataFormatField = DataFormatField.of(
                        entry.getKey(),
                        methodAccessor(entry.getValue()),
                        beansConvention(entry.getValue().getGenericReturnType(), subFilter, visitedTypes, registry)
                );
                fields.add(dataFormatField);
            }
        }
        return fields;
    }

    private static DataFormat beansConvention(Type type, ConventionContext conventionContext, Map<Class<?>, ConventionContext> visitedTypes, Map<Class<?>, DataFormat> registry) {
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length == 0) {
                Type[] lowerBounds = wildcardType.getLowerBounds();
                if (lowerBounds.length == 0) {
                    return DataFormat.of(Object.class, Collections.emptyList());
                }
                type = lowerBounds[0];
            } else {
                type = upperBounds[0];
            }
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class)) {
                throw new UnsupportedOperationException("Unsupported type: " + rawType);
            }
            Class<?> rawTypeClass = (Class<?>) rawType;
            if (Collection.class.isAssignableFrom(rawTypeClass)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type elementType = actualTypeArguments[actualTypeArguments.length - 1];
                return CollectionDataFormat.of(type, beansConvention(elementType, conventionContext, visitedTypes, registry));
            } else if (Map.class.isAssignableFrom(rawTypeClass)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type keyType = actualTypeArguments[0];
                Type elementType = actualTypeArguments[actualTypeArguments.length - 1];
                return MapDataFormat.of( type, beansConvention(keyType, conventionContext, visitedTypes, registry), beansConvention(elementType, conventionContext, visitedTypes, registry));
            }
            type = rawTypeClass;
        }
        if (!(type instanceof Class)) {
            throw new UnsupportedOperationException("Unsupported type: " + type);
        }
        Class<?> typeClass = (Class<?>) type;
        DataFormat existingFormat = registry.get(typeClass);
        if (existingFormat != null) {
            return existingFormat;
        }

        if (conventionContext.isBaseType(typeClass)) {
            DataFormat format = DataFormat.of(type, Collections.emptyList());
            registry.put(typeClass, format);
            return format;
        }
        ConventionContext oldConventionContext = visitedTypes.put(typeClass, conventionContext);
        if (oldConventionContext != null && conventionContext == oldConventionContext) {
            throw new IllegalArgumentException("Detected cyclic model via class: " + typeClass.getTypeName());
        }
        DataFormat format = DataFormat.of(typeClass, beansConventionFields(typeClass, conventionContext, visitedTypes, registry));
        visitedTypes.remove(typeClass);
        registry.put(typeClass, format);
        return format;
    }

    private static List<DataFormatField> fieldsViaMethodConventionFields(Class<?> clazz, ConventionContext conventionContext, Map<Class<?>, ConventionContext> visitedTypes, Map<Class<?>, DataFormat> registry) {
        TreeMap<String, Method> attributes = getAttributesFieldsViaMethodConvention(clazz );
        List<DataFormatField> fields = new ArrayList<>(attributes.size());
        for (Map.Entry<String, Method> entry : attributes.entrySet()) {
            ConventionContext subFilter = conventionContext.getSubFilter(clazz, entry.getValue());
            if (subFilter != null) {
                DataFormatField dataFormatField = DataFormatField.of(
                        entry.getKey(),
                        methodAccessor(entry.getValue()),
                        fieldsViaMethodConvention(entry.getValue().getGenericReturnType(), subFilter, visitedTypes, registry)
                );
                fields.add(dataFormatField);
            }
        }
        return fields;
    }

    private static DataFormat fieldsViaMethodConvention(Type type, ConventionContext conventionContext, Map<Class<?>, ConventionContext> visitedTypes, Map<Class<?>, DataFormat> registry) {
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length == 0) {
                Type[] lowerBounds = wildcardType.getLowerBounds();
                if (lowerBounds.length == 0) {
                    return DataFormat.of(Object.class, Collections.emptyList());
                }
                type = lowerBounds[0];
            } else {
                type = upperBounds[0];
            }
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class)) {
                throw new UnsupportedOperationException("Unsupported type: " + rawType);
            }
            Class<?> rawTypeClass = (Class<?>) rawType;
            if (Collection.class.isAssignableFrom(rawTypeClass)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type elementType = actualTypeArguments[actualTypeArguments.length - 1];
                return CollectionDataFormat.of(type, fieldsViaMethodConvention(elementType, conventionContext, visitedTypes, registry));
            } else if (Map.class.isAssignableFrom(rawTypeClass)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type keyType = actualTypeArguments[0];
                Type elementType = actualTypeArguments[actualTypeArguments.length - 1];
                return MapDataFormat.of( type, fieldsViaMethodConvention( keyType, conventionContext, visitedTypes, registry), fieldsViaMethodConvention( elementType, conventionContext, visitedTypes, registry));
            }
            type = rawTypeClass;
        }
        if (!(type instanceof Class)) {
            throw new UnsupportedOperationException("Unsupported type: " + type);
        }
        Class<?> typeClass = (Class<?>) type;
        DataFormat existingFormat = registry.get(typeClass);
        if (existingFormat != null) {
            return existingFormat;
        }

        if (conventionContext.isBaseType(typeClass)) {
            DataFormat format = DataFormat.of(type, Collections.emptyList());
            registry.put(typeClass, format);
            return format;
        }
        ConventionContext oldConventionContext = visitedTypes.put( typeClass, conventionContext );
        if (oldConventionContext != null && conventionContext == oldConventionContext) {
            throw new IllegalArgumentException("Detected cyclic model via class: " + typeClass.getTypeName());
        }
        DataFormat format = DataFormat.of(typeClass, fieldsViaMethodConventionFields(typeClass, conventionContext, visitedTypes, registry));
        visitedTypes.remove(typeClass);
        registry.put(typeClass, format);
        return format;
    }

    private static List<DataFormatField> componentMethodConventionFields(Class<?> clazz, ConventionContext conventionContext, Map<Class<?>, ConventionContext> visitedTypes, Map<Class<?>, DataFormat> registry) {
        TreeMap<String, Method> attributes = getAttributesComponentMethodConvention(clazz);
        List<DataFormatField> fields = new ArrayList<>(attributes.size());
        for (Map.Entry<String, Method> entry : attributes.entrySet()) {
            ConventionContext subFilter = conventionContext.getSubFilter(clazz , entry.getValue());
            if (subFilter != null) {
                DataFormatField dataFormatField = DataFormatField.of(
                        entry.getKey(),
                        methodAccessor(entry.getValue()),
                        componentMethodConvention(entry.getValue().getGenericReturnType(), subFilter, visitedTypes, registry)
                );
                fields.add(dataFormatField);
            }
        }
        return fields;
    }

    private static DataFormat componentMethodConvention(Type type, ConventionContext conventionContext, Map<Class<?>, ConventionContext> visitedTypes, Map<Class<?>, DataFormat> registry) {
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length == 0) {
                Type[] lowerBounds = wildcardType.getLowerBounds();
                if (lowerBounds.length == 0) {
                    return DataFormat.of(Object.class, Collections.emptyList());
                }
                type = lowerBounds[0];
            } else {
                type = upperBounds[0];
            }
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class)) {
                throw new UnsupportedOperationException("Unsupported type: " + rawType);
            }
            Class<?> rawTypeClass = (Class<?>) rawType;
            if (Collection.class.isAssignableFrom(rawTypeClass)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type elementType = actualTypeArguments[actualTypeArguments.length - 1];
                return CollectionDataFormat.of(type, componentMethodConvention(elementType, conventionContext, visitedTypes, registry));
            } else if (Map.class.isAssignableFrom(rawTypeClass)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type keyType = actualTypeArguments[0];
                Type elementType = actualTypeArguments[actualTypeArguments.length - 1];
                return MapDataFormat.of( type, componentMethodConvention(keyType, conventionContext, visitedTypes, registry), componentMethodConvention(elementType, conventionContext, visitedTypes, registry));
            }
            type = rawTypeClass;
        }
        if (!(type instanceof Class)) {
            throw new UnsupportedOperationException("Unsupported type: " + type);
        }
        Class<?> typeClass = (Class<?>) type;
        DataFormat existingFormat = registry.get(typeClass);
        if (existingFormat != null) {
            return existingFormat;
        }

        if (conventionContext.isBaseType(typeClass)) {
            DataFormat format = DataFormat.of(type, Collections.emptyList());
            registry.put(typeClass, format);
            return format;
        }
        ConventionContext oldConventionContext = visitedTypes.put(typeClass, conventionContext);
        if (oldConventionContext != null && conventionContext == oldConventionContext) {
            throw new IllegalArgumentException("Detected cyclic model via class: " + typeClass.getTypeName());
        }
        DataFormat format = DataFormat.of(typeClass, componentMethodConventionFields(typeClass, conventionContext, visitedTypes, registry));
        visitedTypes.remove(typeClass);
        registry.put(typeClass, format);
        return format;
    }

    private static DataFormatFieldAccessor methodAccessor(Method method) {
        return new MethodFieldAccessor(method);
    }

    private static DataFormatFieldAccessor fieldAccessor(Field field) {
        return new FieldFieldAccessor(field);
    }

    private static TreeMap<String, Method> getAttributesBeansConvention(Class<?> clazz) {
        TreeMap<String, Method> attributeMap = new TreeMap<>();
        visitAttributes(attributeMap, clazz);
        return attributeMap;
    }

    private static TreeMap<String, Method> getAttributesFieldsViaMethodConvention(Class<?> clazz) {
        TreeMap<String, Method> attributeMap = new TreeMap<>();
        do {
            for (Field field : clazz.getDeclaredFields()) {
                if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    Method method;
                    try {
                        method = clazz.getDeclaredMethod(field.getName());
                    } catch (NoSuchMethodException e) {
                        try {
                            method = clazz.getDeclaredMethod("get" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1));
                        } catch (NoSuchMethodException e2) {
                            throw new RuntimeException("Couldn't find method for field: " + field.getName(), e);
                        }
                    }
                    attributeMap.putIfAbsent(field.getName(), method);
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null && clazz != Object.class);
        return attributeMap;
    }

    private static TreeMap<String, Method> getAttributesComponentMethodConvention(Class<?> clazz) {
        TreeMap<String, Method> attributeMap = new TreeMap<>();
        do {
            for (Method method : clazz.getDeclaredMethods()) {
                if ( isAccessor( method ) ) {
                    String attributeName = getAttributeName(method);
                    if (attributeName != null) {
                        attributeMap.putIfAbsent(attributeName, method);
                    } else {
                        attributeMap.putIfAbsent(method.getName(), method);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null && clazz != Object.class);
        return attributeMap;
    }

    private static boolean isAccessor(Method method) {
        return !method.isSynthetic()
                && !method.isBridge()
                && Modifier.isPublic(method.getModifiers())
                && !Modifier.isStatic(method.getModifiers())
                && method.getReturnType() != void.class
                && method.getParameterCount() == 0;
    }

    private static void visitAttributes(TreeMap<String, Method> attributeMap, Class<?> clazz) {
        visitClassAttributes(attributeMap, clazz);
        visitInterfaceAttributes(attributeMap, clazz);
    }

    private static void visitClassAttributes(TreeMap<String, Method> attributeMap, Class<?> clazz) {
        do {
            visitDeclaredAttributes(attributeMap, clazz);
            clazz = clazz.getSuperclass();
        } while (clazz != null && clazz != Object.class);
    }

    private static void visitInterfaceAttributes(TreeMap<String, Method> attributeMap, Class<?> clazz) {
        for (Class<?> interfaceClazz : clazz.getInterfaces()) {
            visitDeclaredAttributes(attributeMap, interfaceClazz);
        }
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            visitInterfaceAttributes(attributeMap, clazz.getSuperclass());
        }
        for (Class<?> interfaceClazz : clazz.getInterfaces()) {
            visitInterfaceAttributes(attributeMap, interfaceClazz);
        }
    }

    private static void visitDeclaredAttributes(TreeMap<String, Method> attributeMap, Class<?> clazz) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (isAccessor(method)) {
                String attributeName = getAttributeName(method);
                if (attributeName != null) {
                    attributeMap.putIfAbsent(attributeName, method);
                }
            }
        }
    }

    private static String getAttributeName(Method method) {
        if (method.getParameterCount() == 0 && method.getReturnType() != void.class) {
            String methodName = method.getName();
            if (methodName.startsWith("get") && methodName.length() > 3) {
                return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
            } else if (methodName.startsWith("is") && methodName.length() > 2) {
                return Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
            }
        }
        return null;
    }

}
