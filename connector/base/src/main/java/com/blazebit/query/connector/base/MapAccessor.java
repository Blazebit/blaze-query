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

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

public class MapAccessor implements Accessor {
    private final Accessor baseAccessor;
    private final @Nullable Accessor keyAccessor;
    private final @Nullable Accessor elementAccessor;

    public MapAccessor(Accessor baseAccessor, @Nullable Accessor keyAccessor, @Nullable Accessor elementAccessor) {
        this.baseAccessor = baseAccessor;
        this.keyAccessor = keyAccessor;
        this.elementAccessor = elementAccessor;
    }

    public Accessor getBaseAccessor() {
        return baseAccessor;
    }

    public @Nullable Accessor getKeyAccessor() {
        return keyAccessor;
    }

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
        Map<Object, Object> map = (Map<Object, Object>) baseAccessor.getValue( o );
        if (map == null) {
            return null;
        }
        if ( keyAccessor == null && elementAccessor == null ) {
            return map;
        }
        Map<Object, Object> newMap = new HashMap<>();
        for ( Map.Entry<Object, Object> entry : map.entrySet() ) {
            Object key = keyAccessor == null ? entry.getKey() : keyAccessor.getValue( entry.getKey() );
            Object value = elementAccessor == null ? entry.getValue() : elementAccessor.getValue( entry.getValue() );
            newMap.put( key, value );
        }
        return newMap;
    }
}