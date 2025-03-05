/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.databind.JsonNode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
public final class DateUtils {

	public static final DateFormat DATE_FORMAT;
	public static final DateFormat ISO_DATE_FORMAT;

	static {
		DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd" ); // Format for lastActivityOn
		DATE_FORMAT.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

		ISO_DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssX" );
		ISO_DATE_FORMAT.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
	}

	private DateUtils() {
	}

	public static Date parseDate(JsonNode dateNode, DateFormat dateFormat) {
		String dateString = dateNode.asText( null ); // Returns null if the field is missing
		if ( dateString == null || dateNode.isMissingNode() || dateNode.asText().isEmpty() ) {
			return null; // Return null if the date is missing or empty
		}
		try {
			return dateFormat.parse( dateString );
		}
		catch (ParseException e) {
			throw new RuntimeException( "Failed to parse date: " + dateString, e );
		}
	}
}
