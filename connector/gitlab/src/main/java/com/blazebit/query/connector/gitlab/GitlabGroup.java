/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.blazebit.query.connector.utils.ObjectMappers;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;

import static com.blazebit.query.connector.utils.DateUtils.parseIsoOffsetDateTime;


/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitlabGroup(String id, String name, String path, boolean requireTwoFactorAuthentication,
						int twoFactorGracePeriod, OffsetDateTime createdAt, String description, String fullName,
						String projectCreationLevel, OffsetDateTime updatedAt, String visibility) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitlabGroup fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree( jsonString );

			return new GitlabGroup(
					json.get( "id" ).asText(),
					json.get( "name" ).asText(),
					json.get( "path" ).asText(),
					json.path( "requireTwoFactorAuthentication" ).asBoolean( false ),
					json.path( "twoFactorGracePeriod" ).asInt( 0 ),
					parseIsoOffsetDateTime( json.path( "createdAt" ).asText(null) ),
					json.path( "description" ).asText( null ),
					json.path( "fullName" ).asText( null ),
					json.path( "projectCreationLevel" ).asText( null ),
					parseIsoOffsetDateTime( json.path( "updatedAt" ).asText(null) ),
					json.path( "visibility" ).asText( null )
			);
		}
		catch (Exception e) {
			throw new RuntimeException( "Error parsing JSON for GitlabGroup", e );
		}
	}
}
