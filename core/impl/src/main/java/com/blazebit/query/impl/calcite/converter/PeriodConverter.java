/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.converter;

import java.time.Period;

/**
 * Converter for an {@link Period} value.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class PeriodConverter implements Converter<Period, Integer> {
	/**
	 * The {@link Period} converter.
	 */
	public static final PeriodConverter INSTANCE = new PeriodConverter();

	private PeriodConverter() {
	}

	@Override
	public Integer convert(Period o) {
		if ( o == null ) {
			return null;
		}

		return (int) o.toTotalMonths();
	}
}
