/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.function;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.adapter.enumerable.CallImplementor;
import org.apache.calcite.adapter.enumerable.RexImpTable;
import org.apache.calcite.adapter.enumerable.RexToLixTranslator;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.schema.FunctionParameter;
import org.apache.calcite.schema.ImplementableFunction;
import org.apache.calcite.schema.ScalarFunction;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ArraysOverlapFunction implements ScalarFunction, ImplementableFunction, CallImplementor {

	private final Method basicMethod;
	private final List<FunctionParameter> parameters = ImmutableList.of(
			new SimpleFunctionParameter(0, "array0", List.class, false),
			new SimpleFunctionParameter(1, "array1", List.class, false)
	);

	public ArraysOverlapFunction() {
		try {
			this.basicMethod = ArraysOverlapFunction.class.getDeclaredMethod( "arraysOverlap", List.class, List.class);
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public List<FunctionParameter> getParameters() {
		return parameters;
	}

	@Override
	public RelDataType getReturnType(RelDataTypeFactory typeFactory) {
		return typeFactory.createJavaType(Boolean.class);
	}

	@Override
	public CallImplementor getImplementor() {
		return this;
	}

	@Override
	public Expression implement(RexToLixTranslator translator, RexCall call, RexImpTable.NullAs nullAs) {
		final List<Expression> expressions = translator.translateList(call.getOperands());
		return Expressions.call(basicMethod, expressions);
	}

	public static Boolean arraysOverlap(List<?> list1, List<?> list2) {
		if (list1 == null || list2 == null) {
			return null;
		}
		if (list1.size() < list2.size()) {
			for (Object o1 : list1) {
				if (list2.contains(o1)) {
					return true;
				}
			}
		}
		else {
			for (Object o1 : list2) {
				if (list1.contains(o1)) {
					return true;
				}
			}
		}
		return false;
	}

}
