/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
public final class DateUtils {

	private DateUtils() {
	}

	public static OffsetDateTime parseDate(JsonNode dateTimeNode, DateTimeFormatter dtf) {
		String dateTimeString = dateTimeNode.asText(null); // Returns null if the field is missing
		if (dateTimeString == null || dateTimeNode.isMissingNode() || dateTimeString.isEmpty()) {
			return null; // Return null if the date is missing or empty
		}
		try {
			// if the formatter is for date only, parse it as LocalDate and convert to OffsetDateTime
			if( dtf == DateTimeFormatter.ISO_LOCAL_DATE || dtf == DateTimeFormatter.BASIC_ISO_DATE){
				LocalDate date = LocalDate.parse(dateTimeString, dtf);
				return date.atStartOfDay().atOffset(ZoneOffset.UTC);
			}

			// else parse it as OffsetDateTime
			return OffsetDateTime.parse(dateTimeString, dtf);
		} catch (DateTimeParseException e) {
			throw new RuntimeException("Failed to parse datetime: " + dateTimeString, e);
		}
	}
}
