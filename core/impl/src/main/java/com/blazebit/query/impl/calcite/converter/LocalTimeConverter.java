/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.converter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

/**
 * Converter for an {@link LocalTime} value.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class LocalTimeConverter implements Converter<LocalTime, Integer> {
	/**
	 * The {@link LocalTime} converter.
	 */
	public static final LocalTimeConverter INSTANCE = new LocalTimeConverter();

	private LocalTimeConverter() {
	}

	@Override
	public Integer convert(LocalTime o) {
		if ( o == null ) {
			return null;
		}
		return (int) (o.toEpochSecond( LocalDate.EPOCH, ZoneOffset.UTC ) * 1000) + o.get( ChronoField.MILLI_OF_SECOND );
	}
}
