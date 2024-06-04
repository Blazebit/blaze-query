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

/**
 * Accessor for obtaining an object array from an object, by invoking an array of accessors on it.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ObjectArrayConverter<Source> implements Converter<Source, Object[]> {
    private final Converter<Source, Object>[] converters;

    /**
     * Creates a method accessor.
     *
     * @param converters The accessors to obtain the individual attribute values.
     */
    public ObjectArrayConverter(Converter<Source, ?>[] converters) {
        //noinspection unchecked
        this.converters = (Converter<Source, Object>[]) converters;
    }

    /**
     * Returns the accessors to obtain the attribute values.
     * @return the accessors to obtain the attribute values
     */
    public Converter<Source, Object>[] getConverters() {
        return converters;
    }

    @Override
    public Object[] convert(Source o) {
        if (o == null) {
            return null;
        }
        Object[] array = new Object[converters.length];
        for ( int i = 0; i < converters.length; i++ ) {
            array[i] = converters[i].convert(o);
        }
        return array;
    }
}