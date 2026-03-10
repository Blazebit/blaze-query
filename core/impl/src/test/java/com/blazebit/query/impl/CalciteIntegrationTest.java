/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import com.blazebit.query.QueryContext;
import com.blazebit.query.QuerySession;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFormat;
import com.blazebit.query.spi.Queries;
import com.blazebit.query.spi.QueryContextBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalciteIntegrationTest {

	@Test
	public void testNestedObjectAccess() {
		QueryContextBuilder builder = Queries.createQueryContextBuilder();
		builder.registerSchemaObject( Person.class, new MockDataFetcher<>( Person.class,
				List.of( new Person( "John", new Address( "Main St", 123 ) ) ),
				DataFormats.beansConvention( Person.class )
		) );
		builder.registerSchemaObjectAlias( Person.class, "Person" );

		try (QueryContext context = builder.build()) {
			try (QuerySession session = context.createSession()) {
				// Accessing nested property
				List<Object[]> results = session.createQuery( "SELECT p.name, p.address.street, p.address.number FROM Person p", Object[].class ).getResultList();
				assertEquals( 1, results.size() );
				assertEquals( "John", results.get( 0 )[0] );
				assertEquals( "Main St", results.get( 0 )[1] );
				assertEquals( 123, results.get( 0 )[2] );
			}
		}
	}

	@Test
	public void testCollectionAccess() throws java.sql.SQLException {
		QueryContextBuilder builder = Queries.createQueryContextBuilder();
		builder.registerSchemaObject( Department.class, new MockDataFetcher<>( Department.class,
				List.of( new Department( "IT", List.of( "John", "Jane" ) ) ),
				DataFormats.beansConvention( Department.class )
		) );
		builder.registerSchemaObjectAlias( Department.class, "Department" );

		try (QueryContext context = builder.build()) {
			try (QuerySession session = context.createSession()) {
				// In Calcite, collections are often accessed via UNNEST or similar,
				// but here we just check if we can select the collection itself
				List<Object[]> results = session.createQuery( "SELECT d.name, d.employees FROM Department d", Object[].class ).getResultList();
				assertEquals( 1, results.size() );
				assertEquals( "IT", results.get( 0 )[0] );
				Object employees = results.get( 0 )[1];
				assertTrue( employees instanceof List );
				assertEquals( List.of( "John", "Jane" ), employees );
			}
		}
	}

	@Test
	public void testMapAccess() {
		QueryContextBuilder builder = Queries.createQueryContextBuilder();
		builder.registerSchemaObject( Config.class, new MockDataFetcher<>( Config.class,
				List.of( new Config( "App", Map.of( "key1", "value1" ) ) ),
				DataFormats.beansConvention( Config.class )
		) );
		builder.registerSchemaObjectAlias( Config.class, "Config" );

		try (QueryContext context = builder.build()) {
			try (QuerySession session = context.createSession()) {
				// Accessing map
				List<Object[]> results = session.createQuery( "SELECT c.name, c.settings['key1'] FROM Config c", Object[].class ).getResultList();
				assertEquals( 1, results.size() );
				assertEquals( "App", results.get( 0 )[0] );
				assertEquals( "value1", results.get( 0 )[1] );
			}
		}
	}

	@Test
	public void testNestedCollectionAccess() throws java.sql.SQLException {
		QueryContextBuilder builder = Queries.createQueryContextBuilder();
		builder.registerSchemaObject( Company.class, new MockDataFetcher<>( Company.class,
				List.of( new Company( "Acme", List.of( new Department( "IT", List.of( "John" ) ) ) ) ),
				DataFormats.beansConvention( Company.class )
		) );
		builder.registerSchemaObjectAlias( Company.class, "Company" );

		try (QueryContext context = builder.build()) {
			try (QuerySession session = context.createSession()) {
				// Accessing nested collection
				List<Object[]> results = session.createQuery( "SELECT c.name, c.departments FROM Company c", Object[].class ).getResultList();
				assertEquals( 1, results.size() );
				assertEquals( "Acme", results.get( 0 )[0] );
				Object departments = results.get( 0 )[1];
				assertTrue( departments instanceof List );
				List<Object[]> deptList = (List<Object[]>) departments;
				assertEquals( 1, deptList.size() );
				Object[] itDept = deptList.get( 0 );
				// Department has: employees, name
				assertEquals( "IT", itDept[1] );
				assertTrue( itDept[0] instanceof List );
			}
		}
	}

	public static class Company {
		private String name;
		private List<Department> departments;
		public Company(String name, List<Department> departments) { this.name = name; this.departments = departments; }
		public String getName() { return name; }
		public List<Department> getDepartments() { return departments; }
	}

	private void assertTrue(boolean condition) {
		if (!condition) throw new AssertionError();
	}

	public static class Person {
		private String name;
		private Address address;
		public Person(String name, Address address) { this.name = name; this.address = address; }
		public String getName() { return name; }
		public Address getAddress() { return address; }
	}

	public static class Address {
		private String street;
		private int number;
		public Address(String street, int number) { this.street = street; this.number = number; }
		public String getStreet() { return street; }
		public int getNumber() { return number; }
	}

	public static class Department {
		private String name;
		private List<String> employees;
		public Department(String name, List<String> employees) { this.name = name; this.employees = employees; }
		public String getName() { return name; }
		public List<String> getEmployees() { return employees; }
	}

	public static class Config {
		private String name;
		private Map<String, String> settings;
		public Config(String name, Map<String, String> settings) { this.name = name; this.settings = settings; }
		public String getName() { return name; }
		public Map<String, String> getSettings() { return settings; }
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
