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

import static com.blazebit.query.connector.utils.DateUtils.parseOffsetDateTime;


/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitlabUser(
		String id,
		String name,
		String username,
		OffsetDateTime lastActivityOn,
		boolean active,
		String avatarUrl,
		String bio,
		boolean bot,
		String commitEmail,
		OffsetDateTime createdAt,
		String discord,
		boolean gitpodEnabled,
		Integer groupCount,
		boolean human,
		String jobTitle,
		String linkedin,
		String location,
		String organization,
		String pronouns,
		String publicEmail,
		String twitter,
		String webPath,
		String webUrl
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitlabUser fromJson(String jsonString) {
		try {
			JsonNode userNode = MAPPER.readTree( jsonString );

			// If the node is deeply nested, extract the "user" field if available
			if ( userNode.has( "node" ) ) {
				userNode = userNode.path( "node" );
			}
			if ( userNode.has( "user" ) ) {
				userNode = userNode.path( "user" );
			}

			// If no valid user found, log warning and skip
			if ( userNode.path( "id" ).asText().isEmpty() ) {
				System.out.println( "⚠️ Skipping invalid user: " + jsonString );
				return null;
			}

			return new GitlabUser(
					userNode.path( "id" ).asText(),
					userNode.path( "name" ).asText(),
					userNode.path( "username" ).asText(),
					parseOffsetDateTime( userNode.path( "lastActivityOn" ) ),
					userNode.path( "active" ).asBoolean(),
					userNode.has( "avatarUrl" ) ? userNode.path( "avatarUrl" ).asText() : null,
					userNode.has( "bio" ) ? userNode.path( "bio" ).asText() : null,
					userNode.path( "bot" ).asBoolean(),
					userNode.has( "commitEmail" ) ? userNode.path( "commitEmail" ).asText() : null,
					parseOffsetDateTime( userNode.path( "createdAt" ) ),
					userNode.has( "discord" ) ? userNode.path( "discord" ).asText() : null,
					userNode.has( "gitpodEnabled" ) && userNode.path( "gitpodEnabled" ).asBoolean( false ),
					userNode.has( "groupCount" ) ? userNode.path( "groupCount" ).asInt( 0 ) : 0,
					userNode.has( "human" ) && userNode.path( "human" ).asBoolean( false ),
					userNode.has( "jobTitle" ) ? userNode.path( "jobTitle" ).asText() : null,
					userNode.has( "linkedin" ) ? userNode.path( "linkedin" ).asText() : null,
					userNode.has( "location" ) ? userNode.path( "location" ).asText() : null,
					userNode.has( "organization" ) ? userNode.path( "organization" ).asText() : null,
					userNode.has( "pronouns" ) ? userNode.path( "pronouns" ).asText() : null,
					userNode.has( "publicEmail" ) ? userNode.path( "publicEmail" ).asText() : null,
					userNode.has( "twitter" ) ? userNode.path( "twitter" ).asText() : null,
					userNode.has( "webPath" ) ? userNode.path( "webPath" ).asText() : null,
					userNode.has( "webUrl" ) ? userNode.path( "webUrl" ).asText() : null
			);
		}
		catch (Exception e) {
			throw new RuntimeException( "Error parsing JSON for GitlabUser", e );
		}
	}

}
