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

import java.util.List;

import org.apache.calcite.linq4j.Enumerator;
import org.checkerframework.checker.nullness.qual.Nullable;

public class AccessorListEnumerator<NativeType, Result> implements Enumerator<Result> {
    private final List<? extends NativeType> list;
    private final RowConverter<NativeType, Result> converter;
    int i = -1;

    public AccessorListEnumerator(List<? extends NativeType> list, RowConverter<NativeType, Result> converter) {
        this.list = list;
        this.converter = converter;
    }

    private static RowConverter<?, ?> converter(Accessor[] accessors, @Nullable int[] fields) {
        if (fields == null) {
            return new ArrayRowConverter<>( accessors );
        } else if (fields.length == 1) {
            return new SingleColumnRowConverter<>(accessors[fields[0]]);
        } else {
            Accessor[] fieldAccessors = new Accessor[fields.length];
            for ( int i = 0; i < fields.length; i++ ) {
                fieldAccessors[i] = accessors[fields[i]];
            }
            return new ArrayRowConverter<>( fieldAccessors );
        }
    }

    public static <NativeType> RowConverter<NativeType, Object[]> arrayConverter(Accessor[] accessors, @Nullable int[] fields) {
        if (fields == null) {
            return new ArrayRowConverter<>( accessors );
        } else {
            Accessor[] fieldAccessors = new Accessor[fields.length];
            for ( int i = 0; i < fields.length; i++ ) {
                fieldAccessors[i] = accessors[fields[i]];
            }
            return new ArrayRowConverter<>( fieldAccessors );
        }
    }

    /** Returns an array of integers {0, ..., n - 1}. */
    public static int[] identityList(int n) {
        int[] integers = new int[n];
        for (int i = 0; i < n; i++) {
            integers[i] = i;
        }
        return integers;
    }

    @Override
    public Result current() {
        return converter.convertRow( list.get( i ) );
    }

    @Override
    public boolean moveNext() {
        return ++i < list.size();
    }

    @Override
    public void reset() {
        i = -1;
    }

    @Override
    public void close() {
    }

    /**
     * Row converter.
     */
    public abstract static class RowConverter<NativeType, Result> {
        abstract Result convertRow(NativeType object);
    }

    /**
     * Array row converter.
     */
    public static class ArrayRowConverter<NativeType> extends RowConverter<NativeType, Object[]> {

        private final Accessor[] accessors;

        public ArrayRowConverter(Accessor[] accessors) {
            this.accessors = accessors;
        }

        @Override
        public Object[] convertRow(NativeType object) {
            Object[] objects = new Object[accessors.length];
            for ( int i = 0; i < objects.length; i++ ) {
                objects[i] = accessors[i].getValue( object );
            }
            return objects;
        }
    }

    /** Single column row converter. */
    private static class SingleColumnRowConverter<NativeType> extends RowConverter<NativeType, Object> {
        private final Accessor accessor;

        public SingleColumnRowConverter(Accessor accessor) {
            this.accessor = accessor;
        }

        @Override
        public Object convertRow(NativeType object) {
            return accessor.getValue( object );
        }
    }
}
