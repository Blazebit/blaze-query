/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class GitlabGraphQlClient {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final HttpClient httpClient;
	private final String gitlabApiUrl;
	private final String authToken;

	public GitlabGraphQlClient(String host, String gitlabToken) {
		this.httpClient = HttpClient.newHttpClient();
		this.gitlabApiUrl = host + "/api/graphql";
		this.authToken = gitlabToken;
	}

	public List<GitlabUser> fetchUsers() {
		String query = """
			{
			"query": "query { users(ids: [\\"gid://gitlab/User/21369228\\"]) { nodes { id name username lastActivityOn active } } }"
			}
		""";

		return executeQuery(query, "users", GitlabUser.class);
	}
	public List<GitlabProject> fetchProjects() {
		String query = """
			{
			"query": "query { projects(membership: true) { nodes { id name defaultBranch } } }"
			}
		""";

		return executeQuery(query, "projects", GitlabProject.class);
	}

	public List<GitlabGroup> fetchGroups() {
		String query = """
			{
			"query": "query { groups(ownedOnly: true) { nodes { id name path requireTwoFactorAuthentication twoFactorGracePeriod } } }"
			}
		""";

		return executeQuery(query, "groups", GitlabGroup.class);
	}

	private <T> List<T> executeQuery(String query, String rootNode, Class<T> responseType) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(gitlabApiUrl))
					.header("Authorization", "Bearer " + authToken)
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(query))
					.build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != 200) {
				throw new RuntimeException("GitLab API error: " + response.body());
			}

			JsonNode jsonResponse = OBJECT_MAPPER.readTree(response.body());
			JsonNode nodes = jsonResponse.path("data").path(rootNode).path("nodes");

			if (nodes.isMissingNode()) {
				return Collections.emptyList();
			}

			return OBJECT_MAPPER.readerForListOf(responseType).readValue(nodes);
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch " + rootNode + " from GitLab GraphQL API", e);
		}
	}
}
