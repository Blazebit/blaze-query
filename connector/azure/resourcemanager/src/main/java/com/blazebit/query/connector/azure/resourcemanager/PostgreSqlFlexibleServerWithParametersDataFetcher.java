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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.4
 */
public class PostgreSqlFlexibleServerWithParametersDataFetcher implements DataFetcher<AzureResourcePostgreSqlFlexibleServerWithParameters>, Serializable {
	public static final PostgreSqlFlexibleServerWithParametersDataFetcher INSTANCE = new PostgreSqlFlexibleServerWithParametersDataFetcher();

	private PostgreSqlFlexibleServerWithParametersDataFetcher() {
	}

	@Override
	public List<AzureResourcePostgreSqlFlexibleServerWithParameters> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManagerPostgreSqlManager> postgreSqlResourceManagers =
					AzureResourceManagerPostgreSqlManagerConnectorConfig.POSTGRESQL_MANAGER.getAll(context);

			List<String> parametersToFetch = AzureResourceManagerPostgreSqlManagerConnectorConfig.PARAMETERS_TO_FETCH.getAll(context);

			List<AzureResourcePostgreSqlFlexibleServerWithParameters> serverParametersList = new ArrayList<>();

		if (!parametersToFetch.isEmpty()) {
			for (AzureResourceManagerPostgreSqlManager resourceManager : postgreSqlResourceManagers) {
			for (Server postgreSqlFlexibleServer : resourceManager.getManager().servers().list()) {
				Map<String, String> serverParameters = new HashMap<>();

				for (String parameterName : parametersToFetch) {
				try {
					Object parameterValue =
						resourceManager
							.getManager()
							.configurations()
							.get(
								postgreSqlFlexibleServer.resourceGroupName(),
								postgreSqlFlexibleServer.name(),
								parameterName)
							.value();

					serverParameters.put(
						parameterName, parameterValue != null ? parameterValue.toString() : null);
				} catch (Exception e) {
					serverParameters.put(parameterName, null);
				}
				}

				serverParametersList.add(
					new AzureResourcePostgreSqlFlexibleServerWithParameters(
						resourceManager.getTenantId(),
						postgreSqlFlexibleServer.id(),
						postgreSqlFlexibleServer.innerModel(),
						serverParameters));
			}
			}
		}
			return serverParametersList;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException("Could not fetch server parameters", e);
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention(
				AzureResourcePostgreSqlFlexibleServerWithParameters.class,
				AzureResourceManagerConventionContext.INSTANCE
		);
	}
}
