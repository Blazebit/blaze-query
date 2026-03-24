/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link GitHubGraphQlClient#executePaginatedQuery} partial error handling.
 */
class GitHubGraphQlClientTest {

	private HttpServer server;
	private String serverUrl;

	@BeforeEach
	void startServer() throws Exception {
		server = HttpServer.create( new InetSocketAddress( 0 ), 0 );
		serverUrl = "http://localhost:" + server.getAddress().getPort() + "/graphql";
	}

	@AfterEach
	void stopServer() {
		server.stop( 0 );
	}

	@Test
	void should_return_partial_data_when_response_contains_errors_alongside_data() {
		// GitHub returns errors for specific fields (e.g. repositoryName condition) but still
		// provides the rest of the ruleset data. The client should log a warning and return the data.
		String partialResponse = """
				{
				"data": {
					"node": {
					"rulesets": {
						"pageInfo": { "endCursor": null, "hasNextPage": false },
						"nodes": [
						{ "id": "RS_abc", "target": "BRANCH", "enforcement": "ACTIVE",
							"conditions": { "repositoryName": null },
							"source": { "__typename": "Repository", "id": "REPO_1" },
							"rules": { "nodes": [] }
						}
						]
					}
					}
				},
				"errors": [
					{ "type": "FORBIDDEN",
					"path": ["node","rulesets","nodes",0,"conditions","repositoryName"],
					"message": "Resource not accessible by integration" }
				]
				}
				""";

		server.createContext( "/graphql", exchange -> {
			byte[] body = partialResponse.getBytes( StandardCharsets.UTF_8 );
			exchange.getResponseHeaders().set( "Content-Type", "application/json" );
			exchange.sendResponseHeaders( 200, body.length );
			exchange.getResponseBody().write( body );
			exchange.getResponseBody().close();
		} );
		server.start();

		GitHubGraphQlClient client = new GitHubGraphQlClient( "test-token", serverUrl );

		Map<String, Object> variables = new HashMap<>();
		variables.put( "repositoryId", "REPO_1" );

		List<GitHubRuleset> results = client.executePaginatedQuery(
				"query($repositoryId: ID!, $first: Int, $cursor: String) { node(id: $repositoryId) { ... on Repository { rulesets(first: $first, after: $cursor) { pageInfo { endCursor hasNextPage } nodes { id } } } } }",
				variables,
				"node.rulesets",
				data -> {
					// extract id from each node
					List<GitHubRuleset> rulesets = new java.util.ArrayList<>();
					data.path( "nodes" ).forEach( node ->
							rulesets.add( new GitHubRuleset(
									node.path( "id" ).asText(),
									node.path( "target" ).asText( null ),
									node.path( "enforcement" ).asText( null ),
									null, null, null, null, List.of() ) )
					);
					return rulesets;
				}
		);

		assertThat( results ).hasSize( 1 );
		assertThat( results.get( 0 ).id() ).isEqualTo( "RS_abc" );
	}

	@Test
	void should_throw_when_response_contains_errors_and_no_data() {
		String errorOnlyResponse = """
				{
				"errors": [
					{ "type": "UNAUTHORIZED", "message": "Bad credentials" }
				]
				}
				""";

		server.createContext( "/graphql", exchange -> {
			byte[] body = errorOnlyResponse.getBytes( StandardCharsets.UTF_8 );
			exchange.getResponseHeaders().set( "Content-Type", "application/json" );
			exchange.sendResponseHeaders( 200, body.length );
			exchange.getResponseBody().write( body );
			exchange.getResponseBody().close();
		} );
		server.start();

		GitHubGraphQlClient client = new GitHubGraphQlClient( "bad-token", serverUrl );

		Map<String, Object> variables = new HashMap<>();

		assertThatThrownBy( () -> client.executePaginatedQuery(
				"query { viewer { login } }",
				variables,
				"viewer",
				data -> List.of()
		) ).isInstanceOf( RuntimeException.class )
				.cause()
				.hasMessageContaining( "GitHub GraphQL error" );
	}
}
