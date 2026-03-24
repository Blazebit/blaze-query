/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.6
 */
public class GitHubGraphQlClient {

	private static final Logger LOG = Logger.getLogger( GitHubGraphQlClient.class.getName() );
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();
	private static final int DEFAULT_PAGE_SIZE = 100; // Must be within the range of 1-100
	private static final String GITHUB_GRAPHQL_ENDPOINT = "https://api.github.com/graphql";
	private static final int MAX_RETRIES = 3;
	private static final long RETRY_BASE_DELAY_MS = 1000L;

	private final HttpClient httpClient;
	private final String authToken;
	private final String endpoint;

	public GitHubGraphQlClient(String authToken) {
		this(authToken, GITHUB_GRAPHQL_ENDPOINT);
	}

	GitHubGraphQlClient(String authToken, String endpoint) {
		this.httpClient = HttpClient.newHttpClient();
		this.endpoint = endpoint;
		this.authToken = authToken;
	}

	public List<GitHubRepository> fetchRepositories() {
		Map<String, Object> variables = new HashMap<>();
		variables.put("ownerAffiliation", "OWNER");

		String query = """
		query($ownerAffiliation: RepositoryAffiliation!, $first: Int, $cursor: String) {
			viewer {
				repositories(first: $first, after: $cursor, ownerAffiliations: [$ownerAffiliation]) {
					pageInfo {
						endCursor
						hasNextPage
					}
					nodes {
						id
						name
						description
						isArchived
						isDisabled
						isInOrganization
						isEmpty
						isPrivate
						isFork
						forkingAllowed
						createdAt
						visibility
						owner {
							__typename
							id
							login
						}
						defaultBranchRef {
							id
							name
						}
					}
				}
			}
		}
		""";

		return executePaginatedQuery(query, variables, "viewer.repositories", this::extractRepositories);
	}

	public List<GitHubOrganization> fetchOrganizations() {
		Map<String, Object> variables = new HashMap<>();

		String query = """
		query($first: Int, $cursor: String) {
			viewer {
				organizations(first: $first, after: $cursor) {
					pageInfo {
						endCursor
						hasNextPage
					}
					nodes {
						id
						name
						requiresTwoFactorAuthentication
					}
				}
			}
		}
		""";

		return executePaginatedQuery(query, variables, "viewer.organizations", GitHubGraphQlClient::extractOrganizations);
	}

	public List<GitHubRuleset> fetchRepositoryRulesets(String repositoryId) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("repositoryId", repositoryId);

		String query = """
		query($repositoryId: ID!, $first: Int, $cursor: String) {
			node(id: $repositoryId) {
				... on Repository {
					rulesets(first: $first, after: $cursor) {
						pageInfo {
							endCursor
							hasNextPage
						}
						nodes {
							id
							target
							enforcement
							source {
								__typename
								... on Repository{
									id
								}
								... on Organization{
									id
								}
								... on Enterprise{
									id
								}
							}
							conditions {
								refName {
									include
									exclude
								}
								repositoryId {
									repositoryIds
								}
								repositoryName {
									exclude
									include
								}
							}
							rules(first: $first) {
								nodes {
									type
									parameters {
										... on CopilotCodeReviewParameters {
											reviewDraftPullRequests
											reviewOnPush
										}
										... on PullRequestParameters {
											requireCodeOwnerReview
											requiredApprovingReviewCount
											dismissStaleReviewsOnPush
											requireLastPushApproval
											requiredReviewThreadResolution
										}
										... on RequiredStatusChecksParameters {
											strictRequiredStatusChecksPolicy
										}
									}
								}
							}
						}
					}
				}
			}
		}
		""";

		return executePaginatedQuery(query, variables, "node.rulesets", this::extractRulesets);
	}

	public List<GitHubBranchProtectionRule> fetchRepositoryBranchProtectionRules(String repositoryId) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("repositoryId", repositoryId);

		String query = """
		query($repositoryId: ID!, $first: Int, $cursor: String) {
			node(id: $repositoryId) {
				... on Repository {
					branchProtectionRules(first: $first, after: $cursor) {
						pageInfo {
							endCursor
							hasNextPage
						}
						nodes {
							id
							allowsForcePushes
							requiresCodeOwnerReviews
							allowsDeletions
							isAdminEnforced
							requireLastPushApproval
							requiredApprovingReviewCount
							requiresConversationResolution
							restrictsReviewDismissals
							requiresCommitSignatures
							requiresStatusChecks
							requiresStrictStatusChecks
							dismissesStaleReviews
							requiresApprovingReviews
							repository {
								id
								name
							}
							matchingRefs(first: $first) {
								nodes {
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

		return executePaginatedQuery(query, variables, "node.branchProtectionRules", this::extractBranchProtectionRules);
	}

	public List<GitHubPullRequest> fetchRepositoryPullRequestsSince(String repositoryId, String branchName, OffsetDateTime since) {
		return fetchRepositoryPullRequests( repositoryId, branchName,
				pr -> pr.createdAt() != null && !pr.createdAt().isBefore( since ) );
	}

	public List<GitHubPullRequest> fetchRepositoryPullRequests(String repositoryId, String branchName) {
		return fetchRepositoryPullRequests( repositoryId, branchName, null );
	}

	private List<GitHubPullRequest> fetchRepositoryPullRequests(String repositoryId, String branchName, Predicate<GitHubPullRequest> keepWhile) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("repositoryId", repositoryId);
		variables.put("defaultBranchName", branchName);

		String query = """
		query($repositoryId: ID!, $first: Int, $cursor: String, $defaultBranchName: String!) {
			node(id: $repositoryId) {
				... on Repository {
					pullRequests(
						first: $first,
						after: $cursor,
						states: [MERGED],
						baseRefName: $defaultBranchName,
						orderBy: {field: CREATED_AT, direction: DESC}) {
						pageInfo {
							endCursor
							hasNextPage
						}
						nodes {
							id
							title
							createdAt
							closed
							closedAt
							merged
							mergedAt
							state
							reviewDecision
							repository {
								id
								name
							}
							baseRef {
								id
								name
							}
						}
					}
				}
			}
		}
		""";

		return executePaginatedQuery(query, variables, "node.pullRequests", this::extractPullRequests, keepWhile);
	}

	public List<GitHubRuleset> fetchOrganizationRulesets(String organizationId) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("organizationId", organizationId);

		String query = """
		query($organizationId: ID!, $first: Int, $cursor: String) {
			node(id: $organizationId) {
				... on Organization {
					rulesets(first: $first, after: $cursor) {
						pageInfo {
							endCursor
							hasNextPage
						}
						nodes {
							target
							enforcement
							source {
								__typename
								... on Repository{
									id
								}
								... on Organization{
									id
								}
								... on Enterprise{
									id
								}
							}
							conditions {
								refName {
									include
									exclude
								}
								repositoryId {
									repositoryIds
								}
								repositoryName {
									exclude
									include
								}
							}
							rules(first: $first) {
								nodes {
									type
									parameters {
										... on CopilotCodeReviewParameters {
											reviewDraftPullRequests
											reviewOnPush
										}
										... on PullRequestParameters {
											requireCodeOwnerReview
											requiredApprovingReviewCount
											dismissStaleReviewsOnPush
											requireLastPushApproval
											requiredReviewThreadResolution
										}
										... on RequiredStatusChecksParameters {
											strictRequiredStatusChecksPolicy
										}
									}
								}
							}
						}
					}
				}
			}
		}
		""";

		return executePaginatedQuery(query, variables, "node.rulesets", this::extractRulesets);
	}

	private List<GitHubRepository> extractRepositories(JsonNode rootNode) {
		List<GitHubRepository> repositories = new ArrayList<>();

		for (JsonNode repoNode : rootNode.path("nodes")) {
			if (!repoNode.isMissingNode()) {
				repositories.add(GitHubRepository.fromJson(repoNode.toString()));
			}
		}

		return repositories;
	}

	private List<GitHubRuleset> extractRulesets(JsonNode rootNode) {
		List<GitHubRuleset> rulesets = new ArrayList<>();

		for (JsonNode rulesetNode : rootNode.path("nodes")) {
			if (!rulesetNode.isMissingNode()) {
				rulesets.add(GitHubRuleset.fromJson(rulesetNode.toString()));
			}
		}

		return rulesets;
	}

	private List<GitHubBranchProtectionRule> extractBranchProtectionRules(JsonNode rootNode) {
		List<GitHubBranchProtectionRule> rules = new ArrayList<>();

		for (JsonNode ruleNode : rootNode.path("nodes")) {
			if (!ruleNode.isMissingNode()) {
				rules.add(GitHubBranchProtectionRule.fromJson(ruleNode.toString()));
			}
		}

		return rules;
	}

	private List<GitHubPullRequest> extractPullRequests(JsonNode rootNode) {
		List<GitHubPullRequest> pullRequests = new ArrayList<>();

		for (JsonNode prNode : rootNode.path("nodes")) {
			if (!prNode.isMissingNode()) {
				pullRequests.add(GitHubPullRequest.fromJson(prNode.toString()));
			}
		}

		return pullRequests;
	}

	private static List<GitHubOrganization> extractOrganizations(JsonNode rootNode) {
		List<GitHubOrganization> organizations = new ArrayList<>();

		for (JsonNode orgNode : rootNode.path("nodes")) {
			if (!orgNode.isMissingNode()) {
				organizations.add(GitHubOrganization.fromJson(orgNode.toString()));
			}
		}

		return organizations;
	}

	public <T> List<T> executePaginatedQuery(
			String query,
			Map<String, Object> variables,
			String rootNode,
			JsonPathExtractor<T> extractor
	) {
		return executePaginatedQuery( query, variables, rootNode, extractor, null );
	}

	public <T> List<T> executePaginatedQuery(
			String query,
			Map<String, Object> variables,
			String rootNode,
			JsonPathExtractor<T> extractor,
			Predicate<T> keepWhile
	) {
		List<T> allResults = new ArrayList<>();
		String cursor = null;
		boolean hasNextPage;

		do {
			variables.put("cursor", cursor);
			variables.put("first", DEFAULT_PAGE_SIZE);

			String requestBody = createJsonRequest(query, variables);

			try {
				HttpRequest request = HttpRequest.newBuilder()
						.uri(URI.create(endpoint))
						.header("Authorization", "Bearer " + authToken)
						.header("Content-Type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString(requestBody))
						.build();

				HttpResponse<String> response = sendWithRetries( request, rootNode );

				JsonNode jsonResponse = MAPPER.readTree(response.body());

				JsonNode errors = jsonResponse.path("errors");
				if (errors.isArray() && !errors.isEmpty()) {
					JsonNode data = jsonResponse.path("data");
					if (data.isMissingNode() || data.isNull()) {
						throw new RuntimeException("GitHub GraphQL error: " + errors);
					}
					LOG.log( Level.WARNING, "GitHub GraphQL returned partial data with errors: {0}", errors );
				}

				JsonNode data = jsonResponse.path("data");

				// Navigate to the root node
				for (String node : rootNode.split("\\.")) {
					data = data.path(node);
				}

				JsonNode pageInfo = data.path("pageInfo");

				List<T> extractedResults = extractor.extract(data);

				if ( keepWhile != null ) {
					for ( T item : extractedResults ) {
						if ( keepWhile.test( item ) ) {
							allResults.add( item );
						}
						else {
							// Results are ordered DESC, so once an item fails the predicate,
							// all subsequent items will also fail — stop paginating.
							return allResults;
						}
					}
				}
				else {
					allResults.addAll(extractedResults);
				}

				cursor = pageInfo.path("endCursor").asText(null);
				hasNextPage = pageInfo.path("hasNextPage").asBoolean(false);
			}
			catch (RuntimeException e) {
				throw e;
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to fetch " + rootNode + " from GitHub GraphQL API", e);
			}
		}
		while (hasNextPage && cursor != null);

		return allResults;
	}

	private HttpResponse<String> sendWithRetries(HttpRequest request, String rootNode) throws Exception {
		HttpResponse<String> response = null;
		for ( int attempt = 1; attempt <= MAX_RETRIES; attempt++ ) {
			response = httpClient.send( request, HttpResponse.BodyHandlers.ofString() );
			if ( !isRetryableStatus( response.statusCode() ) ) {
				break;
			}
			LOG.log( Level.WARNING, "GitHub API returned {0}, retrying (attempt {1}/{2})",
					new Object[]{ response.statusCode(), attempt, MAX_RETRIES } );
			if ( attempt < MAX_RETRIES ) {
				try {
					Thread.sleep( RETRY_BASE_DELAY_MS * attempt );
				}
				catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					throw new RuntimeException( "Interrupted during retry delay for " + rootNode, ie );
				}
			}
		}
		if ( response != null && isRetryableStatus( response.statusCode() ) ) {
			throw new RuntimeException( "GitHub API returned " + response.statusCode()
					+ " after " + MAX_RETRIES + " attempts: " + response.body() );
		}
		if ( response.statusCode() != 200 ) {
			throw new RuntimeException( "GitHub API error: " + response.body() );
		}
		return response;
	}

	private static boolean isRetryableStatus(int statusCode) {
		return statusCode >= 500 || statusCode == 429;
	}

	private String createJsonRequest(String query, Map<String, Object> variables) {
		try {
			Map<String, Object> requestMap = Map.of(
					"query", query,
					"variables", variables
			);

			return MAPPER.writeValueAsString(requestMap);
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to create JSON request", e);
		}
	}

	@FunctionalInterface
	public interface JsonPathExtractor<T> {
		List<T> extract(JsonNode rootNode);
	}
}
