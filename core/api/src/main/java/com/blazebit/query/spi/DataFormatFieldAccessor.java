/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.spi;

/**
 * An accessor for a {@link DataFormatField} to access data.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DataFormatFieldAccessor {
	/**
	 * Returns the value for a field as accessed by this accessor.
	 *
	 * @param obj The object on which to access the field
	 * @return The value of a field of the given object
	 */
	Object get(Object obj);
}
