/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.base;

import com.blazebit.query.spi.DataFormatFieldAccessor;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.apache.calcite.sql.type.JavaToSqlTypeConversionRules;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface ConventionContext {

	/**
	 * A no-op filter that doesn't filter anything.
	 */
	ConventionContext NO_FILTER = new ConventionContext() {
		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			return this;
		}
	};

	/**
	 * Returns the sub-filter to use for attributes of the return type of the attribute identified by the given method.
	 * A {@code null} return means that the attribute identified by the given method should be filtered.
	 *
	 * @param concreteClass The concrete class for which this method should be checked
	 * @param member The member for an attribute that should be checked
	 * @return The sub-filter to use or {@code null} if the attribute for this method should be filtered.
	 */
	ConventionContext getSubFilter(Class<?> concreteClass, Member member);

	/**
	 * Returns whether the given class is a basic type.
	 *
	 * @param typeClass The class to check
	 * @return whether the given class is a basic type
	 */
	default boolean isBaseType(Class<?> typeClass) {
		return isEnumType( typeClass )
				|| JavaToSqlTypeConversionRules.instance().lookup( typeClass ) != null
				|| typeClass == Object.class
				|| typeClass == Instant.class
				|| typeClass == ZonedDateTime.class
				|| typeClass == OffsetDateTime.class
				|| typeClass == OffsetTime.class
				|| typeClass == LocalDate.class
				|| typeClass == LocalDateTime.class
				|| typeClass == LocalTime.class
				|| typeClass == Duration.class
				|| typeClass == Period.class
				|| typeClass == UUID.class
				;
	}

	/**
	 * Returns whether the given class is an enum type.
	 *
	 * @param typeClass The class to check
	 * @return whether the given class is an enum type
	 */
	default boolean isEnumType(Class<?> typeClass) {
		return typeClass.isEnum();
	}

	/**
	 * Returns whether an exception thrown by the given accessor method should cause {@code null} to be returned
	 * instead of rethrowing.
	 *
	 * @param method The accessor method to check
	 * @return whether a method invocation should produce {@code null} when an exception happens
	 */
	default boolean nullOnException(Method method) {
		return false;
	}

	/**
	 * Resolves the given type to a different type that should be used for the data format.
	 * This allows remapping types that are not directly supported by the query engine.
	 *
	 * @param typeClass The type class to resolve
	 * @return The resolved type class, or the original type class if no resolution is needed
	 */
	default Class<?> resolveType(Class<?> typeClass) {
		return typeClass;
	}

	/**
	 * Creates a converting accessor that wraps the given accessor to convert values
	 * from the source type to the target type, as determined by {@link #resolveType(Class)}.
	 *
	 * @param accessor The original accessor
	 * @param sourceType The original type returned by the accessor
	 * @param targetType The target type to convert to
	 * @return A converting accessor, or the original accessor if no conversion is needed
	 */
	default DataFormatFieldAccessor createConvertingAccessor(
			DataFormatFieldAccessor accessor, Class<?> sourceType, Class<?> targetType) {
		return accessor;
	}
}
