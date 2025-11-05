/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public record AwsBucketPolicyStatement(
		String principalJsonValue,
		String effect,
		String conditionJsonValue,
		String resourceJsonValue

) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static AwsBucketPolicyStatement fromJson(String payload) {
		try {
			JsonNode json = MAPPER.readTree( payload );
			return new AwsBucketPolicyStatement(
					json.get( "Principal" ).toString(), json.get( "Effect" ).asText( "" ),
					json.has( "Condition" ) ? json.get( "Condition" ).toString() : "",
					json.has( "Resource" ) ? json.get( "Resource" ).toString() : ""
			);
		}
		catch (Exception e) {
			throw new RuntimeException( "Error parsing JSON for AwsBucketPolicyStatement", e );
		}
	}
}
