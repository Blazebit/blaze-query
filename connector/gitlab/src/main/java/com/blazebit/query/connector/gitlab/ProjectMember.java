/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ProjectMember extends Member {

	private Project project;

	/**
	 * Creates a new project member.
	 *
	 * @param member The base member
	 * @param project The project
	 */
	public ProjectMember(Member member, Project project) {
		setAvatarUrl( member.getAvatarUrl() );
		setCreatedAt( member.getCreatedAt() );
		setEmail( member.getEmail() );
		setId( member.getId() );
		setName( member.getName() );
		setState( member.getState() );
		setUsername( member.getUsername() );
		setWebUrl( member.getWebUrl() );
		setAccessLevel( member.getAccessLevel() );
		setExpiresAt( member.getExpiresAt() );
		setGroupSamlIdentity( member.getGroupSamlIdentity() );
		setProject( project );
	}

	/**
	 * Returns the project this user is a member of.
	 *
	 * @return the project this user is a member of
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Sets the project this user is a member of.
	 *
	 * @param project the project this user is a member of
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * Sets the project this user is a member of.
	 *
	 * @param project the project this user is a member of
	 * @return {@code this} for method chaining
	 */
	public ProjectMember withProject(Project project) {
		this.project = project;
		return this;
	}
}
