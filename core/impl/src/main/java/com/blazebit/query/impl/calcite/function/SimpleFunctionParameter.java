/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.function;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.FunctionParameter;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class SimpleFunctionParameter implements FunctionParameter {

	private final int ordinal;
	private final String name;
	private final Class<?> type;
	private final boolean optional;

	public SimpleFunctionParameter(int ordinal, String name, Class<?> type, boolean optional) {
		this.ordinal = ordinal;
		this.name = name;
		this.type = type;
		this.optional = optional;
	}

	@Override
	public int getOrdinal() {
		return ordinal;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public RelDataType getType(RelDataTypeFactory typeFactory) {
		return typeFactory.createJavaType(type);
	}

	@Override
	public boolean isOptional() {
		return optional;
	}
}
