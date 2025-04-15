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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.6
 */
public class GitHubGraphQlClient {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final int DEFAULT_PAGE_SIZE = 100;
	private static final String GITHUB_GRAPHQL_ENDPOINT = "https://api.github.com/graphql";

	private final HttpClient httpClient;
	private final String authToken;

	public GitHubGraphQlClient(String authToken) {
		this.httpClient = HttpClient.newHttpClient();
		this.authToken = authToken;
	}

	public List<GitHubRepository> fetchRepositoriesWithDetails() {
		// First, fetch repositories with minimal nested details
		List<GitHubRepository> repositories = fetchRepositoriesBasic();

		// Then, fetch detailed rulesets and branch protection rules separately
		return repositories.stream()
				.map(this::enrichRepositoryDetails)
				.collect( Collectors.toList());
	}

	private List<GitHubRepository> fetchRepositoriesBasic() {
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
						forkingAllowed
						createdAt
						visibility
						defaultBranchRef {
							id
							name
						}
					}
				}
			}
		}
		""";

		return executePaginatedQuery(query, variables, "viewer.repositories", this::extractRepositoriesBasic);
	}

	private List<GitHubRuleset> fetchRepositoryRulesets(String repositoryId) {
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
							target
							enforcement
							conditions {
								refName {
									include
								}
							}
							rules(first: 100) {
								nodes {
									type
									parameters {
										... on PullRequestParameters {
											requireCodeOwnerReview
											requiredApprovingReviewCount
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

	private List<GitHubBranchProtectionRule> fetchRepositoryBranchProtectionRules(String repositoryId) {
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
							matchingRefs(first: 100) {
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

	private List<GitHubRepository> extractRepositoriesBasic(JsonNode rootNode) {
		List<GitHubRepository> repositories = new ArrayList<>();

		for (JsonNode repoNode : rootNode.path("nodes")) {
			if (!repoNode.isMissingNode()) {
				repositories.add( GitHubRepository.fromJson(repoNode.toString()));
			}
		}

		return repositories;
	}

	private List<GitHubRuleset> extractRulesets(JsonNode rootNode) {
		List<GitHubRuleset> rulesets = new ArrayList<>();

		for (JsonNode rulesetNode : rootNode.path("nodes")) {
			if (!rulesetNode.isMissingNode()) {
				rulesets.add( GitHubRuleset.fromJson(rulesetNode.toString()));
			}
		}

		return rulesets;
	}

	private List<GitHubBranchProtectionRule> extractBranchProtectionRules(JsonNode rootNode) {
		List<GitHubBranchProtectionRule> rules = new ArrayList<>();

		for (JsonNode ruleNode : rootNode.path("nodes")) {
			if (!ruleNode.isMissingNode()) {
				rules.add( GitHubBranchProtectionRule.fromJson(ruleNode.toString()));
			}
		}

		return rules;
	}

	private static List<GitHubOrganization> extractOrganizations(JsonNode rootNode) {
		List<GitHubOrganization> organizations = new ArrayList<>();

		for (JsonNode repoNode : rootNode.path("nodes")) {
			if (!repoNode.isMissingNode()) {
				organizations.add( GitHubOrganization.fromJson(repoNode.toString()));
			}
		}

		return organizations;
	}

	private GitHubRepository enrichRepositoryDetails(GitHubRepository baseRepo) {
		// Fetch rulesets for this specific repository
		List<GitHubRuleset> rulesets = fetchRepositoryRulesets(baseRepo.id());

		// Fetch branch protection rules for this repository
		List<GitHubBranchProtectionRule> branchProtectionRules =
				fetchRepositoryBranchProtectionRules(baseRepo.id());

		// Create a new repository with enriched details
		return new GitHubRepository(
				baseRepo.id(),
				baseRepo.name(),
				baseRepo.description(),
				baseRepo.isArchived(),
				baseRepo.isDisabled(),
				baseRepo.isInOrganization(),
				baseRepo.isEmpty(),
				baseRepo.isPrivate(),
				baseRepo.forkingAllowed(),
				baseRepo.visibility(),
				baseRepo.createdAt(),
				baseRepo.defaultBranchRef(),
				rulesets,
				branchProtectionRules
		);
	}

	public <T> List<T> executePaginatedQuery(
			String query,
			Map<String, Object> variables,
			String rootNode,
			JsonPathExtractor<T> extractor
	) {
		List<T> allResults = new ArrayList<>();
		String cursor = null;
		boolean hasNextPage;

		do {
			// Reset cursor and set page size for each iteration
			variables.put("cursor", cursor);
			variables.put("first", DEFAULT_PAGE_SIZE);

			String requestBody = createJsonRequest(query, variables);

			try {
				HttpRequest request = HttpRequest.newBuilder()
						.uri(URI.create(GITHUB_GRAPHQL_ENDPOINT))
						.header("Authorization", "Bearer " + authToken)
						.header("Content-Type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString(requestBody))
						.build();

				HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

				if (response.statusCode() != 200) {
					throw new RuntimeException("GitHub API error: " + response.body());
				}

				JsonNode jsonResponse = MAPPER.readTree(response.body());

				// Check for errors in the response
				JsonNode errors = jsonResponse.path("errors");
				if (errors.isArray() && !errors.isEmpty() ) {
					throw new RuntimeException("GitHub GraphQL error: " + errors );
				}

				JsonNode data = jsonResponse.path("data");

				// Navigate to the root node
				for (String node : rootNode.split("\\.")) {
					data = data.path(node);
				}

				JsonNode pageInfo = data.path("pageInfo");

				// Use the provided extractor function to get results
				List<T> extractedResults = extractor.extract(data);
				allResults.addAll(extractedResults);

				cursor = pageInfo.path("endCursor").asText(null);
				hasNextPage = pageInfo.path("hasNextPage").asBoolean(false);
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to fetch " + rootNode + " from GitHub GraphQL API", e);
			}
		}
		while (hasNextPage && cursor != null);

		return allResults;
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
