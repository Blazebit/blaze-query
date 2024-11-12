/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.converter;

import java.time.OffsetDateTime;

/**
 * Converter for an {@link OffsetDateTime} value.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class OffsetDateTimeConverter implements Converter<OffsetDateTime, Long> {
	/**
	 * The {@link OffsetDateTime} converter.
	 */
	public static final OffsetDateTimeConverter INSTANCE = new OffsetDateTimeConverter();

	private OffsetDateTimeConverter() {
	}

	@Override
	public Long convert(OffsetDateTime o) {
		if ( o == null ) {
			return null;
		}
		return o.toInstant().toEpochMilli();
	}
}
