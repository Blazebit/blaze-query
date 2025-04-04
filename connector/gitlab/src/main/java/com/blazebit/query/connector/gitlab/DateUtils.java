/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
public final class DateUtils {

	private DateUtils() {
	}

	public static @Nullable OffsetDateTime parseIsoOffsetDateTime(@Nullable String dateTime) {
		try {
			if ( dateTime == null ) {
				return null;
			}

			return OffsetDateTime.parse( dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME );
		}
		catch (DateTimeParseException e) {
			throw new RuntimeException( "Failed to parse datetime: " + dateTime, e );
		}
	}

	public static @Nullable LocalDate parseIsoLocalDate(@Nullable String dateTime) {
		try {
			if ( dateTime == null ) {
				return null;
			}

			return LocalDate.parse( dateTime, DateTimeFormatter.ISO_LOCAL_DATE );
		}
		catch (DateTimeParseException e) {
			throw new RuntimeException( "Failed to parse datetime: " + dateTime, e );
		}
	}
}
