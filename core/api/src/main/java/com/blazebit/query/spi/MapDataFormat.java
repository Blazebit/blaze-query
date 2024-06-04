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
 * An object that describes the data format of a map type.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface MapDataFormat extends DataFormat {

    /**
     * Returns the Java type for this format.
     *
     * @return the Java type for this format
     */
    Type getType();

    /**
     * Returns the format of the map key.
     *
     * @return the format of the map key
     */
    DataFormat getKeyFormat();

    /**
     * Returns the format of the map element.
     *
     * @return the format of the map element
     */
    DataFormat getElementFormat();

    /**
     * Returns a new map data format.
     *
     * @param type The collection type
     * @param keyFormat The key format
     * @param elementFormat The element format
     * @return the map data format
     */
    static MapDataFormat of(Type type, DataFormat keyFormat, DataFormat elementFormat) {
        return new MapDataFormat() {
            @Override
            public Type getType() {
                return type;
            }

            @Override
            public List<DataFormatField> getFields() {
                return Collections.emptyList();
            }

            @Override
            public DataFormat getKeyFormat() {
                return keyFormat;
            }

            @Override
            public DataFormat getElementFormat() {
                return elementFormat;
            }
        };
    }

}
