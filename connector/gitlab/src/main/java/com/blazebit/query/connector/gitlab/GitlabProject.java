/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.blazebit.query.connector.gitlab.DateUtils.parseDate;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
public record GitlabProject(
		String id,
		String name,
		Boolean archived,
		String avatarUrl,
		OffsetDateTime createdAt,
		String description,
		OffsetDateTime lastActivityAt,
		String path,
		OffsetDateTime updatedAt,
		String groupId,
		String defaultBranch, // repository.rootRef
		Boolean mergeRequestsEnabled,
		List<GitlabBranchRule> branchRules
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitlabProject fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree( jsonString );

			return new GitlabProject(
					json.get( "id" ).asText(),
					json.get( "name" ).asText(),
					json.path( "archived" ).asBoolean( false ),
					json.path( "avatarUrl" ).asText( null ),
					parseDate( json.path( "createdAt" ), DateTimeFormatter.ISO_OFFSET_DATE_TIME ),
					json.path( "description" ).asText( null ),
					parseDate( json.path( "lastActivityAt" ), DateTimeFormatter.ISO_OFFSET_DATE_TIME ),
					json.path( "path" ).asText( null ),
					parseDate( json.path( "updatedAt" ), DateTimeFormatter.ISO_OFFSET_DATE_TIME ),
					json.has( "group" ) ? json.get( "group" ).path( "id" ).asText( null ) : null,
					json.has( "repository" ) ? json.get( "repository" ).path( "rootRef" ).asText( null ) : null,
					json.path( "mergeRequestsEnabled" ).asBoolean( false ),
					parseBranchRules( json.path( "branchRules" ) )
			);
		}
		catch (Exception e) {
			throw new RuntimeException( "Error parsing JSON for GitlabProject", e );
		}
	}

	private static List<GitlabBranchRule> parseBranchRules(JsonNode json) {
		if ( !json.has( "edges" ) ) {
			return List.of();
		}
		return StreamSupport.stream( json.get( "edges" ).spliterator(), false )
				.map( edge -> GitlabBranchRule.fromJson( edge.path( "node" ).toString() ) )
				.collect( Collectors.toList() );
	}
}
