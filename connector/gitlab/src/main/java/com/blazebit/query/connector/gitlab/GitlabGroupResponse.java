/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitlabGroupResponse {
	@JsonProperty("data")
	private Data data;

	public List<GitlabGroup> getGroups() {
		return data.groups.nodes;
	}

	private static class Data {
		@JsonProperty("groups")
		private Groups groups;
	}

	private static class Groups {
		@JsonProperty("nodes")
		private List<GitlabGroup> nodes;
	}
}
