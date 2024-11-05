/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
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
		super( "KandjiJavaTimeModule" );

		addDeserializer( Instant.class, KandjiInstantDeserializer.INSTANT );
		addDeserializer( OffsetDateTime.class, KandjiInstantDeserializer.OFFSET_DATE_TIME );
		addDeserializer( ZonedDateTime.class, KandjiInstantDeserializer.ZONED_DATE_TIME );
	}
}
