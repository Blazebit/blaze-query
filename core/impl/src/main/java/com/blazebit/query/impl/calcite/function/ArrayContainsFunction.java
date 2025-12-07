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
import org.apache.calcite.runtime.FlatLists;
import org.apache.calcite.schema.FunctionParameter;
import org.apache.calcite.schema.ImplementableFunction;
import org.apache.calcite.schema.ScalarFunction;
import org.apache.calcite.sql.SqlCollation;
import org.apache.calcite.sql.SqlUtil;
import org.apache.calcite.sql.type.SqlTypeUtil;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.List;

import static org.apache.calcite.adapter.enumerable.EnumUtils.generateCollatorExpression;
import static org.apache.calcite.linq4j.Nullness.castNonNull;
import static org.apache.calcite.util.Static.RESOURCE;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ArrayContainsFunction implements ScalarFunction, ImplementableFunction, CallImplementor {

	private final Method basicMethod;
	private final Method decimalMethod;
	private final Method comparatorMethod;
	private final List<FunctionParameter> parameters = ImmutableList.of(
			new SimpleFunctionParameter(0, "array", List.class, false),
			new SimpleFunctionParameter(1, "element", Object.class, false)
	);

	public ArrayContainsFunction() {
		try {
			this.basicMethod = ArrayContainsFunction.class.getDeclaredMethod("arrayContains", List.class, Object.class);
			this.decimalMethod = ArrayContainsFunction.class.getDeclaredMethod("arrayContains", List.class, BigDecimal.class);
			this.comparatorMethod = ArrayContainsFunction.class.getDeclaredMethod("arrayContains", List.class, String.class, Comparator.class);
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
		final RelDataType operandType0 = castNonNull(call.getOperands().get(0).getType().getComponentType());
		final RelDataType operandType1 = call.getOperands().get(1).getType();
		final Expression fieldComparator;
		if (SqlTypeUtil.inCharFamily(operandType0) && SqlTypeUtil.inCharFamily(operandType1)) {
			Charset cs0 = operandType0.getCharset();
			Charset cs1 = operandType1.getCharset();
			assert (null != cs0) && (null != cs1)
					: "An implicit or explicit charset should have been set";
			if (!cs0.equals(cs1)) {
				throw SqlUtil.newContextException(call.pos, RESOURCE.incompatibleCharset("array_contains", cs0.name(), cs1.name()));
			}

			SqlCollation collation0 = operandType0.getCollation();
			SqlCollation collation1 = operandType1.getCollation();
			assert (null != collation0) && (null != collation1)
					: "An implicit or explicit collation should have been set";

			// Validation will occur inside getCoercibilityDyadicOperator...
			SqlCollation resultCol = SqlCollation.getCoercibilityDyadicOperator(collation0, collation1);
			fieldComparator = generateCollatorExpression(resultCol);
		}
		else {
			fieldComparator = null;
		}
		if (expressions.get(1).getType() == BigDecimal.class) {
			return Expressions.call(decimalMethod, expressions);
		}

		return fieldComparator == null
				? Expressions.call(basicMethod, expressions)
				: Expressions.call(comparatorMethod, FlatLists.append(expressions, fieldComparator));
	}

	public static Boolean arrayContains(List<?> list, Object element) {
		if (list == null) {
			return null;
		}
		if (element instanceof List<?> list2) {
			for (Object e : list2) {
				if (!list.contains(e)) {
					return false;
				}
			}
			return true;
		}
		return list.contains(element);
	}

	public static Boolean arrayContains(List<BigDecimal> list, BigDecimal element) {
		if (list == null) {
			return null;
		}
		for (BigDecimal t : list) {
			if (t.compareTo(element) == 0) {
				return true;
			}
		}
		return false;
	}

	public static Boolean arrayContains(List<String> list, String element, Comparator<String> comparator) {
		if (list == null) {
			return null;
		}
		for (String t : list) {
			if (comparator.compare(t, element) == 0) {
				return true;
			}
		}
		return false;
	}
}
