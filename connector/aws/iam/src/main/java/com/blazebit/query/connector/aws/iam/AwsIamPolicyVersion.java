/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public record AwsIamPolicyVersion(
		String accountId,
		String policyArn,
		String versionId,
		Boolean isDefaultVersion,
		Instant createDate,
		String documentVersion,
		List<AwsIamPolicyStatement> documentStatement
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static AwsIamPolicyVersion fromJson(
			String accountId,
			String policyArn,
			String versionId,
			Boolean isDefaultVersion,
			Instant createDate,
			String policyDocument) {
		try {
			JsonNode json = MAPPER.readTree( policyDocument );
			return new AwsIamPolicyVersion(
					accountId,
					policyArn,
					versionId,
					isDefaultVersion,
					createDate,
					json.has( "Version" ) ? json.get( "Version" ).asText( "" ) : "",
					parseStatement( json )
			);
		}
		catch (Exception e) {
			throw new RuntimeException( "Error parsing JSON for AwsIamPolicyVersion", e );
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
