/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.defender;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * A data fetcher for fetching Microsoft Defender for Endpoint machines.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class DefenderMachineDataFetcher implements DataFetcher<DefenderMachine>, Serializable {

	private static final String MACHINES_URL = "https://api.securitycenter.microsoft.com/api/machines";
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static final DefenderMachineDataFetcher INSTANCE = new DefenderMachineDataFetcher();

	private DefenderMachineDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DefenderMachine.class, DefenderConventionContext.INSTANCE );
	}

	@Override
	public List<DefenderMachine> fetch(DataFetchContext context) {
		try {
			List<DefenderClientAccessor> accessors = DefenderConnectorConfig.DEFENDER_CLIENT.getAll( context );
			List<DefenderMachine> list = new ArrayList<>();
			for ( DefenderClientAccessor accessor : accessors ) {
				String url = MACHINES_URL;
				while ( url != null ) {
					HttpRequest request = HttpRequest.newBuilder()
							.uri( URI.create( url ) )
							.header( "Authorization", "Bearer " + accessor.getBearerToken() )
							.header( "Accept", "application/json" )
							.GET()
							.build();
					HttpResponse<String> response = accessor.getHttpClient()
							.send( request, HttpResponse.BodyHandlers.ofString() );
					if ( response.statusCode() != 200 ) {
						throw new DataFetcherException(
								"Defender machines API returned HTTP " + response.statusCode() );
					}
					JsonNode root = OBJECT_MAPPER.readTree( response.body() );
					JsonNode values = root.get( "value" );
					if ( values != null && values.isArray() ) {
						for ( JsonNode node : values ) {
							list.add( DefenderMachine.fromJson( accessor.getTenantId(), node ) );
						}
					}
					JsonNode nextLink = root.get( "@odata.nextLink" );
					url = nextLink != null && !nextLink.isNull() ? nextLink.asText() : null;
				}
			}
			return list;
		}
		catch (DataFetcherException e) {
			throw e;
		}
		catch (Exception e) {
			throw new DataFetcherException( "Could not fetch Defender machines", e );
		}
	}
}
