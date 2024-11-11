/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.base;

import java.lang.reflect.Method;

import com.blazebit.query.spi.DataFormatFieldAccessor;

/**
 * Accessor for a method based attribute of an object.
 * Returns {@code null} if an exception is thrown instead of re-throwing it.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class LaxMethodFieldAccessor implements DataFormatFieldAccessor {
	private final Method method;

	/**
	 * Creates a method accessor.
	 *
	 * @param method The method to obtain an attribute value.
	 */
	public LaxMethodFieldAccessor(Method method) {
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	@Override
	public Object get(Object o) {
		try {
			return method.invoke( o );
		}
		catch (Exception e) {
			throw new RuntimeException( e );
		}
	}
}
