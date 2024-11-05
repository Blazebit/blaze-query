/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.converter;

/**
 * Accessor for an attribute of an object.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface Converter<Source, Target> {

	/**
	 * Returns the attribute value for the attribute accessed by this accessor of the given object.
	 *
	 * @param o The object
	 * @return The attribute value
	 */
	Target convert(Source o);
}
