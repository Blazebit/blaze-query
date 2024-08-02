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

package com.blazebit.query.spi;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * An object that describes the data format of a type.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DataFormat {

    /**
     * Returns the Java type for this format.
     *
     * @return the Java type for this format
     */
    Type getType();

    /**
     * Returns the fields encapsulated by this format.
     *
     * @return the fields encapsulated by this format
     */
    List<DataFormatField> getFields();

    /**
     * Returns whether this data format represents an enum.
     *
     * @return whether this data format represents an enum.
     */
    default boolean isEnum() {
        return false;
    }

    /**
     * Returns a new data format for the given type and fields.
     *
     * @param type The type
     * @param fields The fields of the type
     * @return the data format
     */
    static DataFormat of(Type type, List<DataFormatField> fields) {
        return new DataFormat() {
            @Override
            public Type getType() {
                return type;
            }

            @Override
            public List<DataFormatField> getFields() {
                return fields;
            }
        };
    }

    /**
     * Returns a new enum data format for the given type.
     *
     * @param type The enum type
     * @return the data format
     */
    static DataFormat enumType(Type type) {
        return new DataFormat() {
            @Override
            public Type getType() {
                return type;
            }

            @Override
            public List<DataFormatField> getFields() {
                return Collections.emptyList();
            }

            @Override
            public boolean isEnum() {
                return true;
            }
        };
    }

}
