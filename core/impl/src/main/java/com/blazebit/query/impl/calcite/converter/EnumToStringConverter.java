/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.converter;

/**
 * Converter for a custom {@code enum} that uses {@code toString()} for conversion.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class EnumToStringConverter implements Converter<Object, String> {
	/**
	 * Creates a new {@code enum} converter.
	 */
	public static final EnumToStringConverter INSTANCE = new EnumToStringConverter();

	private EnumToStringConverter() {
	}

	@Override
	public String convert(Object o) {
		if ( o == null ) {
			return null;
		}
		return o.toString();
	}
}
