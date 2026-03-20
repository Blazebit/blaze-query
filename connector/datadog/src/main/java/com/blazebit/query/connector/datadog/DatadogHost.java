/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v1.model.Host;

import java.util.List;

/**
 * Represents a Datadog infrastructure Host. Used to track system health and
 * whether hosts are actively reporting.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public record DatadogHost(
		Long id,
		String name,
		String hostName,
		List<String> aliases,
		List<String> apps,
		List<String> sources,
		Long lastReportedTime,
		Boolean up,
		Boolean muted
) {

	/**
	 * Maps a Datadog SDK {@link Host} to a {@link DatadogHost} record.
	 */
	public static DatadogHost from(Host host) {
		return new DatadogHost(
				host.getId(),
				host.getName(),
				host.getHostName(),
				host.getAliases() != null ? host.getAliases() : List.of(),
				host.getApps() != null ? host.getApps() : List.of(),
				host.getSources() != null ? host.getSources() : List.of(),
				host.getLastReportedTime(),
				host.getUp(),
				host.getIsMuted()
		);
	}
}
