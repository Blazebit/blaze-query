/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.linear;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTP client for the Linear GraphQL API. Authenticated via a Personal API Key.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public class LinearGraphQlClient {

	private static final Logger LOG = Logger.getLogger( LinearGraphQlClient.class.getName() );
	private static final String LINEAR_GRAPHQL_ENDPOINT = "https://api.linear.app/graphql";
	private static final int DEFAULT_PAGE_SIZE = 50;
	private static final int MAX_RETRIES = 3;
	private static final long RETRY_BASE_DELAY_MS = 1000L;

	static final ObjectMapper MAPPER;

	static {
		MAPPER = new ObjectMapper();
		MAPPER.registerModule( new JavaTimeModule() );
	}

	private final HttpClient httpClient;
	private final String apiKey;
	private final String endpoint;

	public LinearGraphQlClient(String apiKey) {
		this( apiKey, LINEAR_GRAPHQL_ENDPOINT );
	}

	LinearGraphQlClient(String apiKey, String endpoint) {
		this.httpClient = HttpClient.newHttpClient();
		this.apiKey = apiKey;
		this.endpoint = endpoint;
	}

	public List<LinearIssue> fetchIssues() {
		String query = """
				query($first: Int, $after: String) {
					issues(first: $first, after: $after) {
						pageInfo {
							hasNextPage
							endCursor
						}
						nodes {
							id
							identifier
							title
							priority
							priorityLabel
							createdAt
							updatedAt
							dueDate
							completedAt
							canceledAt
							url
							state {
								id
								name
								type
							}
							team {
								id
								name
								key
							}
							assignee {
								id
								name
								email
							}
							creator {
								id
								name
								email
							}
							labels {
								nodes {
									id
									name
								}
							}
						}
					}
				}
				""";
		return executePaginatedQuery( query, new HashMap<>(), "issues", LinearIssue::fromJson );
	}

	public List<LinearUser> fetchUsers() {
		String query = """
				query($first: Int, $after: String) {
					users(first: $first, after: $after) {
						pageInfo {
							hasNextPage
							endCursor
						}
						nodes {
							id
							name
							displayName
							email
							active
							admin
							guest
							createdAt
							updatedAt
						}
					}
				}
				""";
		return executePaginatedQuery( query, new HashMap<>(), "users", LinearUser::fromJson );
	}

	public List<LinearTeam> fetchTeams() {
		String query = """
				query($first: Int, $after: String) {
					teams(first: $first, after: $after) {
						pageInfo {
							hasNextPage
							endCursor
						}
						nodes {
							id
							name
							key
							description
							private
							createdAt
							updatedAt
						}
					}
				}
				""";
		return executePaginatedQuery( query, new HashMap<>(), "teams", LinearTeam::fromJson );
	}

	public List<LinearWorkflowState> fetchWorkflowStates() {
		String query = """
				query($first: Int, $after: String) {
					workflowStates(first: $first, after: $after) {
						pageInfo {
							hasNextPage
							endCursor
						}
						nodes {
							id
							name
							type
							color
							position
							team {
								id
								name
								key
							}
						}
					}
				}
				""";
		return executePaginatedQuery( query, new HashMap<>(), "workflowStates", LinearWorkflowState::fromJson );
	}

	<T> List<T> executePaginatedQuery(
			String query,
			Map<String, Object> variables,
			String rootNode,
			NodeExtractor<T> extractor) {
		List<T> allResults = new ArrayList<>();
		String cursor = null;
		boolean hasNextPage;

		do {
			variables.put( "first", DEFAULT_PAGE_SIZE );
			variables.put( "after", cursor );

			String requestBody = buildRequestBody( query, variables );

			try {
				HttpRequest request = HttpRequest.newBuilder()
						.uri( URI.create( endpoint ) )
						.header( "Authorization", apiKey )
						.header( "Content-Type", "application/json" )
						.POST( HttpRequest.BodyPublishers.ofString( requestBody ) )
						.build();

				HttpResponse<String> response = sendWithRetries( request, rootNode );

				JsonNode json = MAPPER.readTree( response.body() );

				JsonNode errors = json.path( "errors" );
				if ( errors.isArray() && !errors.isEmpty() ) {
					JsonNode data = json.path( "data" );
					if ( data.isMissingNode() || data.isNull() ) {
						throw new RuntimeException( "Linear GraphQL error: " + errors );
					}
					LOG.log( Level.WARNING, "Linear GraphQL returned partial data with errors: {0}", errors );
				}

				JsonNode connectionNode = json.path( "data" ).path( rootNode );

				JsonNode pageInfo = connectionNode.path( "pageInfo" );
				hasNextPage = pageInfo.path( "hasNextPage" ).asBoolean( false );
				cursor = pageInfo.path( "endCursor" ).asText( null );
				if ( cursor != null && cursor.isEmpty() ) {
					cursor = null;
				}

				for ( JsonNode node : connectionNode.path( "nodes" ) ) {
					allResults.add( extractor.extract( node ) );
				}
			}
			catch (IOException e) {
				throw new RuntimeException( "Failed to fetch " + rootNode + " from Linear GraphQL API", e );
			}
		}
		while ( hasNextPage && cursor != null );

		return allResults;
	}

	private HttpResponse<String> sendWithRetries(HttpRequest request, String rootNode) throws IOException {
		HttpResponse<String> response = null;
		try {
			for ( int attempt = 1; attempt <= MAX_RETRIES; attempt++ ) {
				response = httpClient.send( request, HttpResponse.BodyHandlers.ofString() );
				if ( !isRetryable( response.statusCode() ) ) {
					break;
				}
				LOG.log( Level.WARNING, "Linear API returned {0}, retrying (attempt {1}/{2})",
						new Object[]{ response.statusCode(), attempt, MAX_RETRIES } );
				if ( attempt < MAX_RETRIES ) {
					Thread.sleep( RETRY_BASE_DELAY_MS * attempt );
				}
			}
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException( "Interrupted during Linear API request for " + rootNode, e );
		}
		if ( response != null && isRetryable( response.statusCode() ) ) {
			throw new RuntimeException( "Linear API returned " + response.statusCode()
					+ " after " + MAX_RETRIES + " attempts for " + rootNode );
		}
		if ( response.statusCode() != 200 ) {
			throw new RuntimeException( "Linear API error " + response.statusCode() + " for " + rootNode
					+ ": " + response.body() );
		}
		return response;
	}

	private static boolean isRetryable(int statusCode) {
		return statusCode == 429 || statusCode >= 500;
	}

	private String buildRequestBody(String query, Map<String, Object> variables) {
		try {
			return MAPPER.writeValueAsString( Map.of( "query", query, "variables", variables ) );
		}
		catch (Exception e) {
			throw new RuntimeException( "Failed to serialize Linear GraphQL request", e );
		}
	}

	@FunctionalInterface
	interface NodeExtractor<T> {
		T extract(JsonNode node);
	}
}
