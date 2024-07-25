/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.query.testing;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * The Serialization class provides functionality related to object serialization and
 * deserialization using JSON format.
 *
 * <p>
 * This class provides a singleton instance of {@link ObjectMapper} for reading and writing JSON. It
 * is configured to handle serialization and deserialization of objects. The class also registers
 * any available modules for better serialization support.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * ObjectMapper mapper = Serialization.getObjectMapper();
 * MyClass myObject = mapper.readValue(jsonString, MyClass.class);
 * String json = mapper.writeValueAsString(myObject);
 * }</pre>
 * </p>
 *
 * @author Max Hovens
 * @since 1.0.0
 * @see ObjectMapper
 * @see com.fasterxml.jackson.databind.module.SimpleModule
 */
public final class Serialization {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new JsonMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.findAndRegisterModules();
    }

    private Serialization() {
    }

    /**
     * Returns the singleton instance of {@link ObjectMapper} used for JSON serialization and
     * deserialization.
     *
     * <p>
     * The {@code getObjectMapper} method provides a central access point to the
     * {@link ObjectMapper} instance used throughout the application for handling JSON. The
     * ObjectMapper is pre-configured to handle serialization and deserialization of objects and is
     * registered with any available modules for better serialization support.
     * </p>
     *
     * <p>
     * Example usage:
     * <pre>{@code
     * ObjectMapper mapper = Serialization.getObjectMapper();
     * MyClass myObject = mapper.readValue(jsonString, MyClass.class);
     * String json = mapper.writeValueAsString(myObject);
     * }</pre>
     * </p>
     *
     * @return the singleton instance of {@link ObjectMapper}
     * @see ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
