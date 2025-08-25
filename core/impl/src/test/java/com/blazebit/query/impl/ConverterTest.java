/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import java.lang.reflect.Member;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import com.blazebit.query.QueryContext;
import com.blazebit.query.QuerySession;
import com.blazebit.query.TypedQuery;
import com.blazebit.query.connector.base.ConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFormat;
import com.blazebit.query.spi.Queries;
import com.blazebit.query.spi.QueryContextBuilder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ConverterTest {

	@Test
	public void testConverters() {
		Model model = new Model();
		model.theInstant = Instant.parse( "2000-01-01T12:12:12.123456789Z" );
		model.theZonedDateTime = ZonedDateTime.of(
				LocalDateTime.ofInstant( model.theInstant, ZoneOffset.UTC ),
				ZoneId.of( "America/Los_Angeles" )
		);
		model.theOffsetDateTime = OffsetDateTime.of(
				LocalDateTime.ofInstant( model.theInstant, ZoneOffset.UTC ),
				ZoneOffset.ofHoursMinutes( 1, 30 )
		);
		model.theOffsetTime = OffsetTime.of( 12, 12, 12, 123456789, ZoneOffset.ofHoursMinutes( 1, 30 ) );
		model.theLocalDate = LocalDate.ofInstant( model.theInstant, ZoneOffset.UTC );
		model.theLocalDateTime = LocalDateTime.ofInstant( model.theInstant, ZoneOffset.UTC );
		model.theLocalTime = LocalTime.ofInstant( model.theInstant, ZoneOffset.UTC );
		model.theDuration = Duration.ofDays( 1 )
				.plusHours( 9 )
				.plusMinutes( 8 )
				.plusSeconds( 7 )
				.plusNanos( 123456789L );
		model.thePeriod = Period.ofYears( 1 ).plusMonths( 5 );
		model.theUuid = UUID.fromString( "53886a8a-7082-4879-b430-25cb94415be8" );
		model.theEnum = MyEnum.VALUE1;
		model.theCustomEnum = MyCustomEnum.VALUE1;
		QueryContextBuilder queryContextBuilder = Queries.createQueryContextBuilder();
		queryContextBuilder.registerSchemaObject( Model.class, new DataFetcher<>() {
			@Override
			public DataFormat getDataFormat() {
				return DataFormats.fieldsConvention( Model.class, new ConventionContext() {
					@Override
					public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
						return this;
					}

					@Override
					public boolean isEnumType(Class<?> typeClass) {
						return typeClass == MyCustomEnum.class || ConventionContext.super.isEnumType( typeClass );
					}
				} );
			}

			@Override
			public List<Model> fetch(DataFetchContext context) {
				return List.of( model );
			}
		} );
		queryContextBuilder.registerSchemaObjectAlias( Model.class, "Model" );
		try (QueryContext queryContext = queryContextBuilder.build()) {
			try (QuerySession session = queryContext.createSession()) {
				TypedQuery<Object[]> query = session.createQuery(
						"select m.* " +
								"from Model m " +
								"where m.theInstant = timestamp with time zone '2000-01-01 12:12:12.123456789 GMT'" +
								// Time zone handling is broken in Apache Calcite, so we have to adjust the time manually
								"  and m.theZonedDateTime = timestamp with time zone '2000-01-01 20:12:12.123456789 GMT'" +
//								"  and m.theZonedDateTime = timestamp with time zone '2000-01-01 12:12:12.123456789 America/Los_Angeles'" +
								"  and m.theOffsetDateTime = timestamp with time zone '2000-01-01 10:42:12.123456789 GMT'" +
//								"  and m.theOffsetDateTime = timestamp with time zone '2000-01-01 12:12:12.123456789 GMT+01:30'" +
								// Time with time zone handling is completely broken, so use local time
								"  and m.theOffsetTime = time '10:42:12.123456789'" +
//								"  and m.theOffsetTime = time with time zone '12:12:12 GMT+01:30'" + // Fractional seconds support is broken in calcite parser, also, offset/zone is not retained
								"  and m.theLocalDate = date '2000-01-01'" +
								"  and m.theLocalDateTime = timestamp '2000-01-01 12:12:12.123456789'" +
								"  and m.theLocalTime = time '12:12:12.123456789'" +
								// Calcite only supports intervals up to microseconds
								"  and m.theDuration = interval '1 9:8:7.123456' day to second" +
//								"  and m.theDuration = interval '1 9:8:7.123456789' day to second" +
								"  and m.thePeriod = interval '1-5' year to month" +
//								"  and m.theUuid = uuid '53886a8a-7082-4879-b430-25cb94415be8'" +
								"  and m.theEnum = 'VALUE1'" +
								"  and m.theCustomEnum = 'VALUE1'"
				);
				List<Object[]> result = query.getResultList();
				assertEquals( 1, result.size() );
			}
		}
	}

	public static class Model {
		public Object object;
		public Instant theInstant;
		public ZonedDateTime theZonedDateTime;
		public OffsetDateTime theOffsetDateTime;
		public OffsetTime theOffsetTime;
		public LocalDate theLocalDate;
		public LocalDateTime theLocalDateTime;
		public LocalTime theLocalTime;
		public Duration theDuration;
		public Period thePeriod;
		public UUID theUuid;
		public MyEnum theEnum;
		public MyCustomEnum theCustomEnum;
	}

	public enum MyEnum {
		VALUE1,
		VALUE2
	}

	public static class MyCustomEnum {
		public static final MyCustomEnum VALUE1 = new MyCustomEnum( "VALUE1" );
		public static final MyCustomEnum VALUE2 = new MyCustomEnum( "VALUE2" );

		private final String value;

		public MyCustomEnum(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}
}
