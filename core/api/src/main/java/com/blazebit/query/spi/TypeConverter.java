/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.spi;

import java.lang.reflect.Type;
import java.sql.SQLException;

/**
 * A converter for values to a target type.
 *
 * @author Max Hovens
 * @since 2.3.0
 */
public interface TypeConverter {

	/**
	 * Returns whether this converter can convert the value to the target type.
	 *
	 * @param value The value to convert
	 * @param targetType The target type
	 * @return whether this converter can convert the value to the target type
	 */
	boolean canConvert(Object value, Type targetType);

	/**
	 * Converts the value to the target type.
	 *
	 * @param value The value to convert
	 * @param targetType The target type
	 * @param context The conversion context
	 * @return the converted value
	 * @throws SQLException if an error occurs during conversion
	 */
	Object convert(Object value, Type targetType, Context context) throws SQLException;

	/**
	 * A context for the conversion.
	 */
	interface Context {
		/**
		 * Converts the value to the target type using other converters.
		 *
		 * @param value The value to convert
		 * @param targetType The target type
		 * @return the converted value
		 * @throws SQLException if an error occurs during conversion
		 */
		Object convert(Object value, Type targetType) throws SQLException;
	}
}
