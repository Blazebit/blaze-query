/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitlabGraphQlClient {

	private static final int DEFAULT_PAGE_SIZE = 100; // GitLab's default pagination size

	private final HttpClient httpClient;
	private final String gitlabApiUrl;
	private final String authToken;

	public GitlabGraphQlClient(String host, String gitlabToken) {
		this.httpClient = HttpClient.newHttpClient();
		this.gitlabApiUrl = host + "/api/graphql";
		this.authToken = gitlabToken;
	}

//	public List<GitlabUser> fetchUsers() {
//		String query = """
//			{
//			"query": "query { users(ids: [\\"gid://gitlab/User/21369228\\"]) { nodes { id name username lastActivityOn active } } }"
//			}
//		""";
//
//		return executeQuery(query, "users", GitlabUser.class);
//	}

	public List<GitlabUser> fetchUsers(List<String> userIds) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("ids", userIds);

		String query = """
			query ($ids: [ID!]) {
				users(ids: $ids) {
					nodes { id name username lastActivityOn active avatarUrl bio bot commitEmail createdAt discord gitpodEnabled groupCount human jobTitle linkedin location organization pronouns publicEmail twitter webPath webUrl }
				}
			}
		""";

		return executeQuery(query, variables, "users", GitlabUser::fromJson);
	}

//	public List<GitlabProject> fetchProjects() {
//		String query = """
//			{
//			"query": "query { projects(membership: true) { nodes { id name defaultBranch } } }"
//			}
//		""";
//
//		return executeQuery(query, "projects", GitlabProject.class);
//	}

	public List<GitlabProject> fetchProjects(boolean membership) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("membership", membership);

		String query = """
			query ($membership: Boolean, $first: Int, $cursor: String) {
				projects(membership: $membership, first: $first, after: $cursor) {
					pageInfo { endCursor hasNextPage }
					nodes { id name defaultBranch }
				}
			}
		""";

		return executePaginatedQuery(query, variables, "projects", GitlabProject::fromJson);
	}

//	public List<GitlabGroup> fetchGroups() {
//		String query = """
//			{
//			"query": "query { groups(ownedOnly: true) { nodes { id name path requireTwoFactorAuthentication twoFactorGracePeriod } } }"
//			}
//		""";
//
//		return executeQuery(query, "groups", GitlabGroup.class);
//	}

	public List<GitlabGroup> fetchGroups(boolean ownedOnly) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("ownedOnly", ownedOnly);

		String query = """
			query ($ownedOnly: Boolean, $first: Int, $cursor: String) {
				groups(ownedOnly: $ownedOnly, first: $first, after: $cursor) {
					pageInfo { endCursor hasNextPage }
					nodes { id name path requireTwoFactorAuthentication twoFactorGracePeriod }
				}
			}
		""";

		return executePaginatedQuery(query, variables, "groups", GitlabGroup::fromJson);
	}

	private <T> List<T> executePaginatedQuery(String query, Map<String, Object> variables, String rootNode, JsonParser<T> parser) {
		List<T> allResults = new ArrayList<>();
		String cursor = null;
		boolean hasNextPage;

		do {
			variables.put("cursor", cursor);
			variables.put("first", DEFAULT_PAGE_SIZE); // Default page size

			String requestBody = createJsonRequest(query, variables);

			try {
				HttpRequest request = HttpRequest.newBuilder()
						.uri(URI.create(gitlabApiUrl))
						.header("Authorization", "Bearer " + authToken)
						.header("Content-Type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString(requestBody))
						.build();

				HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

				if (response.statusCode() != 200) {
					throw new RuntimeException("GitLab API error: " + response.body());
				}

				JSONObject jsonResponse = new JSONObject(response.body());
				JSONObject data = jsonResponse.getJSONObject("data").getJSONObject(rootNode);
				JSONArray nodes = data.getJSONArray("nodes");
				JSONObject pageInfo = data.getJSONObject("pageInfo");

				for (int i = 0; i < nodes.length(); i++) {
					allResults.add(parser.parse(nodes.getJSONObject(i)));
				}

				cursor = pageInfo.optString("endCursor", null);
				hasNextPage = pageInfo.optBoolean("hasNextPage", false);

			} catch (Exception e) {
				throw new RuntimeException("Failed to fetch " + rootNode + " from GitLab GraphQL API", e);
			}

		} while (hasNextPage && cursor != null);

		return allResults;
	}

	private <T> List<T> executeQuery(String query, Map<String, Object> variables, String rootNode, JsonParser<T> parser) {
		try {
			String requestBody = createJsonRequest(query, variables);

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(gitlabApiUrl))
					.header("Authorization", "Bearer " + authToken)
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(requestBody))
					.build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != 200) {
				throw new RuntimeException("GitLab API error: " + response.body());
			}

			JSONObject jsonResponse = new JSONObject(response.body());
			JSONArray nodes = jsonResponse.getJSONObject("data").getJSONObject(rootNode).getJSONArray("nodes");

			List<T> resultList = new ArrayList<>();
			for (int i = 0; i < nodes.length(); i++) {
				resultList.add(parser.parse(nodes.getJSONObject(i)));
			}
			return resultList;
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch " + rootNode + " from GitLab GraphQL API", e);
		}
	}

	private String createJsonRequest(String query, Map<String, Object> variables) {
		JSONObject json = new JSONObject();
		json.put("query", query);
		json.put("variables", new JSONObject(variables));
		return json.toString();
	}

	@FunctionalInterface
	interface JsonParser<T> {
		T parse(JSONObject json);
	}
}
