/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.devops;

import com.blazebit.query.connector.devops.invoker.JSON;
import com.blazebit.query.connector.devops.model.GitRepository;
import com.blazebit.query.connector.devops.model.PolicyConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

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
				"resource-definitions/azure-devops/repository/repo-main-service.json" );
	}

	public static PolicyConfiguration minReviewersPolicy() {
		return fromJson( PolicyConfiguration.class,
				"resource-definitions/azure-devops/policy-configuration/policy-min-reviewers.json" );
	}

	public static WorkItem loginBugWorkItem() {
		com.blazebit.query.connector.devops.model.WorkItem raw = fromJson(
				com.blazebit.query.connector.devops.model.WorkItem.class,
				"resource-definitions/azure-devops/work-item/work-item-login-bug.json" );
		return new WorkItem( raw );
	}

	private static <T> T fromJson(Class<T> type, String resource) {
		try (InputStream is = DevopsTestObjects.class.getClassLoader().getResourceAsStream( resource )) {
			if ( is == null ) {
				throw new IOException( "Resource not found: " + resource );
			}
			String json = new String( is.readAllBytes(), StandardCharsets.UTF_8 );
			return JSON_PARSER.getMapper().readValue( json, type );
		}
		catch (IOException e) {
			throw new UncheckedIOException( "Could not read test fixture: " + resource, e );
		}
	}
}
