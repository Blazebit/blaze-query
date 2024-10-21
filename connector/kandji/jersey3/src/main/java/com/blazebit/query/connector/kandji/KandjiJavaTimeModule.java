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
package com.blazebit.query.connector.kandji;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A simple module that registers a special deserializer for {@code java.time} types that is RFC3339 compliant.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class KandjiJavaTimeModule extends SimpleModule {

    /**
     * Creates a new module.
     */
    public KandjiJavaTimeModule() {
        super("KandjiJavaTimeModule");

        addDeserializer(Instant.class, KandjiInstantDeserializer.INSTANT);
        addDeserializer(OffsetDateTime.class, KandjiInstantDeserializer.OFFSET_DATE_TIME);
        addDeserializer(ZonedDateTime.class, KandjiInstantDeserializer.ZONED_DATE_TIME);
    }
}
