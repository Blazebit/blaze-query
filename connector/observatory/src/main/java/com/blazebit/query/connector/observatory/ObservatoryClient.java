/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.observatory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Simple client for Mozilla Observatory API v2.
 *
 * Endpoint: POST {baseUrl}/api/v2/scan?host={host}
 *
 * @author Martijn Sprengers
 * @since 1.0.25
 */
public class ObservatoryClient {

	private final String host;
	private final String baseUrl;
	private final HttpClient httpClient;

	public ObservatoryClient(String host) {
		this(host, "https://observatory-api.mdn.mozilla.net");
	}

	public ObservatoryClient(String host, String baseUrl) {
		this.host = host;
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
		this.httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(30))
				.build();
	}

	public String getHost() {
		return host;
	}

	/**
	 * Triggers a scan and returns an {@link ObservatoryScan}.
	 */
	public ObservatoryScan fetchScan() {
		try {
			String json = runScan();
			return ObservatoryScan.fromJson(json, host);
		} catch (IOException e) {
			throw new UncheckedIOException("Error calling Observatory API", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted while calling Observatory API", e);
		}
	}

	private String runScan() throws IOException, InterruptedException {
		String encodedHost = URLEncoder.encode(host, StandardCharsets.UTF_8);
		URI uri = URI.create(baseUrl + "/api/v2/scan?host=" + encodedHost);

		HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(uri)
				.timeout(Duration.ofSeconds(60))
				.header("Accept", "application/json")
				.POST(HttpRequest.BodyPublishers.noBody())
				.build();

		HttpResponse<String> response =
				httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() >= 400) {
			throw new IOException("Observatory API error " + response.statusCode()
					+ ": " + response.body());
		}

		return response.body();
	}
}
