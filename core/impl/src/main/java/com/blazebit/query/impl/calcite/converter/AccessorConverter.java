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

package com.blazebit.query.impl.calcite.converter;

import com.blazebit.query.spi.DataFormatFieldAccessor;

/**
 * Accessor for obtaining a field value from a source object.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AccessorConverter<Source, Target> implements Converter<Source, Target> {
    private final DataFormatFieldAccessor accessor;

    /**
     * Creates a converter based on an accessor.
     *
     * @param accessor The accessor to obtain the object for field
     */
    public AccessorConverter(DataFormatFieldAccessor accessor) {
        this.accessor = accessor;
    }

    /**
     * Returns the field accessor.
     *
     * @return the field accessor
     */
    public DataFormatFieldAccessor getAccessor() {
        return accessor;
    }

    @Override
    public Target convert(Source o) {
        if (o == null) {
            return null;
        }
        //noinspection unchecked
        return (Target) accessor.get( o );
    }
}