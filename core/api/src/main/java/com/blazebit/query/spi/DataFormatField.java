/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.spi;

/**
 * An object to fetch schema object data.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DataFormatField {

	/**
	 * Returns the name of the field.
	 *
	 * @return the name of the field
	 */
	String getName();

	/**
	 * Returns the accessor for the field.
	 *
	 * @return the accessor for the field
	 */
	DataFormatFieldAccessor getAccessor();

	/**
	 * Returns the format of the field type.
	 *
	 * @return the format of the field type
	 */
	DataFormat getFormat();

	/**
	 * Creates a new data format field.
	 *
	 * @param name The name of the field
	 * @param accessor The accessor for the field
	 * @param format The data format of the field type
	 * @return The new data format field
	 */
	static DataFormatField of(String name, DataFormatFieldAccessor accessor, DataFormat format) {
		return new DataFormatField() {
			@Override
			public String getName() {
				return name;
			}

			@Override
			public DataFormatFieldAccessor getAccessor() {
				return accessor;
			}

			@Override
			public DataFormat getFormat() {
				return format;
			}
		};
	}


}
