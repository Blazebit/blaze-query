/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.10
 */
public record GitlabMergeRequest(
		String id,
		String name,
		boolean approved,
		GitlabMergeRequestState state
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitlabMergeRequest fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree( jsonString );

			return new GitlabMergeRequest(
					json.get( "id" ).asText(),
					json.get( "name" ).asText(),
					json.path( "approved" ).asBoolean( false ),
					GitlabMergeRequestState.valueOf( json.get( "state" ).asText() )
			);
		}
		catch (Exception e) {
			throw new RuntimeException( "Error parsing JSON for GitlabMergeRequest", e );
		}
	}

}
