/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Util {
	public static final SimpleDateFormat DATE_FORMAT = createDateFormat();
	public static final SimpleDateFormat ISO_DATE_FORMAT = createIsoDateFormat();

	public static SimpleDateFormat createDateFormat() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); // Format for lastActivityOn
		format.setTimeZone( TimeZone.getTimeZone("UTC"));
		return format;
	}

	public static SimpleDateFormat createIsoDateFormat() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		format.setTimeZone( TimeZone.getTimeZone("UTC"));
		return format;
	}

	public static Date parseDate(String dateString, SimpleDateFormat dateFormat) {
		if (dateString == null || dateString.isEmpty()) {
			return null; // Return null if the date is not provided
		}
		try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException("Failed to parse date: " + dateString, e);
		}
	}
}
