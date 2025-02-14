/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitlabUserResponse {

	@JsonProperty("data")
	private Data data;

	public List<GitlabUser> getUsers() {
		return data.users.nodes;
	}

	private static class Data {
		@JsonProperty("users")
		private Users users;
	}

	private static class Users {
		@JsonProperty("nodes")
		private List<GitlabUser> nodes;
	}
}
