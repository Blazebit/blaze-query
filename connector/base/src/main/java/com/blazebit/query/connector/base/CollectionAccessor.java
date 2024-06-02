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

package com.blazebit.query.connector.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Accessor for a collection attribute of an object.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class CollectionAccessor implements Accessor {
    private final Accessor baseAccessor;
    private final @Nullable Accessor elementAccessor;

    /**
     * Creates a new collection accessor.
     *
     * @param baseAccessor Accessor to obtain a collection.
     * @param elementAccessor Accessor to obtain an element.
     */
    public CollectionAccessor(Accessor baseAccessor, @Nullable Accessor elementAccessor) {
        this.baseAccessor = baseAccessor;
        this.elementAccessor = elementAccessor;
    }

    /**
     * Returns the base accessor to obtain the collection.
     * @return the base accessor to obtain the collection
     */
    public Accessor getBaseAccessor() {
        return baseAccessor;
    }

    /**
     * Returns the element accessor to obtain the element object.
     * @return the element accessor to obtain the element object
     */
    public @Nullable Accessor getElementAccessor() {
        return elementAccessor;
    }

    @Override
    public String getAttributePath() {
        return baseAccessor.getAttributePath();
    }

    @Override
    public Object getValue(Object o) {
        //noinspection unchecked
        Collection<Object> collection = (Collection<Object>) baseAccessor.getValue( o );
        if (collection == null) {
            return null;
        }
        if ( elementAccessor == null && collection instanceof List<?> ) {
            return collection;
        }
        ArrayList<Object> arrayList = new ArrayList<>( collection.size());
        for ( Object object : collection ) {
            arrayList.add( elementAccessor == null ? object : elementAccessor.getValue( object ) );
        }
        return arrayList;
    }
}