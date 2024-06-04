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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.blazebit.query.spi.DataFormatFieldAccessor;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Converter for a collection attribute of an object.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class CollectionConverter<Source, Target extends Collection<?>> extends AccessorConverter<Source, Target> {
    private final @Nullable Converter<Object, ?> elementConverter;

    /**
     * Creates a new collection converter.
     *
     * @param accessor Accessor to obtain the field value.
     * @param elementConverter Converter for element type
     */
    public CollectionConverter(DataFormatFieldAccessor accessor, @Nullable Converter<?, ?> elementConverter) {
        super(accessor);
        //noinspection unchecked
        this.elementConverter = (Converter<Object, ?>) elementConverter;
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
        Collection<Object> collection = (Collection<Object>) getAccessor().get( o );
        if (collection == null) {
            return null;
        }
        if ( elementConverter == null && collection instanceof List<?> ) {
            //noinspection unchecked
            return (Target) collection;
        }
        ArrayList<Object> arrayList = new ArrayList<>( collection.size());
        for ( Object object : collection ) {
            arrayList.add( elementConverter == null ? object : elementConverter.convert( object ) );
        }
        //noinspection unchecked
        return (Target) arrayList;
    }
}