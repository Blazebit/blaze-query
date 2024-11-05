/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.converter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

/**
 * Converter for an {@link LocalDateTime} value.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class LocalDateTimeConverter implements Converter<LocalDateTime, Long> {
	/**
	 * The {@link LocalDateTime} converter.
	 */
	public static final LocalDateTimeConverter INSTANCE = new LocalDateTimeConverter();

	private LocalDateTimeConverter() {
	}

	@Override
	public Long convert(LocalDateTime o) {
		if ( o == null ) {
			return null;
		}
		return o.toEpochSecond( ZoneOffset.UTC ) * 1000 + o.get( ChronoField.MILLI_OF_SECOND );
	}
}
