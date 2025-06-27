/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.postgresqlflexibleserver.models.Server;
import com.azure.resourcemanager.postgresqlflexibleserver.models.ServerBackup;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.4
 */
public class PostgreSqlFlexibleServerBackupDataFetcher implements DataFetcher<AzureResourcePostgreSqlFlexibleServerBackup>, Serializable {
	public static final PostgreSqlFlexibleServerBackupDataFetcher INSTANCE = new PostgreSqlFlexibleServerBackupDataFetcher();

	private PostgreSqlFlexibleServerBackupDataFetcher() {
	}

	@Override
	public List<AzureResourcePostgreSqlFlexibleServerBackup> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManagerPostgreSqlManager> postgreSqlResourceManagers = AzureResourceManagerPostgreSqlManagerConnectorConfig.POSTGRESQL_MANAGER.getAll(
					context );
			List<AzureResourcePostgreSqlFlexibleServerBackup> list = new ArrayList<>();
			for ( AzureResourceManagerPostgreSqlManager resourceManager : postgreSqlResourceManagers ) {
				for ( Server postgreSqlFlexibleServer : resourceManager.getManager().servers().list() ) {
					for ( ServerBackup postgreSqlFlexibleServerBackup : resourceManager.getManager().backups().listByServer( postgreSqlFlexibleServer.resourceGroupName(), postgreSqlFlexibleServer.name() ) ) {
						list.add( new AzureResourcePostgreSqlFlexibleServerBackup(
								resourceManager.getTenantId(),
								postgreSqlFlexibleServerBackup.id(),
								postgreSqlFlexibleServerBackup.innerModel(),
								postgreSqlFlexibleServer.innerModel().id()
						) );
					}

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
		return DataFormats.componentMethodConvention( AzureResourcePostgreSqlFlexibleServerBackup.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}
}
