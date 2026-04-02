/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.resourcegraph.models.QueryRequest;
import com.azure.resourcemanager.resourcegraph.models.QueryRequestOptions;
import com.azure.resourcemanager.resourcegraph.models.QueryResponse;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A data fetcher for Azure Defender for Cloud security assessments via the Azure Resource Graph
 * {@code securityresources} table. Returns one entry per assessment per resource.
 *
 * <p>Requires {@link AzureResourceManagerConnectorConfig#RESOURCE_GRAPH_CLIENT} to be configured
 * with a {@link ResourceGraphClientAccessor} that provides an authenticated
 * {@link com.azure.resourcemanager.resourcegraph.ResourceGraphManager}.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class SecurityAssessmentDataFetcher implements DataFetcher<AzureResourceSecurityAssessment>, Serializable {

	private static final String KQL_QUERY =
			"securityresources"
			+ " | where type =~ 'microsoft.security/assessments'"
			+ " | project"
			+ "     id,"
			+ "     resourceId = tolower(tostring(properties.resourceDetails.id)),"
			+ "     displayName = tostring(properties.displayName),"
			+ "     statusCode = tostring(properties.status.code),"
			+ "     severity = tostring(properties.metadata.severity)";

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static final SecurityAssessmentDataFetcher INSTANCE = new SecurityAssessmentDataFetcher();

	private SecurityAssessmentDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AzureResourceSecurityAssessment.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}

	@Override
	public List<AzureResourceSecurityAssessment> fetch(DataFetchContext context) {
		try {
			List<ResourceGraphClientAccessor> accessors =
					AzureResourceManagerConnectorConfig.RESOURCE_GRAPH_CLIENT.getAll( context );
			List<AzureResourceSecurityAssessment> list = new ArrayList<>();
			for ( ResourceGraphClientAccessor accessor : accessors ) {
				QueryRequest request = new QueryRequest()
						.withQuery( KQL_QUERY )
						.withSubscriptions( accessor.getSubscriptionIds() );
				String skipToken = null;
				do {
					if ( skipToken != null ) {
						request.withOptions( new QueryRequestOptions().withSkipToken( skipToken ) );
					}
					QueryResponse response = accessor.getManager().resourceProviders().resources( request );
					skipToken = response.skipToken();
					parseResponse( accessor.getTenantId(), response, list );
				} while ( skipToken != null );
			}
			return list;
		}
		catch (DataFetcherException e) {
			throw e;
		}
		catch (Exception e) {
			throw new DataFetcherException( "Could not fetch security assessments", e );
		}
	}

	private static void parseResponse(
			String tenantId,
			QueryResponse response,
			List<AzureResourceSecurityAssessment> list) throws Exception {
		String dataJson = OBJECT_MAPPER.writeValueAsString( response.data() );
		JsonNode root = OBJECT_MAPPER.readTree( dataJson );

		JsonNode columns = root.get( "columns" );
		JsonNode rows = root.get( "rows" );
		if ( rows == null || !rows.isArray() || columns == null ) {
			return;
		}

		int idIdx = -1;
		int resourceIdIdx = -1;
		int displayNameIdx = -1;
		int statusCodeIdx = -1;
		int severityIdx = -1;
		for ( int i = 0; i < columns.size(); i++ ) {
			String name = columns.get( i ).path( "name" ).asText();
			switch ( name ) {
				case "id":
					idIdx = i;
					break;
				case "resourceId":
					resourceIdIdx = i;
					break;
				case "displayName":
					displayNameIdx = i;
					break;
				case "statusCode":
					statusCodeIdx = i;
					break;
				case "severity":
					severityIdx = i;
					break;
				default:
					break;
			}
		}

		for ( JsonNode row : rows ) {
			String assessmentId = idIdx >= 0 ? row.get( idIdx ).asText() : null;
			String subscriptionId = assessmentId != null && assessmentId.contains( "/" )
					? assessmentId.split( "/" )[2] : null;
			String resourceId = resourceIdIdx >= 0 ? row.get( resourceIdIdx ).asText() : null;
			String displayName = displayNameIdx >= 0 ? row.get( displayNameIdx ).asText() : null;
			String statusCode = statusCodeIdx >= 0 ? row.get( statusCodeIdx ).asText() : null;
			String severity = severityIdx >= 0 ? row.get( severityIdx ).asText() : null;
			list.add( new AzureResourceSecurityAssessment(
					tenantId, assessmentId, subscriptionId, resourceId, displayName, statusCode, severity ) );
		}
	}
}
