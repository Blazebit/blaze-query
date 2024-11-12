/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.spi;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * An object that describes the data format of a type.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DataFormat {

	/**
	 * Returns the Java type for this format.
	 *
	 * @return the Java type for this format
	 */
	Type getType();

	/**
	 * Returns the fields encapsulated by this format.
	 *
	 * @return the fields encapsulated by this format
	 */
	List<DataFormatField> getFields();

	/**
	 * Returns whether this data format represents an enum.
	 *
	 * @return whether this data format represents an enum.
	 */
	default boolean isEnum() {
		return false;
	}

	/**
	 * Returns a new data format for the given type and fields.
	 *
	 * @param type The type
	 * @param fields The fields of the type
	 * @return the data format
	 */
	static DataFormat of(Type type, List<DataFormatField> fields) {
		return new DataFormat() {
			@Override
			public Type getType() {
				return type;
			}

			@Override
			public List<DataFormatField> getFields() {
				return fields;
			}
		};
	}

	/**
	 * Returns a new enum data format for the given type.
	 *
	 * @param type The enum type
	 * @return the data format
	 */
	static DataFormat enumType(Type type) {
		return new DataFormat() {
			@Override
			public Type getType() {
				return type;
			}

			@Override
			public List<DataFormatField> getFields() {
				return Collections.emptyList();
			}

			@Override
			public boolean isEnum() {
				return true;
			}
		};
	}

}
