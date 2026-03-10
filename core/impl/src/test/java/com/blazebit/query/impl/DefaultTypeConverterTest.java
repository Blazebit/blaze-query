/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import com.blazebit.query.spi.TypeConverter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Max Hovens
 * @since 2.2.0
 */
class DefaultTypeConverterTest {

	private final DefaultTypeConverter converter = DefaultTypeConverter.INSTANCE;
	private final TypeConverter.Context context = new TypeConverter.Context() {
		@Override
		public Object convert(Object value, Type targetType) throws SQLException {
			return converter.convert(value, targetType, this);
		}
	};

	@Test
	void testNullValue() throws SQLException {
		assertNull(converter.convert(null, String.class, context));
	}

	@Test
	void testIdenticalType() throws SQLException {
		assertEquals("test", converter.convert("test", String.class, context));
		assertEquals(123, converter.convert(123, Integer.class, context));
	}

	@Test
	void testEnumConversion() throws SQLException {
		assertEquals(TestEnum.VALUE1, converter.convert("VALUE1", TestEnum.class, context));
	}

	@Test
	void testDateTimeToInstant() throws SQLException {
		long now = System.currentTimeMillis();
		Timestamp timestamp = new Timestamp(now);
		assertEquals(Instant.ofEpochMilli(now), converter.convert(timestamp, Instant.class, context));
		assertEquals(Instant.ofEpochMilli(now), converter.convert(now, Instant.class, context));
	}

	@Test
	void testDateTimeToLocalDateTime() throws SQLException {
		long now = System.currentTimeMillis();
		Timestamp timestamp = new Timestamp(now);
		assertEquals(timestamp.toLocalDateTime(), converter.convert(timestamp, LocalDateTime.class, context));
	}

	@Test
	void testDateToLocalDate() throws SQLException {
		Date date = Date.valueOf("2023-10-27");
		assertEquals(LocalDate.of(2023, 10, 27), converter.convert(date, LocalDate.class, context));
	}

	@Test
	void testTimeToLocalTime() throws SQLException {
		Time time = Time.valueOf("12:34:56");
		assertEquals(LocalTime.of(12, 34, 56), converter.convert(time, LocalTime.class, context));
	}

	@Test
	void testZonedDateTime() throws SQLException {
		long now = System.currentTimeMillis();
		Timestamp timestamp = new Timestamp(now);
		assertEquals(Instant.ofEpochMilli(now).atZone(ZoneOffset.UTC), converter.convert(timestamp, ZonedDateTime.class, context));
	}

	@Test
	void testOffsetDateTime() throws SQLException {
		long now = System.currentTimeMillis();
		Timestamp timestamp = new Timestamp(now);
		assertEquals(Instant.ofEpochMilli(now).atOffset(ZoneOffset.UTC), converter.convert(timestamp, OffsetDateTime.class, context));
	}

	@Test
	void testOffsetTime() throws SQLException {
		Time time = Time.valueOf("12:34:56");
		assertEquals(LocalTime.of(12, 34, 56).atOffset(ZoneOffset.UTC), converter.convert(time, OffsetTime.class, context));
	}

	@Test
	void testDuration() throws SQLException {
		assertEquals(Duration.ofMillis(1000), converter.convert(1000L, Duration.class, context));
	}

	@Test
	void testPeriod() throws SQLException {
		assertEquals(Period.ofMonths(12), converter.convert(12, Period.class, context));
	}

	@Test
	void testUUID() throws SQLException {
		UUID uuid = UUID.randomUUID();
		assertEquals(uuid, converter.convert(uuid.toString(), UUID.class, context));
		byte[] bytes = new byte[16];
		assertEquals(UUID.nameUUIDFromBytes(bytes), converter.convert(bytes, UUID.class, context));
	}

	@Test
	void testBigDecimalConversion() throws SQLException {
		assertEquals(BigDecimal.valueOf(1.23), converter.convert(1.23, BigDecimal.class, context));
		assertEquals(BigDecimal.valueOf(123L), converter.convert(123L, BigDecimal.class, context));
		assertEquals(BigDecimal.valueOf(123), converter.convert(123, BigDecimal.class, context));
		assertEquals(new BigDecimal("123.45"), converter.convert("123.45", BigDecimal.class, context));
		assertEquals(new BigDecimal(BigInteger.TEN), converter.convert(BigInteger.TEN, BigDecimal.class, context));
	}

	@Test
	void testBigIntegerConversion() throws SQLException {
		assertEquals(BigInteger.valueOf(123L), converter.convert(123L, BigInteger.class, context));
		assertEquals(BigInteger.valueOf(123), converter.convert(123, BigInteger.class, context));
		assertEquals(new BigInteger("123"), converter.convert("123", BigInteger.class, context));
		assertEquals(BigInteger.valueOf(123), converter.convert(new BigDecimal("123.45"), BigInteger.class, context));
	}

	@Test
	void testNumericPrimitiveConversions() throws SQLException {
		assertEquals(123L, converter.convert(123, long.class, context));
		assertEquals(123L, converter.convert("123", Long.class, context));
		assertEquals(123, converter.convert(123L, int.class, context));
		assertEquals(123, converter.convert("123", Integer.class, context));
		assertEquals(1.23, (Double) converter.convert("1.23", double.class, context), 0.001);
		assertEquals(1.23f, (Float) converter.convert("1.23", Float.class, context), 0.001f);
		assertEquals((short) 123, converter.convert(123, short.class, context));
		assertEquals((byte) 123, converter.convert(123, byte.class, context));
	}

	@Test
	void testObjectTargetType() throws SQLException {
		long now = System.currentTimeMillis();
		assertEquals(Instant.ofEpochMilli(now), converter.convert(new Timestamp(now), Object.class, context));
		assertEquals(LocalDate.of(2023, 10, 27), converter.convert(Date.valueOf("2023-10-27"), Object.class, context));
		assertEquals(LocalTime.of(12, 34, 56), converter.convert(Time.valueOf("12:34:56"), Object.class, context));

		Object[] array = new Object[]{1, 2};
		assertArrayEquals(array, (Object[]) converter.convert(array, Object.class, context));
	}

	@Test
	void testSqlArrayToList() throws SQLException {
		Array sqlArray = new MockArray(new Object[]{"a", "b"});

		assertEquals(Arrays.asList("a", "b"), converter.convert(sqlArray, List.class, context));
		assertEquals(new HashSet<>(Arrays.asList("a", "b")), converter.convert(sqlArray, Set.class, context));
	}

	@Test
	void testArrayToArray() throws SQLException {
		Object[] data = new Object[]{1, 2};

		Integer[] result = (Integer[]) converter.convert(data, Integer[].class, context);
		assertArrayEquals(new Integer[]{1, 2}, result);
	}

	@Test
	void testStructToObjectArray() throws SQLException {
		Struct struct = new MockStruct(new Object[]{"test", 123});
		Object[] result = (Object[]) converter.convert(struct, Object.class, context);
		assertArrayEquals(new Object[]{"test", 123}, result);
	}

	@Test
	void testNestedCollections() throws SQLException {
		Array innerArray = new MockArray(new Object[]{1, 2});
		Array outerArray = new MockArray(new Object[]{innerArray});

		ParameterizedType listType = new MockParameterizedType(List.class, new MockParameterizedType(List.class, Integer.class));
		List<List<Integer>> result = (List<List<Integer>>) converter.convert(outerArray, listType, context);

		assertEquals(1, result.size());
		assertEquals(Arrays.asList(1, 2), result.get(0));
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
		@Override public void free() { }
	}

	private static class MockStruct implements Struct {
		private final Object[] attributes;

		MockStruct(Object[] attributes) {
			this.attributes = attributes;
		}

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

	enum TestEnum {
		VALUE1, VALUE2
	}
}
