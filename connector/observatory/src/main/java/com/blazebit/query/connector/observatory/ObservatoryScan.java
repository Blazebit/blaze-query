/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.observatory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.time.OffsetDateTime;

import static com.blazebit.query.connector.observatory.DateUtils.parseIsoOffsetDateTime;

/**
 * @author Martijn Sprengers
 * @since 1.0.25
 */
public record ObservatoryScan(
		long id,
		String host,
		String detailsUrl,
		int algorithmVersion,
		OffsetDateTime scannedAt,
		String error,
		String grade,
		int score,
		int statusCode,
		int testsFailed,
		int testsPassed,
		int testsQuantity
) {

	private static final JsonMapper MAPPER = ObjectMappers.getInstance();

	/**
	 * Parse an Observatory v2 scan result JSON string into an {@link ObservatoryScan}.
	 * <p>
	 * The JSON is expected to look like:
	 * <pre>
	 * {
	 *   "id": 79627603,
	 *   "details_url": "<a href="https://developer.mozilla.org/en-US/observatory/analyze?host=tidalcontrol.com">...</a>",
	 *   "algorithm_version": 5,
	 *   "scanned_at": "2025-12-05T16:52:10.602Z",
	 *   "error": null,
	 *   "grade": "B",
	 *   "score": 70,
	 *   "status_code": 200,
	 *   "tests_failed": 2,
	 *   "tests_passed": 8,
	 *   "tests_quantity": 10
	 * }
	 * </pre>
	 *
	 * @param jsonString The raw JSON returned from the v2 API.
	 * @param host       The host that was scanned (taken from Blaze Query context / integration config).
	 * @return Parsed {@link ObservatoryScan}
	 */
	public static ObservatoryScan fromJson(String jsonString, String host) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			long id = json.path("id").asLong();
			String detailsUrl = json.path("details_url").asText(null);
			int algorithmVersion = json.path("algorithm_version").asInt();
			OffsetDateTime scannedAt = parseIsoOffsetDateTime(json.path("scanned_at").asText(null));

			JsonNode errorNode = json.path("error");
			String error = errorNode.isMissingNode() || errorNode.isNull()
					? null
					: errorNode.asText(null);

			String grade = json.path("grade").asText(null);
			int score = json.path("score").asInt();
			int statusCode = json.path("status_code").asInt();
			int testsFailed = json.path("tests_failed").asInt();
			int testsPassed = json.path("tests_passed").asInt();
			int testsQuantity = json.path("tests_quantity").asInt();

			return new ObservatoryScan(
					id,
					host,
					detailsUrl,
					algorithmVersion,
					scannedAt,
					error,
					grade,
					score,
					statusCode,
					testsFailed,
					testsPassed,
					testsQuantity
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for ObservatoryScan", e);
		}
	}
}
