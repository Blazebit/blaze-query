/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import org.junit.jupiter.api.Test;


import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Max Hovens
 * @since 2.2.0
 */
public class ExhaustiveTypeConversionTest {

	private Object convert(Object value, Type targetType) throws Exception {
		QueryContextBuilderImpl builder = new QueryContextBuilderImpl();
		QueryContextImpl queryContext = (QueryContextImpl) builder.build();
		Method convertMethod = QueryContextImpl.class.getDeclaredMethod("convert", Object.class, Type.class);
		convertMethod.setAccessible(true);
		return convertMethod.invoke(queryContext, value, targetType);
	}

	@Test
	void testDateTimeConversions() throws Exception {
		long now = 1710000000000L; // Fixed timestamp
		Timestamp timestamp = new Timestamp(now);
		Date date = new Date(now);
		Time time = new Time(now);

		assertEquals(timestamp.toInstant(), convert(timestamp, Instant.class));
		assertEquals(timestamp.toLocalDateTime(), convert(timestamp, LocalDateTime.class));
		assertEquals(date.toLocalDate(), convert(date, LocalDate.class));
		assertEquals(time.toLocalTime(), convert(time, LocalTime.class));

		assertEquals(timestamp.toInstant().atZone(ZoneOffset.UTC), convert(timestamp, ZonedDateTime.class));
		assertEquals(timestamp.toInstant().atOffset(ZoneOffset.UTC), convert(timestamp, OffsetDateTime.class));
		assertEquals(time.toLocalTime().atOffset(ZoneOffset.UTC), convert(time, OffsetTime.class));

		// Long to Instant
		assertEquals(Instant.ofEpochMilli(now), convert(now, Instant.class));
	}

	@Test
	void testDurationAndPeriod() throws Exception {
		assertEquals(Duration.ofMillis(1000), convert(1000L, Duration.class));
		assertEquals(Period.ofMonths(12), convert(12, Period.class));
	}

	@Test
	void testUUID() throws Exception {
		UUID uuid = UUID.randomUUID();
		assertEquals(uuid, convert(uuid.toString(), UUID.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	void testCollectionConversions() throws Exception {
		Timestamp t1 = new Timestamp(1710000000000L);
		Timestamp t2 = new Timestamp(1710001000000L);
		Array mockArray = new MockArray(new Object[]{t1, t2});

		// Test List<Instant>
		ParameterizedType listType = new MockParameterizedType(List.class, Instant.class);

		List<Instant> result = (List<Instant>) convert(mockArray, listType);
		assertEquals(2, result.size());
		assertEquals(t1.toInstant(), result.get(0));
		assertEquals(t2.toInstant(), result.get(1));

		// Test Set<Instant>
		ParameterizedType setType = new MockParameterizedType(Set.class, Instant.class);
		Set<Instant> setResult = (Set<Instant>) convert(mockArray, setType);
		assertEquals(2, setResult.size());
		assertTrue(setResult.contains(t1.toInstant()));
	}

	@Test
	void testStructConversion() throws Exception {
		Timestamp t1 = new Timestamp(1710000000000L);
		Struct mockStruct = new MockStruct(new Object[]{"test", t1});

		Object[] result = (Object[]) convert(mockStruct, Object.class);
		assertEquals("test", result[0]);
		assertEquals(t1.toInstant(), result[1]);
	}

	@Test
	@SuppressWarnings("unchecked")
	void testArrayOfArrayConversion() throws Exception {
		Array innerMock = new MockArray(new Object[]{1, 2});
		Array outerMock = new MockArray(new Object[]{innerMock});

		List<List<Integer>> result = (List<List<Integer>>) convert(outerMock, List.class);
		assertEquals(1, result.size());
		assertEquals(List.of(1, 2), result.get(0));
	}

	@Test
	void testEnumConversion() throws Exception {
		assertEquals(Thread.State.RUNNABLE, convert("RUNNABLE", Thread.State.class));
	}

@Test
void testNumericConversions() throws Exception {
	assertEquals(new BigDecimal("10.5"), convert(new BigDecimal("10.5"), BigDecimal.class));
	assertEquals(BigInteger.valueOf(100L), convert(BigInteger.valueOf(100L), BigInteger.class));

	// Target is BigDecimal, input is Double
	assertEquals(new BigDecimal("10.5"), convert(10.5d, BigDecimal.class));
	// Target is BigInteger, input is Long
	assertEquals(BigInteger.valueOf(100L), convert(100L, BigInteger.class));
	// Target is Long, input is Integer
	assertEquals(100L, convert(100, Long.class));
}

	private static class MockArray implements Array {
		private final Object[] elements;
		MockArray(Object[] elements) { this.elements = elements; }
		@Override public String getBaseTypeName() { return null; }
		@Override public int getBaseType() { return 0; }
		@Override public Object getArray() { return elements; }
		@Override public Object getArray(Map<String, Class<?>> map) { return elements; }
		@Override public Object getArray(long index, int count) { return null; }
		@Override public Object getArray(long index, int count, Map<String, Class<?>> map) { return null; }
		@Override public ResultSet getResultSet() { return null; }
		@Override public ResultSet getResultSet(Map<String, Class<?>> map) { return null; }
		@Override public ResultSet getResultSet(long index, int count) { return null; }
		@Override public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) { return null; }
		@Override public void free() {}
	}

	private static class MockStruct implements Struct {
		private final Object[] attributes;
		MockStruct(Object[] attributes) { this.attributes = attributes; }
		@Override public String getSQLTypeName() { return null; }
		@Override public Object[] getAttributes() { return attributes; }
		@Override public Object[] getAttributes(Map<String, Class<?>> map) { return attributes; }
	}

	private static class MockParameterizedType implements ParameterizedType {
		private final Class<?> raw;
		private final Type arg;
		MockParameterizedType(Class<?> raw, Type arg) { this.raw = raw; this.arg = arg; }
		@Override public Type[] getActualTypeArguments() { return new Type[]{arg}; }
		@Override public Type getRawType() { return raw; }
		@Override public Type getOwnerType() { return null; }
	}
}
