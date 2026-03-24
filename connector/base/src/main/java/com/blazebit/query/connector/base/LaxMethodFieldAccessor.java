/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.base;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.blazebit.query.spi.DataFormatFieldAccessor;

/**
 * Accessor for a method based attribute of an object.
 * Returns {@code null} if an exception is thrown instead of re-throwing it.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class LaxMethodFieldAccessor implements DataFormatFieldAccessor {

	private static final Logger LOG = Logger.getLogger( LaxMethodFieldAccessor.class.getName() );

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
			LOG.log( Level.FINE, "Lax accessor for {0}.{1}() returned null due to exception",
					new Object[]{ method.getDeclaringClass().getSimpleName(), method.getName() } );
			return null;
		}
	}
}
