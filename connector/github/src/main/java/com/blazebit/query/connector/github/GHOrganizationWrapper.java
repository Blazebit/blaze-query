/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github;

import org.kohsuke.github.GHOrganization;

public class GHOrganizationWrapper {
	private final String login;
	private final Long id;
	private final String url;

	public GHOrganizationWrapper(GHOrganization organization) {
		this.login = organization.getLogin();
		this.id = organization.getId();
		this.url = organization.getUrl().toString();  // Convert URL to String
	}

	public String getLogin() {
		return login;
	}

	public Long getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}
}
