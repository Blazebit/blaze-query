/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.converter;

import java.time.Instant;

/**
 * Converter for an {@link Instant} value.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class InstantConverter implements Converter<Instant, Long> {
	/**
	 * The {@link Instant} converter.
	 */
	public static final InstantConverter INSTANCE = new InstantConverter();

	private InstantConverter() {
	}

	@Override
	public Long convert(Instant o) {
		if ( o == null ) {
			return null;
		}
		return o.toEpochMilli();
	}
}
