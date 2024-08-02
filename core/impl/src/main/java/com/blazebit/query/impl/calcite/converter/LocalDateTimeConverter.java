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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

/**
 * Converter for an {@link LocalDateTime} value.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class LocalDateTimeConverter implements Converter<LocalDateTime, Long> {
    /**
     * The {@link LocalDateTime} converter.
     */
    public static final LocalDateTimeConverter INSTANCE = new LocalDateTimeConverter();

    private LocalDateTimeConverter() {
    }

    @Override
    public Long convert(LocalDateTime o) {
        if (o == null) {
            return null;
        }
        return o.toEpochSecond( ZoneOffset.UTC ) * 1000 + o.get( ChronoField.MILLI_OF_SECOND );
    }
}