/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.linear;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

/**
 * Represents a Linear workspace member.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public record LinearUser(
		String id,
		String name,
		String displayName,
		String email,
		Boolean active,
		Boolean admin,
		Boolean guest,
		OffsetDateTime createdAt,
		OffsetDateTime updatedAt) {

	public static LinearUser fromJson(JsonNode n) {
		return new LinearUser(
				n.path( "id" ).asText( null ),
				n.path( "name" ).asText( null ),
				n.path( "displayName" ).asText( null ),
				n.path( "email" ).asText( null ),
				n.path( "active" ).isNull() || n.path( "active" ).isMissingNode()
						? null : n.path( "active" ).asBoolean(),
				n.path( "admin" ).isNull() || n.path( "admin" ).isMissingNode()
						? null : n.path( "admin" ).asBoolean(),
				n.path( "guest" ).isNull() || n.path( "guest" ).isMissingNode()
						? null : n.path( "guest" ).asBoolean(),
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
