/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.base;

import com.blazebit.query.spi.DataFormatFieldAccessor;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Max Hovens
 * @since 2.3.0
 */
class AccessorTest {

	@Test
	void testMethodFieldAccessor() throws Exception {
		Method method = SampleBean.class.getMethod( "getName" );
		MethodFieldAccessor accessor = new MethodFieldAccessor( method );
		assertEquals( "Alice", accessor.get( new SampleBean( "Alice", 30 ) ) );
		assertEquals( method, accessor.getMethod() );
	}

	@Test
	void testMethodFieldAccessorThrowsOnError() throws Exception {
		Method method = ThrowingBean.class.getMethod( "getValue" );
		MethodFieldAccessor accessor = new MethodFieldAccessor( method );
		assertThrows( RuntimeException.class, () -> accessor.get( new ThrowingBean() ) );
	}

	@Test
	void testLaxMethodFieldAccessorReturnsNullOnError() throws Exception {
		Method method = ThrowingBean.class.getMethod( "getValue" );
		LaxMethodFieldAccessor accessor = new LaxMethodFieldAccessor( method );
		assertNull( accessor.get( new ThrowingBean() ) );
	}

	@Test
	void testLaxMethodFieldAccessorReturnsValueOnSuccess() throws Exception {
		Method method = SampleBean.class.getMethod( "getName" );
		LaxMethodFieldAccessor accessor = new LaxMethodFieldAccessor( method );
		assertEquals( "Alice", accessor.get( new SampleBean( "Alice", 30 ) ) );
	}

	@Test
	void testFieldFieldAccessor() throws Exception {
		Field field = SampleBean.class.getDeclaredField( "name" );
		field.setAccessible( true );
		FieldFieldAccessor accessor = new FieldFieldAccessor( field );
		assertEquals( "Alice", accessor.get( new SampleBean( "Alice", 30 ) ) );
		assertEquals( field, accessor.getField() );
	}

	@Test
	void testConvertingFieldAccessorConvertsValue() throws Exception {
		Method getter = SampleBean.class.getMethod( "getAge" );
		Method converter = String.class.getMethod( "valueOf", int.class );
		DataFormatFieldAccessor delegate = new MethodFieldAccessor( getter );
		ConvertingFieldAccessor accessor = new ConvertingFieldAccessor( delegate, converter );
		assertEquals( "30", accessor.get( new SampleBean( "Alice", 30 ) ) );
		assertEquals( delegate, accessor.getDelegate() );
		assertEquals( converter, accessor.getConverterMethod() );
	}

	@Test
	void testConvertingFieldAccessorReturnsNullForNullDelegate() throws Exception {
		Method getter = SampleBean.class.getMethod( "getName" );
		Method converter = String.class.getMethod( "valueOf", Object.class );
		DataFormatFieldAccessor delegate = new MethodFieldAccessor( getter );
		ConvertingFieldAccessor accessor = new ConvertingFieldAccessor( delegate, converter );
		assertNull( accessor.get( new SampleBean( null, 0 ) ) );
	}

	public static class SampleBean {
		private final String name;
		private final int age;

		public SampleBean(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public int getAge() {
			return age;
		}
	}

	public static class ThrowingBean {
		public String getValue() {
			throw new UnsupportedOperationException( "broken" );
		}
	}
}
