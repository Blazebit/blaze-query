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

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.File;
import java.io.IOException;

/**
 * The TestQueryObject class provides functionality for creating objects from testing Blaze
 * Queries.
 * <p>
 * An example of how to use the {@code createObject} method:
 * <pre>{@code
 * ClassName object = TestQueryObject.create(ClassName.class, "path/to/json/file.json");
 * }</pre>
 * </p>
 * <p>
 * Note that the provided JSON file should be compatible with the specified class, otherwise an
 * exception may occur.
 * </p>
 *
 * @author Max Hovens
 * @since 1.0.0
 * @see Serialization
 */
public final class TestQueryObject {

    private TestQueryObject() {
    }

    /**
     * Reads a JSON file and deserializes it into an object of the specified class.
     *
     * @param <T>      the type of the object to create
     * @param clazz    the class of the object to create
     * @param jsonFile the path to the JSON file
     * @return an instance of the specified class deserialized from the JSON file
     * @throws RuntimeException if an I/O error occurs while reading the JSON file
     * @see Serialization
     */
    public static <T> T fromJson(Class<T> clazz, File jsonFile) {
        try {
            return Serialization.getObjectMapper().readValue(jsonFile, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Reads a JSON string and deserializes it into an object of the specified class.
     *
     * @param <T>   the type of the object to create
     * @param clazz the class of the object to create
     * @param json  the JSON string to deserialize
     * @return an instance of the specified class deserialized from the JSON string
     * @throws RuntimeException if an error occurs while parsing the JSON string
     * @see Serialization
     */
    public static <T> T fromJson(Class<T> clazz, String json) {
        try {
            return Serialization.getObjectMapper().readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
