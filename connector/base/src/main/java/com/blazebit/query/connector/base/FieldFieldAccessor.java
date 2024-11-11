/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.base;

import java.lang.reflect.Field;

import com.blazebit.query.spi.DataFormatFieldAccessor;

/**
 * Accessor for a field based attribute of an object.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class FieldFieldAccessor implements DataFormatFieldAccessor {
	private final Field field;

	/**
	 * Creates a field accessor.
	 *
	 * @param field The field to obtain an attribute value.
	 */
	public FieldFieldAccessor(Field field) {
		this.field = field;
	}

	public Field getField() {
		return field;
	}

	@Override
	public Object get(Object o) {
		try {
			return field.get( o );
		}
		catch (Exception e) {
			throw new RuntimeException( e );
		}
	}
}
