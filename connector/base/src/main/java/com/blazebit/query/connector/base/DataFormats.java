/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.base;

import com.blazebit.query.spi.CollectionDataFormat;
import com.blazebit.query.spi.DataFormat;
import com.blazebit.query.spi.DataFormatField;
import com.blazebit.query.spi.DataFormatFieldAccessor;
import com.blazebit.query.spi.MapDataFormat;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
		return beansConvention( clazz, ConventionContext.NO_FILTER );
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
		return DataFormat.of( clazz, beansConventionFields( clazz, conventionContext ) );
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
		visitedTypes.put( clazz, conventionContext );
		return createFields( clazz, conventionContext, visitedTypes, new HashMap<>(),
				BeansConventionDataFormatFactory.INSTANCE );
	}

	/**
	 * Creates a data format for the given class by using fields via method convention.
	 *
	 * @param clazz The class
	 * @return The data format
	 */
	public static DataFormat fieldsViaMethodConvention(Class<?> clazz) {
		return DataFormat.of( clazz, fieldsViaMethodConventionFields( clazz, ConventionContext.NO_FILTER ) );
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
		return DataFormat.of( clazz, fieldsViaMethodConventionFields( clazz, conventionContext ) );
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
		return createFields( clazz, conventionContext, visitedTypes, new HashMap<>(),
				FieldsViaMethodConventionDataFormatFactory.INSTANCE );
	}

	/**
	 * Creates a data format for the given class by using fields via method convention.
	 *
	 * @param clazz The class
	 * @return The data format
	 */
	public static DataFormat componentMethodConvention(Class<?> clazz) {
		return DataFormat.of( clazz, componentMethodConventionFields( clazz, ConventionContext.NO_FILTER ) );
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
		return DataFormat.of( clazz, componentMethodConventionFields( clazz, conventionContext ) );
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
		return createFields( clazz, conventionContext, visitedTypes, new HashMap<>(),
				ComponentMethodConventionDataFormatFactory.INSTANCE );
	}

	/**
	 * Creates a data format for the given class by using fields convention.
	 *
	 * @param clazz The class
	 * @return The data format
	 */
	public static DataFormat fieldsConvention(Class<?> clazz) {
		return fieldsConvention( clazz, ConventionContext.NO_FILTER );
	}

	/**
	 * Creates a data format for the given class by using fields convention,
	 * only considering methods for which the given method filter predicate returns {@code true}.
	 *
	 * @param clazz The class
	 * @param conventionContext Filter for excluding methods from bean fields
	 * @return The data format
	 */
	public static DataFormat fieldsConvention(Class<?> clazz, ConventionContext conventionContext) {
		return DataFormat.of( clazz, fieldsConventionFields( clazz, conventionContext ) );
	}

	/**
	 * Creates data format fields for the given class by using fields convention,
	 * only considering methods for which the given method filter predicate returns {@code true}.
	 *
	 * @param clazz The class
	 * @param conventionContext Filter for excluding fields
	 * @return The data format fields
	 */
	public static List<DataFormatField> fieldsConventionFields(Class<?> clazz, ConventionContext conventionContext) {
		Map<Class<?>, ConventionContext> visitedTypes = new HashMap<>();
		visitedTypes.put( clazz, conventionContext );
		return createFields( clazz, conventionContext, visitedTypes, new HashMap<>(),
				FieldsConventionDataFormatFactory.INSTANCE );
	}

	private static List<DataFormatField> createFields(Class<?> clazz, ConventionContext conventionContext, Map<Class<?>, ConventionContext> visitedTypes, Map<Class<?>, DataFormat> registry, DataFormatFactory factory) {
		TreeMap<String, ? extends Member> attributes = factory.getAttributes( clazz );
		List<DataFormatField> fields = new ArrayList<>( attributes.size() );
		for ( Map.Entry<String, ? extends Member> entry : attributes.entrySet() ) {
			ConventionContext subFilter = conventionContext.getSubFilter( clazz, entry.getValue() );
			if ( subFilter != null ) {
				Type memberType = factory.memberType( entry.getValue() );
				DataFormatFieldAccessor accessor = factory.memberAccessor( entry.getValue(), conventionContext );

				if ( memberType instanceof Class<?> memberClass ) {
					Class<?> resolved = subFilter.resolveType( memberClass );
					if ( resolved != memberClass ) {
						accessor = subFilter.createConvertingAccessor( accessor, memberClass, resolved );
						memberType = resolved;
					}
				}

				DataFormatField dataFormatField = DataFormatField.of(
						entry.getKey(),
						accessor,
						getOrCreateDataFormat( clazz, memberType, subFilter,
								visitedTypes, registry, factory )
				);
				fields.add( dataFormatField );
			}
		}
		return fields;
	}

	private static DataFormat getOrCreateDataFormat(Class<?> ownerType, Type type, ConventionContext conventionContext, Map<Class<?>, ConventionContext> visitedTypes, Map<Class<?>, DataFormat> registry, DataFormatFactory factory) {
		if ( type instanceof WildcardType ) {
			WildcardType wildcardType = (WildcardType) type;
			Type[] upperBounds = wildcardType.getUpperBounds();
			if ( upperBounds.length == 0 ) {
				Type[] lowerBounds = wildcardType.getLowerBounds();
				if ( lowerBounds.length == 0 ) {
					return DataFormat.of( Object.class, Collections.emptyList() );
				}
				type = lowerBounds[0];
			}
			else {
				type = upperBounds[0];
			}
		}
		if ( type instanceof TypeVariable<?> ) {
			type = resolveTypeVariable( ownerType, (TypeVariable<?>) type );
		}
		if ( type instanceof ParameterizedType ) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type rawType = parameterizedType.getRawType();
			if ( !(rawType instanceof Class) ) {
				throw new UnsupportedOperationException( "Unsupported type: " + rawType );
			}
			Class<?> rawTypeClass = (Class<?>) rawType;
			if ( Collection.class.isAssignableFrom( rawTypeClass ) ) {
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				Type elementType = actualTypeArguments[actualTypeArguments.length - 1];
				return CollectionDataFormat.of( type,
						getOrCreateDataFormat( ownerType, elementType, conventionContext, visitedTypes, registry, factory ) );
			}
			else if ( Map.class.isAssignableFrom( rawTypeClass ) ) {
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				Type keyType = actualTypeArguments[0];
				Type elementType = actualTypeArguments[actualTypeArguments.length - 1];
				return MapDataFormat.of( type,
						getOrCreateDataFormat( ownerType, keyType, conventionContext, visitedTypes, registry, factory ),
						getOrCreateDataFormat( ownerType, elementType, conventionContext, visitedTypes, registry, factory ) );
			}
			type = rawTypeClass;
		}
		if ( !(type instanceof Class) ) {
			throw new UnsupportedOperationException( "Unsupported type: " + type );
		}
		Class<?> typeClass = (Class<?>) type;
		DataFormat existingFormat = registry.get( typeClass );
		if ( existingFormat != null ) {
			return existingFormat;
		}

		if ( visitedTypes.containsKey( typeClass ) ) {
			if ( visitedTypes.get( typeClass ) == conventionContext ) {
				return DataFormat.of( typeClass, Collections.emptyList() );
			}
		}

		if ( conventionContext.isBaseType( typeClass ) ) {
			DataFormat format = createBaseTypeDataFormat( typeClass, conventionContext );
			registry.put( typeClass, format );
			return format;
		}

		ConventionContext oldConventionContext = visitedTypes.put( typeClass, conventionContext );
		try {
			DataFormat format = DataFormat.of( typeClass,
					createFields( typeClass, conventionContext, visitedTypes, registry, factory ) );
			visitedTypes.put( typeClass,
					oldConventionContext );  // Ensure that the visited types map is reverted back correctly
			registry.put( typeClass, format );
			return format;
		}
		catch (RuntimeException e) {
			visitedTypes.remove( typeClass );  // Clean up visited types map in case of exception
			throw e;
		}
	}

	private static Type resolveTypeVariable(Class<?> ownerType, TypeVariable<?> typeVariable) {
		Map<TypeVariable<?>, Type> typeAssignments = new HashMap<>();
		discoverTypeVariableAssignments( ownerType, typeAssignments );
		Set<TypeVariable<?>> visitedTypeVariables = new HashSet<>();
		TypeVariable<?> currentTypeVariable = typeVariable;
		while ( visitedTypeVariables.add( currentTypeVariable ) ) {
			Type type = typeAssignments.get( typeVariable );
			if ( type instanceof Class<?> || type instanceof ParameterizedType ) {
				return type;
			}
			if ( type instanceof TypeVariable<?> ) {
				currentTypeVariable = (TypeVariable<?>) type;
			}
			else {
				break;
			}
		}
		Type[] bounds = typeVariable.getBounds();
		if ( bounds[0] != Object.class ) {
			return bounds[0];
		}
		return typeVariable;
	}

	private static void discoverTypeVariableAssignments(Class<?> ownerType, Map<TypeVariable<?>, Type> typeAssignments) {
		Type genericSuperclass = ownerType.getGenericSuperclass();
		if ( genericSuperclass instanceof ParameterizedType ) {
			addTypeAssignments( typeAssignments, (ParameterizedType) genericSuperclass );
		}
		for ( Type genericInterface : ownerType.getGenericInterfaces() ) {
			if ( genericInterface instanceof ParameterizedType ) {
				addTypeAssignments( typeAssignments, (ParameterizedType) genericInterface );
			}
		}
		Class<?> superclass = ownerType.getSuperclass();
		if ( superclass != null && superclass != Object.class ) {
			discoverTypeVariableAssignments( superclass, typeAssignments );
		}
		for ( Class<?> anInterface : ownerType.getInterfaces() ) {
			discoverTypeVariableAssignments( anInterface, typeAssignments );
		}
	}

	private static void addTypeAssignments(Map<TypeVariable<?>, Type> typeAssignments, ParameterizedType parameterizedType) {
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		Class<?> rawType = (Class<?>) parameterizedType.getRawType();
		TypeVariable<? extends Class<?>>[] typeParameters = rawType.getTypeParameters();
		for ( int i = 0; i < typeParameters.length; i++ ) {
			typeAssignments.put( typeParameters[i], actualTypeArguments[ i ] );
		}
	}

	private static DataFormat createBaseTypeDataFormat(Class<?> type, ConventionContext conventionContext) {
		if ( conventionContext.isEnumType( type ) ) {
			return DataFormat.enumType( type );
		}
		else {
			return DataFormat.of( type, Collections.emptyList() );
		}
	}

	private static boolean isAccessor(Method method) {
		return !method.isSynthetic()
				&& !method.isBridge()
				&& Modifier.isPublic( method.getModifiers() )
				&& !Modifier.isStatic( method.getModifiers() )
				&& method.getReturnType() != void.class
				&& method.getParameterCount() == 0;
	}

	private static String getAttributeName(Method method) {
		if ( method.getParameterCount() == 0 && method.getReturnType() != void.class ) {
			String methodName = method.getName();
			if ( methodName.startsWith( "get" ) && methodName.length() > 3 ) {
				return Character.toLowerCase( methodName.charAt( 3 ) ) + methodName.substring( 4 );
			}
			else if ( methodName.startsWith( "is" ) && methodName.length() > 2 ) {
				return Character.toLowerCase( methodName.charAt( 2 ) ) + methodName.substring( 3 );
			}
		}
		return null;
	}

	private interface DataFormatFactory {
		TreeMap<String, ? extends Member> getAttributes(Class<?> clazz);

		DataFormatFieldAccessor memberAccessor(Member member, ConventionContext conventionContext);

		Type memberType(Member member);
	}

	private static class FieldsConventionDataFormatFactory implements DataFormatFactory {

		static final FieldsConventionDataFormatFactory INSTANCE = new FieldsConventionDataFormatFactory();

		private FieldsConventionDataFormatFactory() {
		}

		@Override
		public TreeMap<String, ? extends Member> getAttributes(Class<?> clazz) {
			TreeMap<String, Field> attributeMap = new TreeMap<>();
			do {
				for ( Field field : clazz.getDeclaredFields() ) {
					if ( !Modifier.isTransient( field.getModifiers() ) && !Modifier.isStatic( field.getModifiers() ) ) {
						attributeMap.putIfAbsent( field.getName(), field );
					}
				}
				clazz = clazz.getSuperclass();
			}
			while ( clazz != null && clazz != Object.class );
			return attributeMap;
		}

		@Override
		public DataFormatFieldAccessor memberAccessor(Member member, ConventionContext conventionContext) {
			return new FieldFieldAccessor( (Field) member );
		}

		@Override
		public Type memberType(Member member) {
			return ((Field) member).getGenericType();
		}
	}

	private static class BeansConventionDataFormatFactory implements DataFormatFactory {

		static final BeansConventionDataFormatFactory INSTANCE = new BeansConventionDataFormatFactory();

		private BeansConventionDataFormatFactory() {
		}

		private static void visitClassAttributes(TreeMap<String, Method> attributeMap, Class<?> clazz) {
			do {
				visitDeclaredAttributes( attributeMap, clazz );
				clazz = clazz.getSuperclass();
			}
			while ( clazz != null && clazz != Object.class );
		}

		private static void visitInterfaceAttributes(TreeMap<String, Method> attributeMap, Class<?> clazz) {
			for ( Class<?> interfaceClazz : clazz.getInterfaces() ) {
				visitDeclaredAttributes( attributeMap, interfaceClazz );
			}
			if ( clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class ) {
				visitInterfaceAttributes( attributeMap, clazz.getSuperclass() );
			}
			for ( Class<?> interfaceClazz : clazz.getInterfaces() ) {
				visitInterfaceAttributes( attributeMap, interfaceClazz );
			}
		}

		private static void visitDeclaredAttributes(TreeMap<String, Method> attributeMap, Class<?> clazz) {
			Method[] declaredMethods = clazz.getDeclaredMethods();
			for ( Method method : declaredMethods ) {
				if ( isAccessor( method ) ) {
					String attributeName = getAttributeName( method );
					if ( attributeName != null ) {
						attributeMap.putIfAbsent( attributeName, method );
					}
				}
			}
		}

		@Override
		public TreeMap<String, ? extends Member> getAttributes(Class<?> clazz) {
			TreeMap<String, Method> attributeMap = new TreeMap<>();
			visitClassAttributes( attributeMap, clazz );
			visitInterfaceAttributes( attributeMap, clazz );
			return attributeMap;
		}

		@Override
		public DataFormatFieldAccessor memberAccessor(Member member, ConventionContext conventionContext) {
			Method method = (Method) member;
			return conventionContext.nullOnException( method ) ? new LaxMethodFieldAccessor( method )
					: new MethodFieldAccessor( method );
		}

		@Override
		public Type memberType(Member member) {
			return ((Method) member).getGenericReturnType();
		}
	}

	private static class FieldsViaMethodConventionDataFormatFactory implements DataFormatFactory {

		static final FieldsViaMethodConventionDataFormatFactory INSTANCE = new FieldsViaMethodConventionDataFormatFactory();

		private FieldsViaMethodConventionDataFormatFactory() {
		}

		@Override
		public TreeMap<String, ? extends Member> getAttributes(Class<?> clazz) {
			TreeMap<String, Method> attributeMap = new TreeMap<>();
			do {
				for ( Field field : clazz.getDeclaredFields() ) {
					if ( !Modifier.isTransient( field.getModifiers() ) && !Modifier.isStatic( field.getModifiers() ) ) {
						Method method;
						try {
							method = clazz.getDeclaredMethod( field.getName() );
						}
						catch (NoSuchMethodException e) {
							try {
								method = clazz.getDeclaredMethod(
										"get" + Character.toUpperCase( field.getName().charAt( 0 ) ) + field.getName()
												.substring( 1 ) );
							}
							catch (NoSuchMethodException e2) {
								throw new RuntimeException( "Couldn't find method for field: " + field.getName(), e );
							}
						}
						attributeMap.putIfAbsent( field.getName(), method );
					}
				}
				clazz = clazz.getSuperclass();
			}
			while ( clazz != null && clazz != Object.class );
			return attributeMap;
		}

		@Override
		public DataFormatFieldAccessor memberAccessor(Member member, ConventionContext conventionContext) {
			return new MethodFieldAccessor( (Method) member );
		}

		@Override
		public Type memberType(Member member) {
			return ((Method) member).getGenericReturnType();
		}
	}

	private static class ComponentMethodConventionDataFormatFactory implements DataFormatFactory {

		static final ComponentMethodConventionDataFormatFactory INSTANCE = new ComponentMethodConventionDataFormatFactory();

		private ComponentMethodConventionDataFormatFactory() {
		}

		@Override
		public TreeMap<String, ? extends Member> getAttributes(Class<?> clazz) {
			TreeMap<String, Method> attributeMap = new TreeMap<>();
			do {
				for ( Method method : clazz.getDeclaredMethods() ) {
					if ( isAccessor( method ) && !isToStringOrHashCode( method ) ) {
						String attributeName = getAttributeName( method );
						if ( attributeName != null ) {
							attributeMap.putIfAbsent( attributeName, method );
						}
						else {
							attributeMap.putIfAbsent( method.getName(), method );
						}
					}
				}
				clazz = clazz.getSuperclass();
			}
			while ( clazz != null && clazz != Object.class );
			return attributeMap;
		}

		private static boolean isToStringOrHashCode(Method method) {
			return "toString".equals( method.getName() ) || "hashCode".equals( method.getName() );
		}

		@Override
		public DataFormatFieldAccessor memberAccessor(Member member, ConventionContext conventionContext) {
			return new MethodFieldAccessor( (Method) member );
		}

		@Override
		public Type memberType(Member member) {
			return ((Method) member).getGenericReturnType();
		}
	}

}
