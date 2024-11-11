/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite;

import java.util.List;

import com.blazebit.query.impl.calcite.converter.Converter;
import org.apache.calcite.linq4j.Enumerator;

/**
 * An enumerator for producing converted objects.
 *
 * @param <NativeType> The original type of the result list
 * @param <Result> The result type produced by the enumerator
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ConverterListEnumerator<NativeType, Result> implements Enumerator<Result> {
	private final List<? extends NativeType> list;
	private final Converter<NativeType, Result> converter;
	private int i = -1;

	/**
	 * Creates a new enumerator that produces objects by applying the given converter on elements of the given list.
	 *
	 * @param list The original list
	 * @param converter The converter to apply
	 */
	public ConverterListEnumerator(List<? extends NativeType> list, Converter<NativeType, Result> converter) {
		this.list = list;
		this.converter = converter;
	}

	@Override
	public Result current() {
		return converter.convert( list.get( i ) );
	}

	@Override
	public boolean moveNext() {
		return ++i < list.size();
	}

	@Override
	public void reset() {
		i = -1;
	}

	@Override
	public void close() {
	}
}
