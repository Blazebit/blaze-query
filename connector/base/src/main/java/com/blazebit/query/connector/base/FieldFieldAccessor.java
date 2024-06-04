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

import java.lang.reflect.Field;

import com.blazebit.query.spi.DataFormatFieldAccessor;

/**
 * Accessor for a field based attribute of an object.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class FieldFieldAccessor implements DataFormatFieldAccessor {
    private final Field field;

    /**
     * Creates a field accessor.
     *
     * @param field The field to obtain an attribute value.
     */
    public FieldFieldAccessor(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    @Override
    public Object get(Object o) {
        try {
            return field.get( o );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }
}