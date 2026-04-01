/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.linear;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

/**
 * Represents a Linear issue label.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public record LinearIssueLabel(
		String id,
		String name,
		String color,
		OffsetDateTime createdAt,
		OffsetDateTime updatedAt,
		TeamRef team,
		ParentRef parent) {

	public record TeamRef(String id, String name, String key) {
	}

	public record ParentRef(String id, String name) {
	}

	public static LinearIssueLabel fromJson(JsonNode n) {
		TeamRef team = null;
		if ( !n.path( "team" ).isMissingNode() && !n.path( "team" ).isNull() ) {
			JsonNode t = n.path( "team" );
			team = new TeamRef( t.path( "id" ).asText( null ), t.path( "name" ).asText( null ),
					t.path( "key" ).asText( null ) );
		}

		ParentRef parent = null;
		if ( !n.path( "parent" ).isMissingNode() && !n.path( "parent" ).isNull() ) {
			JsonNode p = n.path( "parent" );
			parent = new ParentRef( p.path( "id" ).asText( null ), p.path( "name" ).asText( null ) );
		}

		return new LinearIssueLabel(
				n.path( "id" ).asText( null ),
				n.path( "name" ).asText( null ),
				n.path( "color" ).asText( null ),
				parseDateTime( n.path( "createdAt" ) ),
				parseDateTime( n.path( "updatedAt" ) ),
				team,
				parent );
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
