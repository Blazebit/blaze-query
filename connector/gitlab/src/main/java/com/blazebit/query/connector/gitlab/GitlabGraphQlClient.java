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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
public class GitlabGraphQlClient {

	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();
	private static final int DEFAULT_PAGE_SIZE = 100; // GitLab's default pagination size

	private final HttpClient httpClient;
	private final String gitlabApiUrl;
	private final String authToken;

	public GitlabGraphQlClient(String host, String gitlabToken) {
		this.httpClient = HttpClient.newHttpClient();
		this.gitlabApiUrl = host + "/api/graphql";
		this.authToken = gitlabToken;
	}

	private static List<GitlabUser> extractUsersFromProjects(JsonNode rootNode) {
		List<GitlabUser> users = new ArrayList<>();

		// Traverse edges -> node -> projectMembers -> edges -> node -> user
		for ( JsonNode projectEdge : rootNode.path( "edges" ) ) {
			JsonNode projectNode = projectEdge.path( "node" );
			JsonNode projectMembers = projectNode.path( "projectMembers" );

			for ( JsonNode memberEdge : projectMembers.path( "edges" ) ) {
				JsonNode userNode = memberEdge.path( "node" ).path( "user" );
				if ( !userNode.isMissingNode() ) {
					users.add( GitlabUser.fromJson( userNode.toString() ) );
				}
			}
		}
		return users;
	}

	private static List<GitlabUser> extractUsersFromGroups(JsonNode rootNode) {
		List<GitlabUser> users = new ArrayList<>();

		// Traverse edges -> node -> groupMembers -> edges -> node -> user
		for ( JsonNode groupEdge : rootNode.path( "edges" ) ) {
			JsonNode groupNode = groupEdge.path( "node" );
			JsonNode groupMembers = groupNode.path( "groupMembers" );

			for ( JsonNode memberEdge : groupMembers.path( "edges" ) ) {
				JsonNode userNode = memberEdge.path( "node" ).path( "user" );
				if ( !userNode.isMissingNode() ) {
					users.add( GitlabUser.fromJson( userNode.toString() ) );
				}
			}
		}
		return users;
	}

	private static List<GitlabProject> extractProjects(JsonNode rootNode) {
		List<GitlabProject> projects = new ArrayList<>();

		for ( JsonNode projectEdge : rootNode.path( "edges" ) ) {
			JsonNode projectNode = projectEdge.path( "node" );
			if ( !projectNode.isMissingNode() ) {
				projects.add( GitlabProject.fromJson( projectNode.toString() ) );
			}
		}

		return projects;
	}

	private static List<GitlabGroup> extractGroups(JsonNode rootNode) {
		List<GitlabGroup> groups = new ArrayList<>();

		for ( JsonNode groupEdge : rootNode.path( "edges" ) ) {
			JsonNode groupNode = groupEdge.path( "node" );
			if ( !groupNode.isMissingNode() ) {
				groups.add( GitlabGroup.fromJson( groupNode.toString() ) );
			}
		}

		return groups;
	}

	private static List<GitlabMergeRequest> extractMergeRequestsFromProjects(JsonNode rootNode) {
		List<GitlabMergeRequest> mergeRequests = new ArrayList<>();

		JsonNode projectsNodes = rootNode.path("nodes");
		for (JsonNode projectNode : projectsNodes) {
			JsonNode mergeRequestsNode = projectNode.path("mergeRequests");

			for (JsonNode mergeRequestNode : mergeRequestsNode.path("nodes")) {
				if (!mergeRequestNode.isMissingNode()) {
					mergeRequests.add(GitlabMergeRequest.fromJson(mergeRequestNode.toString()));
				}
			}
		}
		return mergeRequests;
	}

	public List<GitlabUser> fetchUsers(List<String> userIds) {
		Map<String, Object> variables = new HashMap<>();
		variables.put( "ids", userIds );

		String query = """
					query ($ids: [ID!]) {
						users(ids: $ids) {
							nodes { id name username lastActivityOn active avatarUrl bio bot commitEmail createdAt discord gitpodEnabled groupCount human jobTitle linkedin location organization pronouns publicEmail twitter webPath webUrl }
						}
					}
				""";

		return executeQuery( query, variables, "users", GitlabUser::fromJson );
	}

	public List<GitlabUser> fetchUsersFromProjects(boolean membership) {
		Map<String, Object> variables = new HashMap<>();
		variables.put( "membership", membership );

		String query = """
					query ($membership: Boolean, $first: Int, $cursor: String) {
						projects(membership: $membership, first: $first, after: $cursor) {
							pageInfo { endCursor hasNextPage }
							edges {
								node {
									projectMembers {
										edges {
											node {
												user {
													id
													name
													username
													lastActivityOn
													avatarUrl
													publicEmail
												}
											}
										}
									}
								}
							}
						}
					}
				""";

		return executePaginatedQuery( query, variables, "projects", GitlabGraphQlClient::extractUsersFromProjects );
	}

	public List<GitlabUser> fetchUsersFromGroups(boolean membership) {
		Map<String, Object> variables = new HashMap<>();
		variables.put( "membership", membership );

		String query = """
					query ($membership: Boolean, $first: Int, $cursor: String) {
						groups(ownedOnly: $membership, first: $first, after: $cursor) {
							pageInfo { endCursor hasNextPage }
							edges {
								node {
									groupMembers {
										edges {
											node {
												user {
													id
													name
													username
													lastActivityOn
													avatarUrl
													publicEmail
												}
											}
										}
									}
								}
							}
						}
					}
				""";

		return executePaginatedQuery( query, variables, "groups", GitlabGraphQlClient::extractUsersFromGroups );
	}

	public List<GitlabUser> fetchUsersFromProjectsAndGroups(boolean membership) {
		List<GitlabUser> projectUsers = fetchUsersFromProjects( membership );
		List<GitlabUser> groupUsers = fetchUsersFromGroups( membership );

		// Merge results, removing duplicates
		Set<String> uniqueIds = new HashSet<>();
		List<GitlabUser> allUsers = new ArrayList<>();

		for ( GitlabUser user : projectUsers ) {
			if ( uniqueIds.add( user.id() ) ) {
				allUsers.add( user );
			}
		}
		for ( GitlabUser user : groupUsers ) {
			if ( uniqueIds.add( user.id() ) ) {
				allUsers.add( user );
			}
		}

		return allUsers;
	}

	public List<GitlabProject> fetchProjects(boolean membership) {
		Map<String, Object> variables = new HashMap<>();
		variables.put( "membership", membership );

		String query = """
					query ($membership: Boolean, $first: Int, $cursor: String) {
						projects(membership: $membership, first: $first, after: $cursor) {
							pageInfo { endCursor hasNextPage }
							edges {
								node {
									id
									name
									archived
									avatarUrl
									createdAt
									description
									lastActivityAt
									path
									updatedAt
									group { id }
									repository { rootRef }
									mergeRequestsEnabled
									branchRules {
										edges {
											node {
												id
												name
												isDefault
												isProtected
												branchProtection {
													allowForcePush
													codeOwnerApprovalRequired
												}
											}
										}
									}
								}
							}
						}
					}
				""";

		return executePaginatedQuery( query, variables, "projects", GitlabGraphQlClient::extractProjects );
	}

	public List<GitlabGroup> fetchGroups(boolean ownedOnly) {
		Map<String, Object> variables = new HashMap<>();
		variables.put( "ownedOnly", ownedOnly );

		String query = """
					query ($ownedOnly: Boolean, $first: Int, $cursor: String) {
						groups(ownedOnly: $ownedOnly, first: $first, after: $cursor) {
							pageInfo { endCursor hasNextPage }
							edges {
								node {
									id
									name
									path
									requireTwoFactorAuthentication
									twoFactorGracePeriod
								}
							}
						}
					}
				""";

		return executePaginatedQuery( query, variables, "groups", GitlabGraphQlClient::extractGroups );
	}

	public List<GitlabMergeRequest> fetchMergeRequestsFromProjects(GitlabMergeRequestState state) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("membership", true);
		variables.put("state", state);

		String query = """
				query ($membership: Boolean, $state: MergeRequestState, $first: Int, $cursor: String) {
					projects(membership: $membership, first: $first, after: $cursor) {
						pageInfo { endCursor hasNextPage }
						nodes {
							name
							mergeRequests(state: $state, first: $first, after: $cursor) {
								pageInfo { endCursor hasNextPage }
								nodes {
								id
								name
								title
								state
								approved
								approvalsRequired
								createdAt
								mergedAt
								targetBranch
									targetProjectId
								author{
									id
									name
								}
								approvedBy{
									nodes{
									id
									name
									}
								}
								}
							}
						}
					}
				}
			""";

		return executePaginatedQuery(query, variables, "projects", GitlabGraphQlClient::extractMergeRequestsFromProjects);
	}

	private <T> List<T> executePaginatedQuery(
			String query,
			Map<String, Object> variables,
			String rootNode,
			JsonPathExtractor<T> extractor
	) {
		List<T> allResults = new ArrayList<>();
		String cursor = null;
		boolean hasNextPage;

		do {
			variables.put( "cursor", cursor );
			variables.put( "first", DEFAULT_PAGE_SIZE );

			String requestBody = createJsonRequest( query, variables );

			try {
				HttpRequest request = HttpRequest.newBuilder()
						.uri( URI.create( gitlabApiUrl ) )
						.header( "Authorization", "Bearer " + authToken )
						.header( "Content-Type", "application/json" )
						.POST( HttpRequest.BodyPublishers.ofString( requestBody ) )
						.build();

				HttpResponse<String> response = httpClient.send( request, HttpResponse.BodyHandlers.ofString() );

				if ( response.statusCode() != 200 ) {
					throw new RuntimeException( "GitLab API error: " + response.body() );
				}

				// Parse JSON response using Jackson
				JsonNode jsonResponse = MAPPER.readTree( response.body() );
				JsonNode data = jsonResponse.path( "data" ).path( rootNode );
				JsonNode pageInfo = data.path( "pageInfo" );

				// Use the provided extractor function to get results
				List<T> extractedResults = extractor.extract( data );
				allResults.addAll( extractedResults );

				cursor = pageInfo.path( "endCursor" ).asText( null );
				hasNextPage = pageInfo.path( "hasNextPage" ).asBoolean( false );

			}
			catch (Exception e) {
				throw new RuntimeException( "Failed to fetch " + rootNode + " from GitLab GraphQL API", e );
			}

		}
		while ( hasNextPage && cursor != null );

		return allResults;
	}

	private <T> List<T> executeQuery(String query, Map<String, Object> variables, String rootNode, JsonParser<T> parser) {
		try {
			String requestBody = createJsonRequest( query, variables );

			HttpRequest request = HttpRequest.newBuilder()
					.uri( URI.create( gitlabApiUrl ) )
					.header( "Authorization", "Bearer " + authToken )
					.header( "Content-Type", "application/json" )
					.POST( HttpRequest.BodyPublishers.ofString( requestBody ) )
					.build();

			HttpResponse<String> response = httpClient.send( request, HttpResponse.BodyHandlers.ofString() );

			if ( response.statusCode() != 200 ) {
				throw new RuntimeException( "GitLab API error: " + response.body() );
			}

			// Parse JSON response using Jackson
			JsonNode jsonResponse = MAPPER.readTree( response.body() );

			JsonNode dataNode = jsonResponse.path( "data" ).path( rootNode ).path( "nodes" );
			if ( !dataNode.isArray() ) {
				throw new RuntimeException( "Unexpected response structure: " + response.body() );
			}

			List<T> resultList = new ArrayList<>();
			for ( JsonNode node : dataNode ) {
				resultList.add( parser.parse( node.toString() ) ); // Convert JSON node to String and parse
			}

			return resultList;
		}
		catch (Exception e) {
			throw new RuntimeException( "Failed to fetch " + rootNode + " from GitLab GraphQL API", e );
		}
	}

	private String createJsonRequest(String query, Map<String, Object> variables) {
		try {
			Map<String, Object> requestMap = Map.of(
					"query", query,
					"variables", variables
			);

			return MAPPER.writeValueAsString( requestMap );
		}
		catch (Exception e) {
			throw new RuntimeException( "Failed to create JSON request", e );
		}
	}

	@FunctionalInterface
	interface JsonParser<T> {
		T parse(String json);
	}

	@FunctionalInterface
	interface JsonPathExtractor<T> {
		List<T> extract(JsonNode rootNode);
	}
}
