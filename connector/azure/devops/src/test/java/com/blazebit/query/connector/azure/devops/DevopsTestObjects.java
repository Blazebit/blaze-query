/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.devops;

import com.blazebit.query.connector.devops.invoker.JSON;
import com.blazebit.query.connector.devops.model.GitRepository;
import com.blazebit.query.connector.devops.model.PolicyConfiguration;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public final class DevopsTestObjects {

	private static final JSON JSON_PARSER = new JSON();

	private DevopsTestObjects() {
	}

	public static GitRepository mainServiceRepository() {
		return fromJson( GitRepository.class,
				"src/test/resources/resource-definitions/azure-devops/repository/repo-main-service.json" );
	}

	public static PolicyConfiguration minReviewersPolicy() {
		return fromJson( PolicyConfiguration.class,
				"src/test/resources/resource-definitions/azure-devops/policy-configuration/policy-min-reviewers.json" );
	}

	private static <T> T fromJson(Class<T> type, String path) {
		try {
			String json = new String( Files.readAllBytes( Paths.get( path ) ) );
			return JSON_PARSER.getMapper().readValue( json, type );
		}
		catch (IOException e) {
			throw new UncheckedIOException( "Could not read test fixture: " + path, e );
		}
	}
}
