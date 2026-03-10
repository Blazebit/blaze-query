/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import org.junit.jupiter.api.Test;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link ResultValueNormalizer} without Avatica metadata.
 * With-metadata (Struct to Map) conversion is tested through
 * {@link AvaticaTypeExtractionTest} and {@link CalciteIntegrationTest}.
 *
 * @author Max Hovens
 * @since 2.2.0
 */
class ResultValueNormalizerTest {

	private final ResultValueNormalizer normalizer = ResultValueNormalizer.create(
			null, value -> DefaultTypeConverter.INSTANCE.convert( value, Object.class,
					(v, t) -> DefaultTypeConverter.INSTANCE.convert( v, t, (v2, t2) -> v2 ) ) );

	@Test
	void testNullValue() throws SQLException {
		assertNull( normalizer.normalizeValue( null, null ) );
	}

	@Test
	void testScalarPassthrough() throws SQLException {
		assertEquals( "hello", normalizer.normalizeValue( "hello", null ) );
		assertEquals( 42, normalizer.normalizeValue( 42, null ) );
	}

	@Test
	void testScalarSqlUnwrapping() throws SQLException {
		long now = System.currentTimeMillis();
		assertEquals( Instant.ofEpochMilli( now ),
				normalizer.normalizeValue( new Timestamp( now ), null ) );
	}

	@Test
	void testStructToObjectArray() throws SQLException {
		Struct struct = new MockStruct( new Object[]{"test", 123} );
		Object result = normalizer.normalizeValue( struct, null );
		assertTrue( result instanceof Object[] );
		assertArrayEquals( new Object[]{"test", 123}, (Object[]) result );
	}

	@Test
	void testStructWithNestedSqlTypes() throws SQLException {
		long now = System.currentTimeMillis();
		Struct struct = new MockStruct( new Object[]{"test", new Timestamp( now )} );
		Object[] result = (Object[]) normalizer.normalizeValue( struct, null );
		assertEquals( "test", result[0] );
		assertEquals( Instant.ofEpochMilli( now ), result[1] );
	}

	@Test
	void testSqlArrayToList() throws SQLException {
		Array sqlArray = new MockArray( new Object[]{"a", "b"} );
		Object result = normalizer.normalizeValue( sqlArray, null );
		assertEquals( List.of( "a", "b" ), result );
	}

	@Test
	void testSqlArrayWithNestedSqlTypes() throws SQLException {
		long now = System.currentTimeMillis();
		Array sqlArray = new MockArray( new Object[]{new Timestamp( now )} );
		List<?> result = (List<?>) normalizer.normalizeValue( sqlArray, null );
		assertEquals( 1, result.size() );
		assertEquals( Instant.ofEpochMilli( now ), result.get( 0 ) );
	}

	@Test
	void testNestedArrays() throws SQLException {
		Array inner = new MockArray( new Object[]{1, 2} );
		Array outer = new MockArray( new Object[]{inner} );
		List<?> result = (List<?>) normalizer.normalizeValue( outer, null );
		assertEquals( 1, result.size() );
		assertEquals( List.of( 1, 2 ), result.get( 0 ) );
	}

	@Test
	void testObjectArrayNormalization() throws SQLException {
		long now = System.currentTimeMillis();
		Object[] data = new Object[]{"test", new Timestamp( now )};
		Object[] result = (Object[]) normalizer.normalizeValue( data, null );
		assertEquals( "test", result[0] );
		assertEquals( Instant.ofEpochMilli( now ), result[1] );
	}

	@Test
	void testArrayOfStructs() throws SQLException {
		Struct s1 = new MockStruct( new Object[]{"a", 1} );
		Struct s2 = new MockStruct( new Object[]{"b", 2} );
		Array sqlArray = new MockArray( new Object[]{s1, s2} );
		List<?> result = (List<?>) normalizer.normalizeValue( sqlArray, null );
		assertEquals( 2, result.size() );
		// Without metadata, structs become Object[]
		assertArrayEquals( new Object[]{"a", 1}, (Object[]) result.get( 0 ) );
		assertArrayEquals( new Object[]{"b", 2}, (Object[]) result.get( 1 ) );
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
		MockStruct(Object[] attributes) { this.attributes = attributes; }
		@Override public String getSQLTypeName() { return null; }
		@Override public Object[] getAttributes() { return attributes; }
		@Override public Object[] getAttributes(Map<String, Class<?>> map) { return attributes; }
	}
}
