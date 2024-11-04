/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.IOException;

public final class TestObject {

	private TestObject() {}

	public static <T> T fromJson(Class<T> clazz, File jsonFile) {
		try {
			return Serialization.getObjectMapper().readValue(jsonFile, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static final class Serialization {

		private static final ObjectMapper OBJECT_MAPPER;

		static {
			OBJECT_MAPPER =
					JsonMapper.builder()
							.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
							.configure( MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
							.build();
			OBJECT_MAPPER.findAndRegisterModules();
		}

		private Serialization() {}

		/**
		 * Returns the singleton instance of {@link ObjectMapper} used for JSON serialization and
		 * deserialization.
		 *
		 * <p>The {@code getObjectMapper} method provides a central access point to the {@link
		 * ObjectMapper} instance used throughout the application for handling JSON. The ObjectMapper is
		 * pre-configured to handle serialization and deserialization of objects and is registered with
		 * any available modules for better serialization support.
		 *
		 * <p>Example usage:
		 *
		 * <pre>{@code
		 * ObjectMapper mapper = Serialization.getObjectMapper();
		 * MyClass myObject = mapper.readValue(jsonString, MyClass.class);
		 * String json = mapper.writeValueAsString(myObject);
		 * }</pre>
		 *
		 * @return the singleton instance of {@link ObjectMapper}
		 * @see ObjectMapper
		 */
		public static ObjectMapper getObjectMapper() {
			return OBJECT_MAPPER;
		}
	}
}
