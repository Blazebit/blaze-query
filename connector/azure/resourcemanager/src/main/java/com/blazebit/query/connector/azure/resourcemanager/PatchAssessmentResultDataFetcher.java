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
 * A data fetcher for VM patch assessment results via the Azure Resource Graph
 * {@code patchassessmentresources} table. Returns the latest assessment per VM.
 *
 * <p>Requires {@link AzureResourceManagerConnectorConfig#RESOURCE_GRAPH_CLIENT} to be configured
 * with a {@link ResourceGraphClientAccessor} that provides an authenticated
 * {@link com.azure.resourcemanager.resourcegraph.ResourceGraphManager}.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class PatchAssessmentResultDataFetcher implements DataFetcher<AzureResourcePatchAssessmentResult>, Serializable {

	private static final String KQL_QUERY =
			"patchassessmentresources"
			+ " | where type in~ ('microsoft.compute/virtualmachines/patchassessmentresults',"
			+ "'microsoft.hybridcompute/machines/patchassessmentresults')"
			+ " | parse id with resourceId '/patchAssessmentResults' *"
			+ " | project properties, resourceId=tolower(resourceId)"
			+ " | summarize arg_max(tostring(properties.lastModifiedDateTime), *) by resourceId";

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static final PatchAssessmentResultDataFetcher INSTANCE = new PatchAssessmentResultDataFetcher();

	private PatchAssessmentResultDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AzureResourcePatchAssessmentResult.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}

	@Override
	public List<AzureResourcePatchAssessmentResult> fetch(DataFetchContext context) {
		try {
			List<ResourceGraphClientAccessor> accessors =
					AzureResourceManagerConnectorConfig.RESOURCE_GRAPH_CLIENT.getAll( context );
			List<AzureResourcePatchAssessmentResult> list = new ArrayList<>();
			for ( ResourceGraphClientAccessor accessor : accessors ) {
				QueryRequest request = new QueryRequest( KQL_QUERY )
						.setSubscriptions( accessor.getSubscriptionIds() );
				String skipToken = null;
				do {
					if ( skipToken != null ) {
						request.setOptions( new QueryRequestOptions().setSkipToken( skipToken ) );
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
			throw new DataFetcherException( "Could not fetch patch assessment results", e );
		}
	}

	private static void parseResponse(
			String tenantId,
			QueryResponse response,
			List<AzureResourcePatchAssessmentResult> list) throws Exception {
		String dataJson = OBJECT_MAPPER.writeValueAsString( response.data() );
		JsonNode root = OBJECT_MAPPER.readTree( dataJson );

		JsonNode columns = root.get( "columns" );
		JsonNode rows = root.get( "rows" );
		if ( rows == null || !rows.isArray() || columns == null ) {
			return;
		}

		int propertiesIdx = -1;
		int resourceIdIdx = -1;
		for ( int i = 0; i < columns.size(); i++ ) {
			String name = columns.get( i ).path( "name" ).asText();
			if ( "properties".equals( name ) ) {
				propertiesIdx = i;
			}
			else if ( "resourceId".equals( name ) ) {
				resourceIdIdx = i;
			}
		}

		for ( JsonNode row : rows ) {
			JsonNode propertiesNode = propertiesIdx >= 0 ? row.get( propertiesIdx ) : null;
			String resourceId = resourceIdIdx >= 0 ? row.get( resourceIdIdx ).asText() : null;
			list.add( AzureResourcePatchAssessmentResult.fromJson( tenantId, resourceId, propertiesNode ) );
		}
	}
}
