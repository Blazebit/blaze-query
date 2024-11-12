/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.converter;

import java.time.Duration;

/**
 * Converter for an {@link Duration} value.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DurationConverter implements Converter<Duration, Long> {
	/**
	 * The {@link Duration} converter.
	 */
	public static final DurationConverter INSTANCE = new DurationConverter();

	private DurationConverter() {
	}

	@Override
	public Long convert(Duration o) {
		if ( o == null ) {
			return null;
		}

		return o.toMillis();
	}
}
