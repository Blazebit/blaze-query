/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public abstract class AwsPolicyWrapper {

	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	private final String accountId;
	private final String region;
	private final String resourceId;
	private final String id;
	private final String version;
	private final List<AwsPolicyStatement> statement;

	protected AwsPolicyWrapper(String accountId, String region, String resourceId, String policy) {
		this( accountId, region, resourceId, policy, false );
	}

	protected AwsPolicyWrapper(
			String accountId,
			String region,
			String resourceId,
			String policy,
			boolean urlEncoded) {
		this.accountId = accountId;
		this.region = region;
		this.resourceId = resourceId;
		try {
			String policyContent = urlEncoded
					? URLDecoder.decode( policy, StandardCharsets.UTF_8 )
					: policy;
			JsonNode json = MAPPER.readTree( policyContent );
			this.id = json.has( "Id" ) ? json.get( "Id" ).asText( "" ) : "";
			this.version = json.has( "Version" ) ? json.get( "Version" ).asText( "" ) : "";
			this.statement = parseStatements( json );
		}
		catch (Exception e) {
			throw new RuntimeException( "Error parsing JSON for " + getClass().getSimpleName(), e );
		}
	}

	private static List<AwsPolicyStatement> parseStatements(JsonNode json) {
		if ( !json.has( "Statement" ) ) {
			return List.of();
		}
		JsonNode statementNode = json.get( "Statement" );
		if ( statementNode.isArray() ) {
			return StreamSupport.stream( statementNode.spliterator(), false )
					.map( AwsPolicyStatement::fromJson )
					.collect( Collectors.toList() );
		}
		else {
			return List.of( AwsPolicyStatement.fromJson( statementNode ) );
		}
	}

	public String getAccountId() {
		return accountId;
	}

	public String getRegion() {
		return region;
	}

	public String getResourceId() {
		return resourceId;
	}

	public String getId() {
		return id;
	}

	public String getVersion() {
		return version;
	}

	public List<AwsPolicyStatement> getStatement() {
		return statement;
	}
}
