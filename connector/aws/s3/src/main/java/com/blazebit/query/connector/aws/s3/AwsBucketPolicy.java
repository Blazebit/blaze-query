/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public record AwsBucketPolicy(
		String accountId,
		String region,
		String resourceId,
		String id,
		String version,
		List<AwsBucketPolicyStatement> statement
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static AwsBucketPolicy fromJson(String accountId, String region, String resourceId, String payload) {
		try {
			JsonNode json = MAPPER.readTree( payload );
			return new AwsBucketPolicy(
					accountId, region, resourceId, json.has( "Id" ) ? json.get( "Id" ).asText( "" ) : "",
					json.get( "Version" ).asText( "" ),
					parseStatement( json )
			);
		}
		catch (Exception e) {
			throw new RuntimeException( "Error parsing JSON for AwsBucketPolicy", e );
		}
	}

	private static List<AwsBucketPolicyStatement> parseStatement(JsonNode json) {
		if ( !json.has( "Statement" ) ) {
			return List.of();
		}
		JsonNode statementNode = json.get( "Statement" );
		if ( statementNode.isArray() ) {
			return StreamSupport.stream( statementNode.spliterator(), false )
					.map( edge -> AwsBucketPolicyStatement.fromJson( edge.toString() ) )
					.collect( Collectors.toList() );
		} else {
			return List.of( AwsBucketPolicyStatement.fromJson( statementNode.toString() ) );
		}
	}
}
