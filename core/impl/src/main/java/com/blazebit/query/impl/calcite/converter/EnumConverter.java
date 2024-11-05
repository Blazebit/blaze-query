/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.converter;

/**
 * Converter for an {@link Enum} value.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class EnumConverter implements Converter<Enum<?>, String> {
	/**
	 * Creates a new {@link Enum} converter.
	 */
	public static final EnumConverter INSTANCE = new EnumConverter();

	private EnumConverter() {
	}

	@Override
	public String convert(Enum<?> o) {
		if ( o == null ) {
			return null;
		}
		return o.name();
	}
}
