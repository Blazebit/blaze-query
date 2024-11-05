/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.ProtectedBranch;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ProjectProtectedBranch extends ProtectedBranch {

	private Project project;

	/**
	 * Creates a new project protected branch.
	 *
	 * @param protectedBranch The base protected branch
	 * @param project The project
	 */
	public ProjectProtectedBranch(ProtectedBranch protectedBranch, Project project) {
		setId( protectedBranch.getId() );
		setName( protectedBranch.getName() );
		setPushAccessLevels( protectedBranch.getPushAccessLevels() );
		setMergeAccessLevels( protectedBranch.getMergeAccessLevels() );
		setUnprotectAccessLevels( protectedBranch.getUnprotectAccessLevels() );
		setCodeOwnerApprovalRequired( protectedBranch.getCodeOwnerApprovalRequired() );
		setAllowForcePush( protectedBranch.getAllowForcePush() );
		setProject( project );
	}

	/**
	 * Returns the project for this protected branch.
	 *
	 * @return the project fpr this protected branch
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Sets the project for this protected branch.
	 *
	 * @param project the project for this protected branch
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * Sets the project for this protected branch.
	 *
	 * @param project the project for this protected branch
	 * @return {@code this} for method chaining
	 */
	public ProjectProtectedBranch withProject(Project project) {
		this.project = project;
		return this;
	}
}
