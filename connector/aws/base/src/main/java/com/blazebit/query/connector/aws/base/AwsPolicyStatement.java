/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.base;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsPolicyStatement {

	private final String sid;
	private final String effect;
	private final String principalJsonValue;
	private final String actionJsonValue;
	private final String resourceJsonValue;
	private final String conditionJsonValue;

	private AwsPolicyStatement(
			String sid,
			String effect,
			String principalJsonValue,
			String actionJsonValue,
			String resourceJsonValue,
			String conditionJsonValue) {
		this.sid = sid;
		this.effect = effect;
		this.principalJsonValue = principalJsonValue;
		this.actionJsonValue = actionJsonValue;
		this.resourceJsonValue = resourceJsonValue;
		this.conditionJsonValue = conditionJsonValue;
	}

	public static AwsPolicyStatement fromJson(JsonNode json) {
		return new AwsPolicyStatement(
				json.has( "Sid" ) ? json.get( "Sid" ).asText( "" ) : "",
				json.has( "Effect" ) ? json.get( "Effect" ).asText( "" ) : "",
				json.has( "Principal" ) ? json.get( "Principal" ).toString() : "",
				json.has( "Action" ) ? json.get( "Action" ).toString() : "",
				json.has( "Resource" ) ? json.get( "Resource" ).toString() : "",
				json.has( "Condition" ) ? json.get( "Condition" ).toString() : ""
		);
	}

	public String getSid() {
		return sid;
	}

	public String getEffect() {
		return effect;
	}

	public String getPrincipalJsonValue() {
		return principalJsonValue;
	}

	public String getActionJsonValue() {
		return actionJsonValue;
	}

	public String getResourceJsonValue() {
		return resourceJsonValue;
	}

	public String getConditionJsonValue() {
		return conditionJsonValue;
	}
}
