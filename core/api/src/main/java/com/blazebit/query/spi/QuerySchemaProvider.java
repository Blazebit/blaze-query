/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.spi;

import java.util.Map;

/**
 * A {@link java.util.ServiceLoader} SPI to provide schema objects.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface QuerySchemaProvider {

	/**
	 * Resolves the schema objects based on a configuration provider.
	 *
	 * @param configurationProvider A configuration provider
	 * @return The schema objects to register
	 */
	Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider);

}
