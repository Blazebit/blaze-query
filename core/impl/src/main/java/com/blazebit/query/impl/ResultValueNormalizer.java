/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import org.apache.calcite.avatica.AvaticaResultSet;
import org.apache.calcite.avatica.ColumnMetaData;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Normalizes JDBC composite values (Struct, Array, Object[]) into standard
 * Java types (Map, List). When Avatica column metadata is available, structs
 * are converted to maps with field names. Without metadata, structs are
 * converted to object arrays.
 * <p>
 * Scalar type conversion (e.g. Timestamp to Instant) is delegated to the
 * {@link ScalarConverter}, which typically routes through the
 * {@link DefaultTypeConverter}.
 *
 * @author Max Hovens
 * @since 2.3.0
 */
class ResultValueNormalizer {

	private final ColumnMetaData[] columnMetadata;
	private final ScalarConverter scalarConverter;

	private ResultValueNormalizer(ColumnMetaData[] columnMetadata, ScalarConverter scalarConverter) {
		this.columnMetadata = columnMetadata;
		this.scalarConverter = scalarConverter;
	}

	/**
	 * Creates a normalizer for the given result set.
	 * Avatica column metadata is extracted when available to enable
	 * struct-to-map conversion with field names.
	 */
	static ResultValueNormalizer create(ResultSet resultSet, ScalarConverter scalarConverter) {
		return new ResultValueNormalizer( extractColumnMetadata( resultSet ), scalarConverter );
	}

	/**
	 * Normalizes a column value using its positional metadata.
	 */
	Object normalizeColumn(Object value, int columnIndex) throws SQLException {
		ColumnMetaData metadata = columnMetadata != null ? columnMetadata[columnIndex] : null;
		return normalizeValue( value, metadata );
	}

	/**
	 * Normalizes a value using the given column metadata.
	 * Converts structs to maps (with metadata) or object arrays (without),
	 * arrays to lists, and recursively handles nested structures.
	 */
	Object normalizeValue(Object value, ColumnMetaData metadata) throws SQLException {
		if ( value == null ) {
			return null;
		}
		if ( metadata != null ) {
			if ( value instanceof Struct struct
					&& metadata.type instanceof ColumnMetaData.StructType structType ) {
				return structToMap( struct, structType );
			}
			if ( value instanceof java.sql.Array array
					&& metadata.type instanceof ColumnMetaData.ArrayType arrayType ) {
				return normalizeArrayWithMetadata( array, arrayType );
			}
		}
		if ( value instanceof Struct struct ) {
			return normalizeStructAttributes( struct );
		}
		if ( value instanceof java.sql.Array array ) {
			return normalizeArray( array );
		}
		if ( value instanceof Object[] objectArray ) {
			return normalizeObjectArray( objectArray );
		}
		return scalarConverter.convert( value );
	}

	private Map<String, Object> structToMap(
			Struct struct,
			ColumnMetaData.StructType structType) throws SQLException {
		Object[] attributes = struct.getAttributes();
		List<ColumnMetaData> columns = structType.columns;
		Map<String, Object> map = new LinkedHashMap<>( attributes.length );
		for ( int i = 0; i < attributes.length; i++ ) {
			ColumnMetaData col = columns.get( i );
			map.put( col.columnName, normalizeValue( attributes[i], col ) );
		}
		return map;
	}

	private List<Object> normalizeArrayWithMetadata(
			java.sql.Array array,
			ColumnMetaData.ArrayType arrayType) throws SQLException {
		Object[] elements = (Object[]) array.getArray();
		List<Object> list = new ArrayList<>( elements.length );
		ColumnMetaData.AvaticaType componentType = arrayType.getComponent();
		for ( Object element : elements ) {
			if ( element instanceof Struct struct
					&& componentType instanceof ColumnMetaData.StructType structType ) {
				list.add( structToMap( struct, structType ) );
			}
			else {
				list.add( normalizeValue( element, null ) );
			}
		}
		return list;
	}

	private Object[] normalizeStructAttributes(Struct struct) throws SQLException {
		Object[] attributes = struct.getAttributes();
		Object[] result = new Object[attributes.length];
		for ( int i = 0; i < attributes.length; i++ ) {
			result[i] = normalizeValue( attributes[i], null );
		}
		return result;
	}

	private List<Object> normalizeArray(java.sql.Array array) throws SQLException {
		Object[] elements = (Object[]) array.getArray();
		List<Object> list = new ArrayList<>( elements.length );
		for ( Object element : elements ) {
			list.add( normalizeValue( element, null ) );
		}
		return list;
	}

	private Object[] normalizeObjectArray(Object[] objectArray) throws SQLException {
		Object[] result = new Object[objectArray.length];
		for ( int i = 0; i < objectArray.length; i++ ) {
			result[i] = normalizeValue( objectArray[i], null );
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private static ColumnMetaData[] extractColumnMetadata(ResultSet resultSet) {
		try {
			if ( !(resultSet instanceof AvaticaResultSet) ) {
				return null;
			}
			Field field = AvaticaResultSet.class.getDeclaredField( "columnMetaDataList" );
			field.setAccessible( true );
			List<ColumnMetaData> columns = (List<ColumnMetaData>) field.get( resultSet );
			return columns.toArray( new ColumnMetaData[0] );
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * Converts a scalar (non-composite) value.
	 */
	@FunctionalInterface
	interface ScalarConverter {
		Object convert(Object value) throws SQLException;
	}
}
