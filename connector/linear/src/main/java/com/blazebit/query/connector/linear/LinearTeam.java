/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.linear;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

/**
 * Represents a Linear team.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public record LinearTeam(
		String id,
		String name,
		String key,
		String description,
		Boolean privateTeam,
		OffsetDateTime createdAt,
		OffsetDateTime updatedAt) {

	public static LinearTeam fromJson(JsonNode n) {
		return new LinearTeam(
				n.path( "id" ).asText( null ),
				n.path( "name" ).asText( null ),
				n.path( "key" ).asText( null ),
				n.path( "description" ).asText( null ),
				n.path( "private" ).isNull() || n.path( "private" ).isMissingNode()
						? null : n.path( "private" ).asBoolean(),
				parseDateTime( n.path( "createdAt" ) ),
				parseDateTime( n.path( "updatedAt" ) ) );
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
