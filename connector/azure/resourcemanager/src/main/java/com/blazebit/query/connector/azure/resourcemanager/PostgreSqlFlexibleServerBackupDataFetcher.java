/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.core.management.exception.ManagementException;
import com.azure.resourcemanager.postgresqlflexibleserver.models.Server;
import com.azure.resourcemanager.postgresqlflexibleserver.models.ServerBackup;
import com.azure.resourcemanager.postgresqlflexibleserver.models.ServerState;
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
					if ( !postgreSqlFlexibleServer.state().equals( ServerState.READY )
							&& postgreSqlFlexibleServer.name().toLowerCase().contains( "ltrclone" ) ) {
						continue;
					}
					try {
						for ( ServerBackup postgreSqlFlexibleServerBackup : resourceManager.getManager().backups().listByServer( postgreSqlFlexibleServer.resourceGroupName(), postgreSqlFlexibleServer.name() ) ) {
							list.add( new AzureResourcePostgreSqlFlexibleServerBackup(
									resourceManager.getTenantId(),
									postgreSqlFlexibleServerBackup.id(),
									postgreSqlFlexibleServerBackup.innerModel(),
									postgreSqlFlexibleServer.innerModel().id()
							) );
						}
					}
					catch ( ManagementException e ) {
						if ( e.getResponse().getStatusCode() == 404
								&& postgreSqlFlexibleServer.name().toLowerCase().contains( "ltrclone" ) ) {
							// LTR clone references may not exist as actual servers, skip silently
							continue;
						}
						throw e;
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch postgreSqlFlexibleServerBackup list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AzureResourcePostgreSqlFlexibleServerBackup.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}
}
