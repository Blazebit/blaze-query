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

package com.blazebit.query.impl.calcite;

import java.util.List;

import com.blazebit.query.impl.calcite.converter.Converter;
import org.apache.calcite.linq4j.Enumerator;

/**
 * An enumerator for producing converted objects.
 *
 * @param <NativeType> The original type of the result list
 * @param <Result> The result type produced by the enumerator
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ConverterListEnumerator<NativeType, Result> implements Enumerator<Result> {
    private final List<? extends NativeType> list;
    private final Converter<NativeType, Result> converter;
    private int i = -1;

    /**
     * Creates a new enumerator that produces objects by applying the given converter on elements of the given list.
     *
     * @param list The original list
     * @param converter The converter to apply
     */
    public ConverterListEnumerator(List<? extends NativeType> list, Converter<NativeType, Result> converter) {
        this.list = list;
        this.converter = converter;
    }

    @Override
    public Result current() {
        return converter.convert( list.get( i ) );
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
}
