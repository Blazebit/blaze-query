/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.converter;

import java.time.LocalDate;

/**
 * Converter for an {@link LocalDate} value.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class LocalDateConverter implements Converter<LocalDate, Integer> {
	/**
	 * The {@link LocalDate} converter.
	 */
	public static final LocalDateConverter INSTANCE = new LocalDateConverter();

	private LocalDateConverter() {
	}

	@Override
	public Integer convert(LocalDate o) {
		if ( o == null ) {
			return null;
		}
		return (int) o.toEpochDay();
	}
}
