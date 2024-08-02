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

import java.time.OffsetDateTime;

/**
 * Converter for an {@link OffsetDateTime} value.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class OffsetDateTimeConverter implements Converter<OffsetDateTime, Long> {
    /**
     * The {@link OffsetDateTime} converter.
     */
    public static final OffsetDateTimeConverter INSTANCE = new OffsetDateTimeConverter();

    private OffsetDateTimeConverter() {
    }

    @Override
    public Long convert(OffsetDateTime o) {
        if (o == null) {
            return null;
        }
        return o.toInstant().toEpochMilli();
    }
}