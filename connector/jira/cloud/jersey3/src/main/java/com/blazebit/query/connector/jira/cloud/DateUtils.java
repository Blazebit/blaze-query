/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.9
 */
public final class DateUtils {

	private DateUtils() {
	}

	public static @Nullable OffsetDateTime parseIsoOffsetDateTime(@Nullable String dateTime) {
		if (dateTime == null) {
			return null;
		}

		try {
			// Standard ISO formatter first
			return OffsetDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		} catch (DateTimeParseException e) {
			// Custom formatter for dates without the colon in timezone offset
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			try {
				return OffsetDateTime.parse(dateTime, formatter);
			} catch (DateTimeParseException nestedE) {
				throw new RuntimeException("Failed to parse datetime: " + dateTime, e);
			}
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
