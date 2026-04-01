/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.linear;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link LinearGraphQlClient} HTTP behaviour: auth header, pagination, and error handling.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
class LinearGraphQlClientTest {

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
	void should_send_api_key_without_bearer_prefix() {
		AtomicReference<String> capturedAuth = new AtomicReference<>();

		String response = """
				{
					"data": {
						"teams": {
							"pageInfo": { "hasNextPage": false, "endCursor": null },
							"nodes": []
						}
					}
				}
				""";

		server.createContext( "/graphql", exchange -> {
			capturedAuth.set( exchange.getRequestHeaders().getFirst( "Authorization" ) );
			byte[] body = response.getBytes( StandardCharsets.UTF_8 );
			exchange.getResponseHeaders().set( "Content-Type", "application/json" );
			exchange.sendResponseHeaders( 200, body.length );
			exchange.getResponseBody().write( body );
			exchange.getResponseBody().close();
		} );
		server.start();

		new LinearGraphQlClient( "lin_api_testkey123", serverUrl ).fetchTeams();

		assertThat( capturedAuth.get() ).isEqualTo( "lin_api_testkey123" );
		assertThat( capturedAuth.get() ).doesNotStartWith( "Bearer" );
	}

	@Test
	void should_return_nodes_from_single_page_response() {
		String response = """
				{
					"data": {
						"teams": {
							"pageInfo": { "hasNextPage": false, "endCursor": null },
							"nodes": [
								{ "id": "team-1", "name": "Security", "key": "SEC",
								"description": "Security team", "private": false,
								"createdAt": "2025-01-01T00:00:00.000Z",
								"updatedAt": "2026-01-01T00:00:00.000Z" }
							]
						}
					}
				}
				""";

		server.createContext( "/graphql", exchange -> {
			byte[] body = response.getBytes( StandardCharsets.UTF_8 );
			exchange.getResponseHeaders().set( "Content-Type", "application/json" );
			exchange.sendResponseHeaders( 200, body.length );
			exchange.getResponseBody().write( body );
			exchange.getResponseBody().close();
		} );
		server.start();

		List<LinearTeam> teams = new LinearGraphQlClient( "lin_api_key", serverUrl ).fetchTeams();

		assertThat( teams ).hasSize( 1 );
		assertThat( teams.get( 0 ).id() ).isEqualTo( "team-1" );
		assertThat( teams.get( 0 ).name() ).isEqualTo( "Security" );
	}

	@Test
	void should_follow_pagination_cursor() throws Exception {
		String page1 = """
				{
					"data": {
						"teams": {
							"pageInfo": { "hasNextPage": true, "endCursor": "cursor-abc" },
							"nodes": [
								{ "id": "team-1", "name": "Security", "key": "SEC",
								"description": null, "private": false,
								"createdAt": "2025-01-01T00:00:00.000Z",
								"updatedAt": "2025-01-01T00:00:00.000Z" }
							]
						}
					}
				}
				""";
		String page2 = """
				{
					"data": {
						"teams": {
							"pageInfo": { "hasNextPage": false, "endCursor": null },
							"nodes": [
								{ "id": "team-2", "name": "Engineering", "key": "ENG",
								"description": null, "private": false,
								"createdAt": "2025-01-01T00:00:00.000Z",
								"updatedAt": "2025-01-01T00:00:00.000Z" }
							]
						}
					}
				}
				""";

		AtomicReference<String> secondRequestBody = new AtomicReference<>();
		int[] callCount = { 0 };

		server.createContext( "/graphql", exchange -> {
			callCount[0]++;
			String responseBody = callCount[0] == 1 ? page1 : page2;
			if ( callCount[0] == 2 ) {
				secondRequestBody.set( new String( exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8 ) );
			}
			byte[] body = responseBody.getBytes( StandardCharsets.UTF_8 );
			exchange.getResponseHeaders().set( "Content-Type", "application/json" );
			exchange.sendResponseHeaders( 200, body.length );
			exchange.getResponseBody().write( body );
			exchange.getResponseBody().close();
		} );
		server.start();

		List<LinearTeam> teams = new LinearGraphQlClient( "lin_api_key", serverUrl ).fetchTeams();

		assertThat( teams ).hasSize( 2 );
		assertThat( teams ).extracting( LinearTeam::id ).containsExactly( "team-1", "team-2" );
		assertThat( callCount[0] ).isEqualTo( 2 );
		assertThat( secondRequestBody.get() ).contains( "cursor-abc" );
	}

	@Test
	void should_throw_when_response_contains_errors_and_no_data() {
		String errorResponse = """
				{
					"errors": [
						{ "message": "Authentication required", "extensions": { "code": "UNAUTHENTICATED" } }
					]
				}
				""";

		server.createContext( "/graphql", exchange -> {
			byte[] body = errorResponse.getBytes( StandardCharsets.UTF_8 );
			exchange.getResponseHeaders().set( "Content-Type", "application/json" );
			exchange.sendResponseHeaders( 200, body.length );
			exchange.getResponseBody().write( body );
			exchange.getResponseBody().close();
		} );
		server.start();

		LinearGraphQlClient client = new LinearGraphQlClient( "bad-key", serverUrl );

		assertThatThrownBy( () -> client.executePaginatedQuery(
				"query { teams { nodes { id } } }",
				new HashMap<>(),
				"teams",
				node -> node.path( "id" ).asText()
		) ).isInstanceOf( RuntimeException.class )
				.hasMessageContaining( "Linear GraphQL error" );
	}

	@Test
	void should_throw_on_non_200_response() {
		server.createContext( "/graphql", exchange -> {
			byte[] body = "Unauthorized".getBytes( StandardCharsets.UTF_8 );
			exchange.sendResponseHeaders( 401, body.length );
			exchange.getResponseBody().write( body );
			exchange.getResponseBody().close();
		} );
		server.start();

		LinearGraphQlClient client = new LinearGraphQlClient( "bad-key", serverUrl );

		assertThatThrownBy( client::fetchTeams )
				.isInstanceOf( RuntimeException.class )
				.hasMessageContaining( "401" );
	}
}
