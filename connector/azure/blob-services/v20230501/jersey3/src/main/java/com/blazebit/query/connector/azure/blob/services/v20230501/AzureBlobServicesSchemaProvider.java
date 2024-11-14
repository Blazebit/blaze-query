/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.blob.services.v20230501;

import java.util.Map;

import com.blazebit.query.connector.azure.blob.services.v20230501.model.BlobServiceProperties;
import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

/**
 * The schema provider for the Azure Blob Services connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AzureBlobServicesSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public AzureBlobServicesSchemaProvider() {
	}

	@Override
	public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Map.<Class<?>, DataFetcher<?>>of(
				BlobServiceProperties.class, BlobServicesDataFetcher.INSTANCE
		);
	}
}
