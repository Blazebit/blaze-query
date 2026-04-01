/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.linear;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

/**
 * Represents a Linear project.
 * The {@code state} field is one of: {@code backlog}, {@code planned}, {@code inProgress},
 * {@code paused}, {@code completed}, {@code cancelled}.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public record LinearProject(
		String id,
		String name,
		String description,
		String state,
		Integer priority,
		String priorityLabel,
		String startDate,
		String targetDate,
		OffsetDateTime completedAt,
		OffsetDateTime canceledAt,
		OffsetDateTime createdAt,
		OffsetDateTime updatedAt,
		String url,
		LeadRef projectLead) {

	public record LeadRef(String id, String name, String email) {
	}

	public static LinearProject fromJson(JsonNode n) {
		LeadRef projectLead = null;
		if ( !n.path( "lead" ).isMissingNode() && !n.path( "lead" ).isNull() ) {
			JsonNode l = n.path( "lead" );
			projectLead = new LeadRef( l.path( "id" ).asText( null ), l.path( "name" ).asText( null ),
					l.path( "email" ).asText( null ) );
		}

		return new LinearProject(
				n.path( "id" ).asText( null ),
				n.path( "name" ).asText( null ),
				n.path( "description" ).asText( null ),
				n.path( "state" ).asText( null ),
				n.path( "priority" ).isNull() || n.path( "priority" ).isMissingNode()
						? null : n.path( "priority" ).asInt(),
				n.path( "priorityLabel" ).asText( null ),
				n.path( "startDate" ).asText( null ),
				n.path( "targetDate" ).asText( null ),
				parseDateTime( n.path( "completedAt" ) ),
				parseDateTime( n.path( "canceledAt" ) ),
				parseDateTime( n.path( "createdAt" ) ),
				parseDateTime( n.path( "updatedAt" ) ),
				n.path( "url" ).asText( null ),
				projectLead );
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
