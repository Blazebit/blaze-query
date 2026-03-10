/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
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

/**
 * Tests scalar type conversion through the full QueryContextImpl conversion chain.
 * Composite type normalization (Struct, Array) is tested through
 * {@link ResultValueNormalizerTest}, {@link AvaticaTypeExtractionTest},
 * and {@link CalciteIntegrationTest}.
 *
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
}
