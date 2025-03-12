/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github;

import org.kohsuke.github.GHRepository;

public class GHRepoWrapper {

	private final GHRepository repository;

	public GHRepoWrapper(GHRepository repository) {
		this.repository = repository;
	}

	public String getName() {
		return repository.getName();
	}

	public String getDescription() {
		return repository.getDescription();
	}
}
