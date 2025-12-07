/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.observatory;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link ObservatoryScan} rows using {@link ObservatoryClient}s from the context.
 *
 * @author Martijn Sprengers
 * @since 1.0.25
 */
public class ObservatoryScanDataFetcher implements DataFetcher<ObservatoryScan>, Serializable {

	public static final ObservatoryScanDataFetcher INSTANCE = new ObservatoryScanDataFetcher();

	private ObservatoryScanDataFetcher() {
	}

	@Override
	public List<ObservatoryScan> fetch(DataFetchContext context) {
		try {
			// Get all configured ObservatoryClient instances for this query context
			List<ObservatoryClient> clients = ObservatoryConnectorConfig.OBSERVATORY_CLIENT.getAll(context);
			List<ObservatoryScan> result = new ArrayList<>();

			for (ObservatoryClient client : clients) {
				result.addAll(client.fetchScans());
			}

			return result;
		} catch (RuntimeException e) {
			throw new DataFetcherException("Could not fetch Observatory scan data", e);
		}
	}

	@Override
	public DataFormat getDataFormat() {
		// Use the same pattern as GitHub: method-based component convention
		return DataFormats.componentMethodConvention(ObservatoryScan.class, ObservatoryConventionContext.INSTANCE);
	}
}
