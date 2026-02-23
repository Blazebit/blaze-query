/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.devops;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.devops.api.WorkItemsApi;
import com.blazebit.query.connector.devops.invoker.ApiClient;
import com.blazebit.query.connector.devops.invoker.ApiException;
import com.blazebit.query.connector.devops.model.Wiql;
import com.blazebit.query.connector.devops.model.WorkItemList;
import com.blazebit.query.connector.devops.model.WorkItemQueryResult;
import com.blazebit.query.connector.devops.model.WorkItemReference;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fetches Azure DevOps work items via the Work Item Tracking (WIT) REST API.
 * Uses a two-step approach: first runs a WIQL query to obtain work item IDs,
 * then batch-fetches the full work item data in groups of up to 200.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class WorkItemDataFetcher implements DataFetcher<WorkItem>, Serializable {

	public static final WorkItemDataFetcher INSTANCE = new WorkItemDataFetcher();

	private static final int BATCH_SIZE = 200;
	private static final String DEFAULT_WIQL = "SELECT [System.Id] FROM WorkItems";

	private WorkItemDataFetcher() {
	}

	@Override
	public List<WorkItem> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = DevopsConnectorConfig.WIT_API_CLIENT.getAll( context );
			List<String> organizations = DevopsConnectorConfig.ORGANIZATION.getAll( context );
			String wiqlQuery = DevopsConnectorConfig.WIQL_QUERY.find( context );
			if ( wiqlQuery == null ) {
				wiqlQuery = DEFAULT_WIQL;
			}

			List<WorkItem> list = new ArrayList<>();
			for ( int i = 0; i < apiClients.size(); i++ ) {
				ApiClient apiClient = apiClients.get( i );
				String organization = organizations.get( i );
				WorkItemsApi api = new WorkItemsApi( apiClient );

				WorkItemQueryResult result = api.workItemsQueryWiql(
						organization, "7.1", new Wiql().query( wiqlQuery ) );
				List<WorkItemReference> refs = result.getWorkItems();
				if ( refs == null || refs.isEmpty() ) {
					continue;
				}

				for ( int j = 0; j < refs.size(); j += BATCH_SIZE ) {
					List<WorkItemReference> batch = refs.subList( j, Math.min( j + BATCH_SIZE, refs.size() ) );
					String ids = batch.stream()
							.map( r -> r.getId().toString() )
							.collect( Collectors.joining( "," ) );
					WorkItemList items = api.workItemsList( organization, ids, "7.1", "fields" );
					if ( items.getValue() != null ) {
						items.getValue().stream().map( WorkItem::new ).forEach( list::add );
					}
				}
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch work item list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( WorkItem.class, DevopsConventionContext.INSTANCE );
	}
}
