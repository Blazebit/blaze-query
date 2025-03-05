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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.4
 */
public class PostgreSqlFlexibleServerParameterDataFetcher implements DataFetcher<AzureResourcePostgreSqlFlexibleServerWithParameters>, Serializable {
	public static final PostgreSqlFlexibleServerParameterDataFetcher INSTANCE = new PostgreSqlFlexibleServerParameterDataFetcher();

	private static final List<String> PARAMETERS_TO_FETCH = Arrays.asList(
			"ssl_min_protocol_version"
	);

	private PostgreSqlFlexibleServerParameterDataFetcher() {
	}

	@Override
	public List<AzureResourcePostgreSqlFlexibleServerWithParameters> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManagerPostgreSqlManager> postgreSqlResourceManagers =
					AzureResourceManagerPostgreSqlManagerConnectorConfig.POSTGRESQL_MANAGER.getAll(context);

			List<AzureResourcePostgreSqlFlexibleServerWithParameters> serverParametersList = new ArrayList<>();

			for (AzureResourceManagerPostgreSqlManager resourceManager : postgreSqlResourceManagers) {
				for (Server postgreSqlFlexibleServer : resourceManager.getManager().servers().list()) {
					Map<String, String> serverParameters = new HashMap<>();

					for (String parameterName : PARAMETERS_TO_FETCH) {
						try {
							Object parameterValue = resourceManager.getManager().configurations().get(
									postgreSqlFlexibleServer.resourceGroupName(),
									postgreSqlFlexibleServer.name(),
									parameterName
							).value();

							serverParameters.put(
									parameterName,
									parameterValue != null ? parameterValue.toString() : null
							);
						} catch (Exception e) {
							serverParameters.put(parameterName, null);
						}
					}

					serverParametersList.add(new AzureResourcePostgreSqlFlexibleServerWithParameters(
							resourceManager.getTenantId(),
							postgreSqlFlexibleServer.id(),
							postgreSqlFlexibleServer.innerModel(),
							serverParameters
					));
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
