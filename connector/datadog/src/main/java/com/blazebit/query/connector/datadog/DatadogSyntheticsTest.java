/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v1.model.SyntheticsTestDetailsWithoutSteps;

import java.util.List;

/**
 * Represents a Datadog Synthetic monitoring test (API, browser, or mobile).
 * Used to track web-page uptime and availability status.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
 */
public record DatadogSyntheticsTest(
		String publicId,
		String name,
		String type,
		String status,
		List<String> tags,
		List<String> locations
) {

	/**
	 * Maps a Datadog SDK {@link SyntheticsTestDetailsWithoutSteps} to a {@link DatadogSyntheticsTest} record.
	 */
	public static DatadogSyntheticsTest from(SyntheticsTestDetailsWithoutSteps test) {
		return new DatadogSyntheticsTest(
				test.getPublicId(),
				test.getName(),
				test.getType() != null ? test.getType().toString() : null,
				test.getStatus() != null ? test.getStatus().toString() : null,
				test.getTags() != null ? test.getTags() : List.of(),
				test.getLocations() != null ? test.getLocations() : List.of()
		);
	}
}
