/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.postgresqlflexibleserver.models.Server;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martijn Sprengers
 * @since 1.0.3
 */
public class PostgreSqlFlexibleServerDataFetcher implements DataFetcher<AzureResourcePostgreSqlFlexibleServer>, Serializable {

	public static final PostgreSqlFlexibleServerDataFetcher INSTANCE = new PostgreSqlFlexibleServerDataFetcher();

	private PostgreSqlFlexibleServerDataFetcher() {
	}

	@Override
	public List<AzureResourcePostgreSqlFlexibleServer> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManagerPostgreSqlManager> postgreSqlResourceManagers = AzureResourceManagerPostgreSqlManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll(
					context );
			List<AzureResourcePostgreSqlFlexibleServer> list = new ArrayList<>();
			for ( AzureResourceManagerPostgreSqlManager resourceManager : postgreSqlResourceManagers ) {
				for ( Server postgreSqlFlexibleServer : resourceManager.getManager().servers().list() ) {
					list.add( new AzureResourcePostgreSqlFlexibleServer(
							resourceManager.getTenantId(),
							postgreSqlFlexibleServer.id(),
							postgreSqlFlexibleServer.innerModel()
					) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch postgreSqlFlexibleServer list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AzureResourcePostgreSqlFlexibleServer.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}
}
