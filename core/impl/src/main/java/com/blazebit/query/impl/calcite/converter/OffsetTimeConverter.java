/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.converter;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.temporal.ChronoField;

/**
 * Converter for an {@link OffsetTime} value.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class OffsetTimeConverter implements Converter<OffsetTime, Integer> {
	/**
	 * The {@link OffsetTime} converter.
	 */
	public static final OffsetTimeConverter INSTANCE = new OffsetTimeConverter();

	private OffsetTimeConverter() {
	}

	@Override
	public Integer convert(OffsetTime o) {
		if ( o == null ) {
			return null;
		}
		return (int) (o.toEpochSecond( LocalDate.EPOCH ) * 1000) + o.get( ChronoField.MILLI_OF_SECOND );
	}
}
