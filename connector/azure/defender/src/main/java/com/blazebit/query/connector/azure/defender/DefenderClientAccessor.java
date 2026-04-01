/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.defender;

import java.net.http.HttpClient;

/**
 * Accessor for Microsoft Defender for Endpoint API credentials and HTTP client.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public interface DefenderClientAccessor {

	/**
	 * Returns the Azure tenant ID associated with this accessor.
	 */
	String getTenantId();

	/**
	 * Returns the HTTP client to use for API requests.
	 */
	HttpClient getHttpClient();

	/**
	 * Returns a valid bearer token for authenticating against the Defender API.
	 * Implementations are responsible for token refresh as needed.
	 */
	String getBearerToken();

	/**
	 * Creates a simple {@link DefenderClientAccessor} from the given components.
	 */
	static DefenderClientAccessor create(String tenantId, HttpClient httpClient, String bearerToken) {
		return new DefenderClientAccessor() {
			@Override
			public String getTenantId() {
				return tenantId;
			}

			@Override
			public HttpClient getHttpClient() {
				return httpClient;
			}

			@Override
			public String getBearerToken() {
				return bearerToken;
			}
		};
	}
}
