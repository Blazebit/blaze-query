/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.base;

import com.blazebit.query.spi.DataFormat;
import com.blazebit.query.spi.DataFormatField;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Max Hovens
 * @since 2.3.0
 */
class DataFormatsTest {

	@Test
	void testBeansConventionDiscoversGetters() {
		DataFormat format = DataFormats.beansConvention( SimpleBean.class );
		assertEquals( SimpleBean.class, format.getType() );
		Set<String> names = fieldNames( format );
		assertTrue( names.contains( "name" ) );
		assertTrue( names.contains( "age" ) );
		assertTrue( names.contains( "active" ) );
	}

	@Test
	void testBeansConventionExcludesNonAccessors() {
		DataFormat format = DataFormats.beansConvention( SimpleBean.class );
		Set<String> names = fieldNames( format );
		// toString, hashCode, class etc. should not appear
		assertFalse( names.contains( "toString" ) );
		assertFalse( names.contains( "hashCode" ) );
		assertFalse( names.contains( "class" ) );
	}

	@Test
	void testComponentMethodConventionDiscoversRecordStyle() {
		DataFormat format = DataFormats.componentMethodConvention( RecordLikeBean.class );
		Set<String> names = fieldNames( format );
		assertTrue( names.contains( "value" ) );
		assertTrue( names.contains( "label" ) );
	}

	@Test
	void testFieldsConventionDiscoversFields() {
		DataFormat format = DataFormats.fieldsConvention( SimpleBean.class );
		assertEquals( SimpleBean.class, format.getType() );
		Set<String> names = fieldNames( format );
		assertTrue( names.contains( "name" ) );
		assertTrue( names.contains( "age" ) );
		assertTrue( names.contains( "active" ) );
	}

	@Test
	void testFieldsConventionExcludesStaticAndTransient() {
		DataFormat format = DataFormats.fieldsConvention( BeanWithStaticAndTransient.class );
		Set<String> names = fieldNames( format );
		assertTrue( names.contains( "kept" ) );
		assertFalse( names.contains( "staticField" ) );
		assertFalse( names.contains( "transientField" ) );
	}

	@Test
	void testFieldsViaMethodConvention() {
		DataFormat format = DataFormats.fieldsViaMethodConvention( FieldsWithGetters.class );
		Set<String> names = fieldNames( format );
		assertTrue( names.contains( "name" ) );
		assertTrue( names.contains( "age" ) );
	}

	@Test
	void testNestedObjectProducesSubFields() {
		DataFormat format = DataFormats.beansConvention( OuterBean.class );
		DataFormatField innerField = format.getFields().stream()
				.filter( f -> f.getName().equals( "inner" ) )
				.findFirst().orElse( null );
		assertNotNull( innerField );
		DataFormat innerFormat = innerField.getFormat();
		assertNotNull( innerFormat );
		Set<String> innerNames = fieldNames( innerFormat );
		assertTrue( innerNames.contains( "name" ) );
	}

	@Test
	void testCollectionFieldProducesCollectionFormat() {
		DataFormat format = DataFormats.beansConvention( BeanWithCollection.class );
		DataFormatField listField = format.getFields().stream()
				.filter( f -> f.getName().equals( "items" ) )
				.findFirst().orElse( null );
		assertNotNull( listField );
		assertNotNull( listField.getFormat() );
	}

	@Test
	void testMapFieldProducesMapFormat() {
		DataFormat format = DataFormats.beansConvention( BeanWithMap.class );
		DataFormatField mapField = format.getFields().stream()
				.filter( f -> f.getName().equals( "entries" ) )
				.findFirst().orElse( null );
		assertNotNull( mapField );
		assertNotNull( mapField.getFormat() );
	}

	@Test
	void testSubFilterExcludesAttributes() {
		ConventionContext filterAge = new ConventionContext() {
			@Override
			public ConventionContext getSubFilter(Class<?> concreteClass, java.lang.reflect.Member member) {
				if ( member.getName().equals( "getAge" ) || member.getName().equals( "age" ) ) {
					return null; // exclude
				}
				return this;
			}
		};
		DataFormat format = DataFormats.beansConvention( SimpleBean.class, filterAge );
		Set<String> names = fieldNames( format );
		assertTrue( names.contains( "name" ) );
		assertFalse( names.contains( "age" ) );
	}

	@Test
	void testAccessorReturnsCorrectValues() {
		DataFormat format = DataFormats.beansConvention( SimpleBean.class );
		SimpleBean bean = new SimpleBean( "Bob", 25, true );
		for ( DataFormatField field : format.getFields() ) {
			Object value = field.getAccessor().get( bean );
			switch ( field.getName() ) {
				case "name" -> assertEquals( "Bob", value );
				case "age" -> assertEquals( 25, value );
				case "active" -> assertEquals( true, value );
			}
		}
	}

	private Set<String> fieldNames(DataFormat format) {
		return format.getFields().stream()
				.map( DataFormatField::getName )
				.collect( Collectors.toSet() );
	}

	public static class SimpleBean {
		private final String name;
		private final int age;
		private final boolean active;

		public SimpleBean(String name, int age, boolean active) {
			this.name = name;
			this.age = age;
			this.active = active;
		}

		public String getName() { return name; }
		public int getAge() { return age; }
		public boolean isActive() { return active; }
	}

	public static class RecordLikeBean {
		private final String value;
		private final String label;

		public RecordLikeBean(String value, String label) {
			this.value = value;
			this.label = label;
		}

		public String value() { return value; }
		public String label() { return label; }
	}

	public static class FieldsWithGetters {
		private final String name;
		private final int age;

		public FieldsWithGetters(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public String getName() { return name; }
		public int getAge() { return age; }
	}

	public static class BeanWithStaticAndTransient {
		public static String staticField = "static";
		public transient String transientField = "transient";
		public String kept = "kept";
	}

	public static class OuterBean {
		private final InnerBean inner;

		public OuterBean(InnerBean inner) { this.inner = inner; }
		public InnerBean getInner() { return inner; }
	}

	public static class InnerBean {
		private final String name;

		public InnerBean(String name) { this.name = name; }
		public String getName() { return name; }
	}

	public static class BeanWithCollection {
		private final List<String> items;

		public BeanWithCollection(List<String> items) { this.items = items; }
		public List<String> getItems() { return items; }
	}

	public static class BeanWithMap {
		private final Map<String, Integer> entries;

		public BeanWithMap(Map<String, Integer> entries) { this.entries = entries; }
		public Map<String, Integer> getEntries() { return entries; }
	}
}
