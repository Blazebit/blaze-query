/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.base;

import java.lang.reflect.Method;

import com.blazebit.query.spi.DataFormatFieldAccessor;

/**
 * Accessor that wraps another accessor and applies a static conversion method to the result.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class ConvertingFieldAccessor implements DataFormatFieldAccessor {
	private final DataFormatFieldAccessor delegate;
	private final Method converterMethod;

	/**
	 * Creates a converting accessor.
	 *
	 * @param delegate The delegate accessor to obtain the raw value.
	 * @param converterMethod A static method to convert the raw value to the target type.
	 */
	public ConvertingFieldAccessor(DataFormatFieldAccessor delegate, Method converterMethod) {
		this.delegate = delegate;
		this.converterMethod = converterMethod;
	}

	public DataFormatFieldAccessor getDelegate() {
		return delegate;
	}

	public Method getConverterMethod() {
		return converterMethod;
	}

	@Override
	public Object get(Object o) {
		try {
			return converterMethod.invoke( null, delegate.get( o ) );
		}
		catch (Exception e) {
			throw new RuntimeException( e );
		}
	}
}
