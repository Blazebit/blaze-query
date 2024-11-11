/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.spi;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * An object that describes the data format of a collection type.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface CollectionDataFormat extends DataFormat {

	/**
	 * Returns the Java type for this format.
	 *
	 * @return the Java type for this format
	 */
	Type getType();

	/**
	 * Returns the format of the collection element.
	 *
	 * @return the format of the collection element
	 */
	DataFormat getElementFormat();

	/**
	 * Returns a new collection data format.
	 *
	 * @param type The collection type
	 * @param elementFormat The element format
	 * @return the collection data format
	 */
	static CollectionDataFormat of(Type type, DataFormat elementFormat) {
		return new CollectionDataFormat() {
			@Override
			public Type getType() {
				return type;
			}

			@Override
			public List<DataFormatField> getFields() {
				return Collections.emptyList();
			}

			@Override
			public DataFormat getElementFormat() {
				return elementFormat;
			}
		};
	}

}
