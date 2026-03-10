/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import com.blazebit.query.spi.TypeConverter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.SQLException;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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
	void testObjectTargetUnwrapsSqlScalars() throws SQLException {
		long now = System.currentTimeMillis();
		assertEquals(Instant.ofEpochMilli(now), converter.convert(new Timestamp(now), Object.class, context));
		assertEquals(LocalDate.of(2023, 10, 27), converter.convert(Date.valueOf("2023-10-27"), Object.class, context));
		assertEquals(LocalTime.of(12, 34, 56), converter.convert(Time.valueOf("12:34:56"), Object.class, context));
	}

	@Test
	void testObjectTargetPassesThroughNonSqlTypes() throws SQLException {
		assertSame("hello", converter.convert("hello", Object.class, context));
		assertEquals(42, converter.convert(42, Object.class, context));
	}

	enum TestEnum {
		VALUE1, VALUE2
	}
}
