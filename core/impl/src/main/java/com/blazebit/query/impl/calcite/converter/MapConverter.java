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

import java.util.HashMap;
import java.util.Map;

import com.blazebit.query.spi.DataFormatFieldAccessor;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Converter for a map attribute of an object.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class MapConverter<Source, Target extends Map<?, ?>> extends AccessorConverter<Source, Target> {
    private final @Nullable Converter<Object, ?> keyConverter;
    private final @Nullable Converter<Object, ?> elementConverter;

    /**
     * Creates a new map converter.
     *
     * @param accessor Accessor to obtain the field value
     * @param keyConverter Converter for key type
     * @param elementConverter Converter for element type
     */
    public MapConverter(DataFormatFieldAccessor accessor, @Nullable Converter<?, ?> keyConverter, @Nullable Converter<?, ?> elementConverter) {
        super( accessor );
        //noinspection unchecked
        this.keyConverter = (Converter<Object, ?>) keyConverter;
        //noinspection unchecked
        this.elementConverter = (Converter<Object, ?>) elementConverter;
    }

    /**
     * Returns the key accessor to obtain the key object.
     * @return the key accessor to obtain the key object
     */
    public @Nullable Converter<?, ?> getKeyAccessor() {
        return keyConverter;
    }

    /**
     * Returns the element accessor to obtain the element object.
     * @return the element accessor to obtain the element object
     */
    public @Nullable Converter<?, ?> getElementAccessor() {
        return elementConverter;
    }

    @Override
    public Target convert(Source o) {
        if (o == null) {
            return null;
        }
        //noinspection unchecked
        Map<Object, Object> map = (Map<Object, Object>) getAccessor().get( o );
        if (map == null) {
            return null;
        }
        if ( keyConverter == null && elementConverter == null ) {
            //noinspection unchecked
            return (Target) map;
        }
        Map<Object, Object> newMap = new HashMap<>();
        for ( Map.Entry<Object, Object> entry : map.entrySet() ) {
            Object key = keyConverter == null ? entry.getKey() : keyConverter.convert( entry.getKey() );
            Object value = elementConverter == null ? entry.getValue() : elementConverter.convert( entry.getValue() );
            newMap.put( key, value );
        }
        //noinspection unchecked
        return (Target) newMap;
    }
}