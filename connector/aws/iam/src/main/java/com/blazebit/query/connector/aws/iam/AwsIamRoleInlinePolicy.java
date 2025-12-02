/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public record AwsIamRoleInlinePolicy(
		String accountId,
		String roleName,
		String policyName,
		String version,
		List<AwsIamPolicyStatement> statement
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static AwsIamRoleInlinePolicy fromJson(
			String accountId,
			String roleName,
			String policyName,
			String policyDocument) {
		try {
			String decodedDocument = URLDecoder.decode( policyDocument, StandardCharsets.UTF_8 );
			JsonNode json = MAPPER.readTree( decodedDocument );
			return new AwsIamRoleInlinePolicy(
					accountId,
					roleName,
					policyName,
					json.has( "Version" ) ? json.get( "Version" ).asText( "" ) : "",
					parseStatement( json )
			);
		}
		catch (Exception e) {
			throw new RuntimeException( "Error parsing JSON for AwsIamRoleInlinePolicy", e );
		}
	}

	private static List<AwsIamPolicyStatement> parseStatement(JsonNode json) {
		if ( !json.has( "Statement" ) ) {
			return List.of();
		}
		JsonNode statementNode = json.get( "Statement" );
		if ( statementNode.isArray() ) {
			return StreamSupport.stream( statementNode.spliterator(), false )
					.map( edge -> AwsIamPolicyStatement.fromJson( edge.toString() ) )
					.collect( Collectors.toList() );
		} else {
			return List.of( AwsIamPolicyStatement.fromJson( statementNode.toString() ) );
		}
	}
}
