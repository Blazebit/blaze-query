/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
public record GitlabBranchRule(
		String id,
		String name,
		Boolean isDefault,
		Boolean isProtected,
		Boolean allowForcePush,
		Boolean codeOwnerApprovalRequired
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitlabBranchRule fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree( jsonString );
			JsonNode branchProtection = json.path( "branchProtection" );

			return new GitlabBranchRule(
					json.get( "id" ).asText(),
					json.get( "name" ).asText(),
					json.path( "isDefault" ).asBoolean( false ),
					json.path( "isProtected" ).asBoolean( false ),
					branchProtection.path( "allowForcePush" ).asBoolean( false ),
					branchProtection.path( "codeOwnerApprovalRequired" ).asBoolean( false )
			);
		}
		catch (Exception e) {
			throw new RuntimeException( "Error parsing JSON for GitlabBranchRule", e );
		}
	}
}
