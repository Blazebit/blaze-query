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
 * A data fetcher for fetching Microsoft Defender for Endpoint alerts.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class DefenderAlertDataFetcher implements DataFetcher<DefenderAlert>, Serializable {

	private static final String ALERTS_URL = "https://api.securitycenter.microsoft.com/api/alerts";
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static final DefenderAlertDataFetcher INSTANCE = new DefenderAlertDataFetcher();

	private DefenderAlertDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DefenderAlert.class, DefenderConventionContext.INSTANCE );
	}

	@Override
	public List<DefenderAlert> fetch(DataFetchContext context) {
		try {
			List<DefenderClientAccessor> accessors = DefenderConnectorConfig.DEFENDER_CLIENT.getAll( context );
			List<DefenderAlert> list = new ArrayList<>();
			for ( DefenderClientAccessor accessor : accessors ) {
				String url = ALERTS_URL;
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
								"Defender alerts API returned HTTP " + response.statusCode() );
					}
					JsonNode root = OBJECT_MAPPER.readTree( response.body() );
					JsonNode values = root.get( "value" );
					if ( values != null && values.isArray() ) {
						for ( JsonNode node : values ) {
							list.add( DefenderAlert.fromJson( accessor.getTenantId(), node ) );
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
			throw new DataFetcherException( "Could not fetch Defender alerts", e );
		}
	}
}
