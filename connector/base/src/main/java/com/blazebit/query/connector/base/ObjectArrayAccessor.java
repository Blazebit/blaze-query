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

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Accessor for obtaining an object array from an object, by invoking an array of accessors on it.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ObjectArrayAccessor implements Accessor {
    private final @Nullable Accessor baseAccessor;
    private final Accessor[] accessors;

    /**
     * Creates a method accessor.
     *
     * @param baseAccessor Accessor to obtain the object.
     * @param accessors The accessors to obtain the individual attribute values.
     */
    public ObjectArrayAccessor(@Nullable Accessor baseAccessor, Accessor[] accessors) {
        this.baseAccessor = baseAccessor;
        this.accessors = accessors;
    }

    /**
     * Returns the base accessor to obtain the object.
     * @return the base accessor to obtain the object
     */
    public @Nullable Accessor getBaseAccessor() {
        return baseAccessor;
    }

    /**
     * Returns the accessors to obtain the attribute values.
     * @return the accessors to obtain the attribute values
     */
    public Accessor[] getAccessors() {
        return accessors;
    }

    @Override
    public String getAttributePath() {
        return baseAccessor == null ? null : baseAccessor.getAttributePath();
    }

    @Override
    public Object getValue(Object o) {
        if (o == null) {
            return null;
        }
        if ( baseAccessor != null ) {
            o = baseAccessor.getValue(o);
        }
        if (o == null) {
            return null;
        }
        Object[] array = new Object[accessors.length];
        for ( int i = 0; i < accessors.length; i++ ) {
            array[i] = accessors[i].getValue(o);
        }
        return array;
    }
}