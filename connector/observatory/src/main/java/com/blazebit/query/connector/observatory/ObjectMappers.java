/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.observatory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Martijn Sprengers
 * @since 1.0.25
 */
public final class ObjectMappers {

	public static ObjectMapper instance;

	private ObjectMappers() {
	}

	public static ObjectMapper getInstance() {
		if ( instance == null ) {
			instance = new ObjectMapper();
			instance.findAndRegisterModules();
		}

		return instance;
	}
}
