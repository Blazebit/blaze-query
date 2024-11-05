/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.converter;

import java.time.ZonedDateTime;

/**
 * Converter for an {@link ZonedDateTime} value.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ZonedDateTimeConverter implements Converter<ZonedDateTime, Long> {
	/**
	 * The {@link ZonedDateTime} converter.
	 */
	public static final ZonedDateTimeConverter INSTANCE = new ZonedDateTimeConverter();

	private ZonedDateTimeConverter() {
	}

	@Override
	public Long convert(ZonedDateTime o) {
		if ( o == null ) {
			return null;
		}
		return o.toInstant().toEpochMilli();
	}
}
