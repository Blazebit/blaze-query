/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.linear;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

/**
 * Represents a Linear cycle (sprint).
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public record LinearCycle(
		String id,
		String name,
		Integer number,
		OffsetDateTime startsAt,
		OffsetDateTime endsAt,
		OffsetDateTime completedAt,
		OffsetDateTime canceledAt,
		OffsetDateTime createdAt,
		OffsetDateTime updatedAt,
		TeamRef team) {

	public record TeamRef(String id, String name, String key) {
	}

	public static LinearCycle fromJson(JsonNode n) {
		TeamRef team = null;
		if ( !n.path( "team" ).isMissingNode() && !n.path( "team" ).isNull() ) {
			JsonNode t = n.path( "team" );
			team = new TeamRef( t.path( "id" ).asText( null ), t.path( "name" ).asText( null ),
					t.path( "key" ).asText( null ) );
		}

		return new LinearCycle(
				n.path( "id" ).asText( null ),
				n.path( "name" ).asText( null ),
				n.path( "number" ).isNull() || n.path( "number" ).isMissingNode()
						? null : n.path( "number" ).asInt(),
				parseDateTime( n.path( "startsAt" ) ),
				parseDateTime( n.path( "endsAt" ) ),
				parseDateTime( n.path( "completedAt" ) ),
				parseDateTime( n.path( "canceledAt" ) ),
				parseDateTime( n.path( "createdAt" ) ),
				parseDateTime( n.path( "updatedAt" ) ),
				team );
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
