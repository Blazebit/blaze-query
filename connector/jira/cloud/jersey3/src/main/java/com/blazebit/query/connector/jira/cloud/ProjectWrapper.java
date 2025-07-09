/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import com.blazebit.query.connector.jira.cloud.model.Project;

import java.io.Serializable;

/**
 * A wrapper for Project that provides string representation of the URI fields
 * to avoid problems with Apache Calcite SQL when handling java.net.URI objects.
 *
 * @author Dimitar Prisadnikov
 * @since 1.0.10
 */

public class ProjectWrapper implements Serializable {
	private final Project project;
	private final String selfUri;

	public ProjectWrapper(Project project) {
		this.project = project;
		this.selfUri = project.getSelf() != null ? project.getSelf().toString() : null;
	}

	public Project getProject() {
		return project;
	}

	public String getSelfUri() {
		return selfUri;
	}
}
