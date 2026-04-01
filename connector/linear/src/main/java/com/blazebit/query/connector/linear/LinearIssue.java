/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.linear;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Linear issue.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public record LinearIssue(
		String id,
		String identifier,
		String title,
		Integer priority,
		String priorityLabel,
		OffsetDateTime createdAt,
		OffsetDateTime updatedAt,
		String dueDate,
		OffsetDateTime completedAt,
		OffsetDateTime canceledAt,
		String url,
		StateRef state,
		TeamRef team,
		UserRef assignee,
		UserRef creator,
		List<LabelRef> labels) {

	public record StateRef(String id, String name, String type) {
	}

	public record TeamRef(String id, String name, String key) {
	}

	public record UserRef(String id, String name, String email) {
	}

	public record LabelRef(String id, String name) {
	}

	public static LinearIssue fromJson(JsonNode n) {
		StateRef state = null;
		if ( !n.path( "state" ).isMissingNode() && !n.path( "state" ).isNull() ) {
			JsonNode s = n.path( "state" );
			state = new StateRef( s.path( "id" ).asText( null ), s.path( "name" ).asText( null ),
					s.path( "type" ).asText( null ) );
		}

		TeamRef team = null;
		if ( !n.path( "team" ).isMissingNode() && !n.path( "team" ).isNull() ) {
			JsonNode t = n.path( "team" );
			team = new TeamRef( t.path( "id" ).asText( null ), t.path( "name" ).asText( null ),
					t.path( "key" ).asText( null ) );
		}

		UserRef assignee = null;
		if ( !n.path( "assignee" ).isMissingNode() && !n.path( "assignee" ).isNull() ) {
			JsonNode a = n.path( "assignee" );
			assignee = new UserRef( a.path( "id" ).asText( null ), a.path( "name" ).asText( null ),
					a.path( "email" ).asText( null ) );
		}

		UserRef creator = null;
		if ( !n.path( "creator" ).isMissingNode() && !n.path( "creator" ).isNull() ) {
			JsonNode c = n.path( "creator" );
			creator = new UserRef( c.path( "id" ).asText( null ), c.path( "name" ).asText( null ),
					c.path( "email" ).asText( null ) );
		}

		List<LabelRef> labels = new ArrayList<>();
		JsonNode labelNodes = n.path( "labels" ).path( "nodes" );
		if ( labelNodes.isArray() ) {
			for ( JsonNode l : labelNodes ) {
				labels.add( new LabelRef( l.path( "id" ).asText( null ), l.path( "name" ).asText( null ) ) );
			}
		}

		return new LinearIssue(
				n.path( "id" ).asText( null ),
				n.path( "identifier" ).asText( null ),
				n.path( "title" ).asText( null ),
				n.path( "priority" ).isNull() || n.path( "priority" ).isMissingNode()
						? null : n.path( "priority" ).asInt(),
				n.path( "priorityLabel" ).asText( null ),
				parseDateTime( n.path( "createdAt" ) ),
				parseDateTime( n.path( "updatedAt" ) ),
				n.path( "dueDate" ).asText( null ),
				parseDateTime( n.path( "completedAt" ) ),
				parseDateTime( n.path( "canceledAt" ) ),
				n.path( "url" ).asText( null ),
				state,
				team,
				assignee,
				creator,
				labels );
	}

	private static OffsetDateTime parseDateTime(JsonNode node) {
		if ( node.isNull() || node.isMissingNode() ) {
			return null;
		}
		String text = node.asText( null );
		if ( text == null || text.isEmpty() ) {
			return null;
		}
		return OffsetDateTime.parse( text );
	}
}
