/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitlabUser {

	@JsonProperty("id")
	private String id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("username")
	private String username;

	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("lastActivityOn")
	private Date lastActivityOn;

	public String getId() { return id; }
	public String getName() { return name; }
	public String getUsername() { return username; }
	public Boolean getActive() { return active; }
	public Date getLastActivityOn() { return lastActivityOn; }
}
