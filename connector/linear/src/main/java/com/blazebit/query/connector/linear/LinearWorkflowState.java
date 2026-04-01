/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.linear;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a Linear workflow state (issue status within a team).
 * The {@code type} field is one of: {@code triage}, {@code backlog}, {@code unstarted},
 * {@code started}, {@code completed}, {@code cancelled}.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public record LinearWorkflowState(
		String id,
		String name,
		String type,
		String color,
		Double position,
		TeamRef team) {

	public record TeamRef(String id, String name, String key) {
	}

	public static LinearWorkflowState fromJson(JsonNode n) {
		TeamRef team = null;
		if ( !n.path( "team" ).isMissingNode() && !n.path( "team" ).isNull() ) {
			JsonNode t = n.path( "team" );
			team = new TeamRef( t.path( "id" ).asText( null ), t.path( "name" ).asText( null ),
					t.path( "key" ).asText( null ) );
		}

		return new LinearWorkflowState(
				n.path( "id" ).asText( null ),
				n.path( "name" ).asText( null ),
				n.path( "type" ).asText( null ),
				n.path( "color" ).asText( null ),
				n.path( "position" ).isNull() || n.path( "position" ).isMissingNode()
						? null : n.path( "position" ).asDouble(),
				team );
	}
}
