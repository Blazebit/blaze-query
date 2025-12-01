/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Donghwi Kim
 * @since 1.0.0
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
