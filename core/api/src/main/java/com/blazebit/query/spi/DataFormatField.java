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

/**
 * An object to fetch schema object data.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DataFormatField {

    /**
     * Returns the name of the field.
     *
     * @return the name of the field
     */
    String getName();

    /**
     * Returns the accessor for the field.
     *
     * @return the accessor for the field
     */
    DataFormatFieldAccessor getAccessor();

    /**
     * Returns the format of the field type.
     *
     * @return the format of the field type
     */
    DataFormat getFormat();

    /**
     * Creates a new data format field.
     *
     * @param name The name of the field
     * @param accessor The accessor for the field
     * @param format The data format of the field type
     * @return The new data format field
     */
    static DataFormatField of(String name, DataFormatFieldAccessor accessor, DataFormat format) {
        return new DataFormatField() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public DataFormatFieldAccessor getAccessor() {
                return accessor;
            }

            @Override
            public DataFormat getFormat() {
                return format;
            }
        };
    }


}
