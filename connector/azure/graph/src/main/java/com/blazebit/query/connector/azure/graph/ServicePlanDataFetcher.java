/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A data fetcher that returns all service plans in Azure with their parent.
 * <p>
 * This class is responsible for reading and parsing service plan data
 * from a CSV file and converting it into a list of ServicePlan objects.
 *
 * @author Max Hovens
 * @since 1.0.0
 */
public class ServicePlanDataFetcher implements DataFetcher<ServicePlan>, Serializable {

	public static final ServicePlanDataFetcher INSTANCE = new ServicePlanDataFetcher();
	private static final String SERVICE_PLAN_CSV = "/service-plans.csv";
	private static final String SPLIT_REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

	private ServicePlanDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( ServicePlan.class, AzureGraphConventionContext.INSTANCE );
	}

	@Override
	public List<ServicePlan> fetch(DataFetchContext context) {
		try {
			return read();
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch service plan list", e );
		}
	}

	private List<ServicePlan> read() {
		try (var reader = Files.newBufferedReader(
				Path.of( getClass().getResource( SERVICE_PLAN_CSV ).toURI() ) )) {
			return reader.lines()
					.skip( 1 ) // Skip header row
					.map( line -> line
							.replace( "\u00A0", "" ) // Some lines contain NBSP
							.split( SPLIT_REGEX ) )
					.map( line -> new ServicePlan( UUID.fromString( line[4] ), line[3], line[5],
							UUID.fromString( line[2] ), line[1], line[0] ) )
					.collect( Collectors.toList() );
		}
		catch (IOException | URISyntaxException e) {
			throw new RuntimeException( e );
		}
	}
}
