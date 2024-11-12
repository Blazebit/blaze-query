/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.kandji;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;

/**
 * Kandji datetimes are messy and contain extra spaces.
 *
 * @param <T> The temporal type
 * @author Christian Beikov
 * @since 1.0.0
 */
public class KandjiInstantDeserializer<T extends Temporal> extends InstantDeserializer<T> {

	/**
	 * RFC3339 compliant {@link Instant} deserializer.
	 */
	public static final KandjiInstantDeserializer<Instant> INSTANT = new KandjiInstantDeserializer<>(
			Instant.class, DateTimeFormatter.ISO_INSTANT,
			Instant::from,
			a -> Instant.ofEpochMilli( a.value ),
			a -> Instant.ofEpochSecond( a.integer, a.fraction ),
			null,
			true // yes, replace zero offset with Z
	);

	/**
	 * RFC3339 compliant {@link OffsetDateTime} deserializer.
	 */
	public static final KandjiInstantDeserializer<OffsetDateTime> OFFSET_DATE_TIME = new KandjiInstantDeserializer<>(
			OffsetDateTime.class, DateTimeFormatter.ISO_OFFSET_DATE_TIME,
			OffsetDateTime::from,
			a -> OffsetDateTime.ofInstant( Instant.ofEpochMilli( a.value ), a.zoneId ),
			a -> OffsetDateTime.ofInstant( Instant.ofEpochSecond( a.integer, a.fraction ), a.zoneId ),
			(d, z) -> (d.isEqual( OffsetDateTime.MIN ) || d.isEqual( OffsetDateTime.MAX ) ?
					d :
					d.withOffsetSameInstant( z.getRules().getOffset( d.toLocalDateTime() ) )),
			true // yes, replace zero offset with Z
	);

	/**
	 * RFC3339 compliant {@link ZonedDateTime} deserializer.
	 */
	public static final KandjiInstantDeserializer<ZonedDateTime> ZONED_DATE_TIME = new KandjiInstantDeserializer<>(
			ZonedDateTime.class, DateTimeFormatter.ISO_ZONED_DATE_TIME,
			ZonedDateTime::from,
			a -> ZonedDateTime.ofInstant( Instant.ofEpochMilli( a.value ), a.zoneId ),
			a -> ZonedDateTime.ofInstant( Instant.ofEpochSecond( a.integer, a.fraction ), a.zoneId ),
			ZonedDateTime::withZoneSameInstant,
			false // keep zero offset and Z separate since zones explicitly supported
	);

	/**
	 * Creates a new deserializer.
	 *
	 * @param supportedType The supported type
	 * @param formatter The formatter
	 * @param parsedToValue The value accessor
	 * @param fromMilliseconds The value accessor for millis
	 * @param fromNanoseconds The value accessor for nanos
	 * @param adjust Zone adjuster
	 * @param replaceZeroOffsetAsZ Whether to replace zero offset as Z zone
	 */
	protected KandjiInstantDeserializer(
			Class<T> supportedType,
			DateTimeFormatter formatter,
			Function<TemporalAccessor, T> parsedToValue,
			Function<FromIntegerArguments, T> fromMilliseconds,
			Function<FromDecimalArguments, T> fromNanoseconds,
			BiFunction<T, ZoneId, T> adjust,
			boolean replaceZeroOffsetAsZ) {
		super(
				supportedType,
				formatter,
				parsedToValue,
				fromMilliseconds,
				fromNanoseconds,
				adjust,
				replaceZeroOffsetAsZ
		);
	}

	@Override
	protected T _fromString(JsonParser p, DeserializationContext ctxt, String string0) throws IOException {
		int spaceIndex = string0.indexOf( ' ' );
		if ( spaceIndex != -1 ) {
			StringBuilder sb = new StringBuilder( string0.length() );
			sb.append( string0, 0, spaceIndex );
			sb.append( 'T' );
			int nextCharIndex = spaceIndex + 1;
			while ( (spaceIndex = string0.indexOf( ' ', nextCharIndex )) != -1 ) {
				sb.append( string0, nextCharIndex, spaceIndex );
				nextCharIndex = spaceIndex + 1;
			}
			sb.append( string0, nextCharIndex, string0.length() );
			string0 = sb.toString();
		}
		return super._fromString( p, ctxt, string0 );
	}
}
