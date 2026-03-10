/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import com.blazebit.query.QueryContext;
import com.blazebit.query.QuerySession;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.TypeReference;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFormat;
import com.blazebit.query.spi.Queries;
import com.blazebit.query.spi.QueryContextBuilder;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Max Hovens
 * @since 2.3.0
 */
public class ResultTypeTest {

	@Test
	public void testInstantResultType() {
		QueryContextBuilder builder = Queries.createQueryContextBuilder();
		Instant now = Instant.now();
		builder.registerSchemaObject( Person.class, new MockDataFetcher<>( Person.class,
				List.of( new Person( "John", now ) ),
				DataFormats.beansConvention( Person.class )
		) );
		builder.registerSchemaObjectAlias( Person.class, "Person" );

		try (QueryContext context = builder.build()) {
			try (QuerySession session = context.createSession()) {
				// We expect Instant, but Calcite might return Long or Timestamp
				List<Instant> results = session.createQuery( "SELECT p.birthday FROM Person p", Instant.class ).getResultList();
				assertEquals( 1, results.size() );
				Object result = results.get( 0 );
				System.out.println( "[DEBUG_LOG] Instant result type: " + result.getClass().getName() );
				// If it's not Instant, then it's not "conventional" for a TypedQuery<Instant>
				assertTrue( result instanceof Instant, "Expected Instant, but got " + result.getClass().getName() );
			}
		}
	}

	@Test
	public void testEnumResultType() {
		QueryContextBuilder builder = Queries.createQueryContextBuilder();
		builder.registerSchemaObject( Person.class, new MockDataFetcher<>( Person.class,
				List.of( new Person( "John", Instant.now(), Gender.MALE ) ),
				DataFormats.beansConvention( Person.class )
		) );
		builder.registerSchemaObjectAlias( Person.class, "Person" );

		try (QueryContext context = builder.build()) {
			try (QuerySession session = context.createSession()) {
				List<Gender> results = session.createQuery( "SELECT p.gender FROM Person p", Gender.class ).getResultList();
				assertEquals( 1, results.size() );
				Object result = results.get( 0 );
				System.out.println( "[DEBUG_LOG] Enum result type: " + result.getClass().getName() );
				assertTrue( result instanceof Gender, "Expected Gender, but got " + result.getClass().getName() );
			}
		}
	}

	@Test
	public void testCollectionResultType() {
		QueryContextBuilder builder = Queries.createQueryContextBuilder();
		builder.registerSchemaObject( Department.class, new MockDataFetcher<>( Department.class,
				List.of( new Department( "IT", List.of( "John", "Jane" ) ) ),
				DataFormats.beansConvention( Department.class )
		) );
		builder.registerSchemaObjectAlias( Department.class, "Department" );

		try (QueryContext context = builder.build()) {
			try (QuerySession session = context.createSession()) {
				// Result type is List<String>
				TypeReference<List<String>> typeRef = new TypeReference<List<String>>() {};
				List<List<String>> results = session.createQuery( "SELECT d.employees FROM Department d", typeRef ).getResultList();
				assertEquals( 1, results.size() );
				List<String> employees = results.get( 0 );
				System.out.println( "[DEBUG_LOG] Collection result type: " + employees.getClass().getName() );
				assertEquals( List.of( "John", "Jane" ), employees );
			}
		}
	}

	@Test
	public void testMultipleSelectObjectArray() {
		QueryContextBuilder builder = Queries.createQueryContextBuilder();
		builder.registerSchemaObject( Person.class, new MockDataFetcher<>( Person.class,
				List.of( new Person( "John", Instant.now(), Gender.MALE ) ),
				DataFormats.beansConvention( Person.class )
		) );
		builder.registerSchemaObjectAlias( Person.class, "Person" );

		try (QueryContext context = builder.build()) {
			try (QuerySession session = context.createSession()) {
				// Multiple selects, we want Object[] and we want the elements to be converted too
				List<Object[]> results = session.createQuery( "SELECT p.name, p.gender, p.birthday FROM Person p", Object[].class ).getResultList();
				assertEquals( 1, results.size() );
				Object[] row = results.get( 0 );
				assertEquals( "John", row[0] );
				System.out.println( "[DEBUG_LOG] Multiple select - element 1 type: " + row[1].getClass().getName() );
				System.out.println( "[DEBUG_LOG] Multiple select - element 2 type: " + row[2].getClass().getName() );
				// Enums are still String because we don't know it's an Enum without metadata
				assertEquals( "MALE", row[1] );
				// Instant should be converted to Instant because it was a Timestamp
				assertTrue( row[2] instanceof Instant );
			}
		}
	}

	public static class Department {
		private String name;
		private List<String> employees;
		public Department(String name, List<String> employees) { this.name = name; this.employees = employees; }
		public String getName() { return name; }
		public List<String> getEmployees() { return employees; }
	}

	public enum Gender { MALE, FEMALE }

	public static class Person {
		private String name;
		private Instant birthday;
		private Gender gender;
		public Person(String name, Instant birthday) { this.name = name; this.birthday = birthday; }
		public Person(String name, Instant birthday, Gender gender) { this.name = name; this.birthday = birthday; this.gender = gender; }
		public String getName() { return name; }
		public Instant getBirthday() { return birthday; }
		public Gender getGender() { return gender; }
	}

	private static class MockDataFetcher<T> implements DataFetcher<T> {
		private final DataFormat format;
		private final List<T> data;

		MockDataFetcher(Class<T> type, List<T> data, DataFormat format) {
			this.data = data;
			this.format = format;
		}

		@Override
		public DataFormat getDataFormat() {
			return format;
		}

		@Override
		public List<T> fetch(DataFetchContext context) {
			return data;
		}
	}
}
