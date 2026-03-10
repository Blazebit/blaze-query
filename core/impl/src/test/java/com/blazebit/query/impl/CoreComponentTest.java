/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import com.blazebit.query.QueryContext;
import com.blazebit.query.QuerySession;
import com.blazebit.query.metamodel.Metamodel;
import com.blazebit.query.metamodel.SchemaObjectType;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFormat;
import com.blazebit.query.spi.Queries;
import com.blazebit.query.spi.QueryContextBuilder;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Max Hovens
 * @since 2.2.0
 */
public class CoreComponentTest {

	@Test
	public void testMetamodelBuilding() {
		QueryContextBuilder builder = Queries.createQueryContextBuilder();
		builder.registerSchemaObject( String.class, new MockDataFetcher<>( String.class ) );
		builder.registerSchemaObjectAlias( String.class, "MyString" );

		try (QueryContext context = builder.build()) {
			Metamodel metamodel = context.getMetamodel();
			assertNotNull( metamodel );

			SchemaObjectType<String> typeByClass = metamodel.get( String.class );
			assertNotNull( typeByClass );
			assertEquals( String.class, typeByClass.getType() );

			SchemaObjectType<String> typeByAlias = metamodel.get( "MyString" );
			assertSame( typeByClass, typeByAlias );

			SchemaObjectType<String> typeByFqn = metamodel.get( String.class.getCanonicalName() );
			assertSame( typeByClass, typeByFqn );

			Set<SchemaObjectType<?>> types = metamodel.types();
			assertTrue( types.contains( typeByClass ) );

			assertThrows( IllegalArgumentException.class, () -> metamodel.get( Integer.class ) );
			assertThrows( IllegalArgumentException.class, () -> metamodel.get( "NonExistent" ) );
		}
	}

	@Test
	public void testQueryLifecycle() {
		QueryContextBuilder builder = Queries.createQueryContextBuilder();
		builder.setProperty( "test.prop", "value" );

		try (QueryContext context = builder.build()) {
			assertTrue( context.isOpen() );

			try (QuerySession session = context.createSession( Collections.singletonMap( "session.prop", "sessionValue" ) )) {
				assertTrue( session.isOpen() );

				assertEquals( "value", ((DataFetchContext) session).findProperty( "test.prop" ) );
				assertEquals( "sessionValue", ((DataFetchContext) session).findProperty( "session.prop" ) );

				session.setProperty( "dynamic.prop", "dynamicValue" );
				assertEquals( "dynamicValue", ((DataFetchContext) session).findProperty( "dynamic.prop" ) );
			}
			// session is closed now
		}
		// context is closed now
	}

	@Test
	public void testQuerySessionClosedBehavior() {
		QueryContextBuilder builder = Queries.createQueryContextBuilder();
		QueryContext context = builder.build();
		QuerySession session = context.createSession();
		session.close();

		assertThrows( IllegalStateException.class, () -> session.createQuery( "SELECT 1", Object.class ) );
		assertThrows( IllegalStateException.class, () -> session.get( String.class ) );
		assertThrows( IllegalStateException.class, () -> session.put( String.class, Collections.emptyList() ) );

		context.close();
		assertThrows( IllegalStateException.class, context::createSession );
	}

	@Test
	public void testDataFetcherInteraction() {
		QueryContextBuilder builder = Queries.createQueryContextBuilder();
		MockDataFetcher<Person> fetcher = new MockDataFetcher<>( Person.class,
				List.of( new Person( "John", 30 ), new Person( "Jane", 25 ) ),
				DataFormats.componentMethodConvention( Person.class )
		);
		builder.registerSchemaObject( Person.class, fetcher );
		builder.registerSchemaObjectAlias( Person.class, "Person" );

		try (QueryContext context = builder.build()) {
			try (QuerySession session = context.createSession()) {
				List<? extends Person> persons = session.getOrFetch( Person.class );
				assertEquals( 2, persons.size() );
				assertEquals( "John", persons.get( 0 ).name() );

				// Second call should return cached data
				List<? extends Person> personsCached = session.getOrFetch( Person.class );
				assertSame( persons, personsCached );
				assertEquals( 1, fetcher.fetchCount );
			}
		}
	}

	@Test
	public void testQueryExecution() {
		QueryContextBuilder builder = Queries.createQueryContextBuilder();
		builder.registerSchemaObject( Person.class, new MockDataFetcher<>( Person.class,
				List.of( new Person( "John", 30 ), new Person( "Jane", 25 ) ),
				DataFormats.componentMethodConvention( Person.class )
		) );
		builder.registerSchemaObjectAlias( Person.class, "Person" );

		try (QueryContext context = builder.build()) {
			try (QuerySession session = context.createSession()) {
				List<Object[]> results = session.createQuery( "SELECT * FROM Person p WHERE p.age > 28", Object[].class ).getResultList();
				assertEquals( 1, results.size() );
				assertEquals( 30, results.get( 0 )[0] );
				assertEquals( "John", results.get( 0 )[1] );
			}
		}
	}

	private static class MockDataFetcher<T> implements DataFetcher<T> {
		private final DataFormat format;
		private final List<T> data;
		int fetchCount = 0;

		MockDataFetcher(Class<T> type) {
			this( type, Collections.emptyList(), DataFormat.of( type, Collections.emptyList() ) );
		}

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
			fetchCount++;
			return data;
		}
	}

	public record Person(String name, int age) {}
}
