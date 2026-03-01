/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.observatory;

import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * @author Martijn Sprengers
 * @since 1.0.25
 */
public final class ObjectMappers {

	private static JsonMapper instance;

	private ObjectMappers() {
	}

	public static JsonMapper getInstance() {
		if ( instance == null ) {
			instance = JsonMapper.builder().findAndAddModules().build();
		}

		return instance;
	}
}
