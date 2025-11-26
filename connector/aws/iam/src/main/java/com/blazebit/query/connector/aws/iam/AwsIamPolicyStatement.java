/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public record AwsIamPolicyStatement(
		String sid,
		String effect,
		String principalJsonValue,
		String notPrincipalJsonValue,
		String actionJsonValue,
		String notActionJsonValue,
		String resourceJsonValue,
		String notResourceJsonValue,
		String conditionJsonValue
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static AwsIamPolicyStatement fromJson(String payload) {
		try {
			JsonNode json = MAPPER.readTree( payload );
			return new AwsIamPolicyStatement(
					json.has( "Sid" ) ? json.get( "Sid" ).asText( "" ) : "",
					json.has( "Effect" ) ? json.get( "Effect" ).asText( "" ) : "",
					json.has( "Principal" ) ? json.get( "Principal" ).toString() : "",
					json.has( "NotPrincipal" ) ? json.get( "NotPrincipal" ).toString() : "",
					json.has( "Action" ) ? json.get( "Action" ).toString() : "",
					json.has( "NotAction" ) ? json.get( "NotAction" ).toString() : "",
					json.has( "Resource" ) ? json.get( "Resource" ).toString() : "",
					json.has( "NotResource" ) ? json.get( "NotResource" ).toString() : "",
					json.has( "Condition" ) ? json.get( "Condition" ).toString() : ""
			);
		}
		catch (Exception e) {
			throw new RuntimeException( "Error parsing JSON for AwsIamPolicyStatement", e );
		}
	}
}
