/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitlabGroup {
	@JsonProperty("id")
	private String id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("path")
	private String path;

	@JsonProperty("requireTwoFactorAuthentication")
	private Boolean requireTwoFactorAuthentication;

	@JsonProperty("twoFactorGracePeriod")
	private Integer twoFactorGracePeriod;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public Boolean getRequireTwoFactorAuthentication() {
		return requireTwoFactorAuthentication;
	}

	public Integer getTwoFactorGracePeriod() {
		return twoFactorGracePeriod;
	}
}
