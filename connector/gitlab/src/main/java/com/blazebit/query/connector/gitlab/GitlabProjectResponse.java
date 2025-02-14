/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitlabProjectResponse {

	@JsonProperty("data")
	private Data data;

	public List<GitlabProject> getProjects() {
		return data.projects.nodes;
	}

	private static class Data {
		@JsonProperty("projects")
		private Projects projects;
	}

	private static class Projects {
		@JsonProperty("nodes")
		private List<GitlabProject> nodes;
	}
}
