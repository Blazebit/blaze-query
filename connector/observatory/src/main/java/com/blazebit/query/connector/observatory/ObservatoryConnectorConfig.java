/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.observatory;

import com.blazebit.query.spi.DataFetcherConfig;

/**
 * @author Martijn Sprengers
 * @since 1.0.25
 */
public final class ObservatoryConnectorConfig {

	/**
	 * Specifies the {@link ObservatoryClient} to use for querying data.
	 * You wire this via the blaze-query configuration (e.g. per integration).
	 */
	public static final DataFetcherConfig<ObservatoryClient> OBSERVATORY_CLIENT =
			DataFetcherConfig.forPropertyName("observatoryClient");

	private ObservatoryConnectorConfig() {
	}
}
