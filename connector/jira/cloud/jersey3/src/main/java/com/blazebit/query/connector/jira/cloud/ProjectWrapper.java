/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import com.blazebit.query.connector.jira.cloud.model.Project;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A record that contains fields from the Project class.
 * Note: Not all fields from the original Project class are included in this wrapper.
 *
 * @author Dimitar Prisadnikov
 * @since 1.0.10
 */
public record ProjectWrapper(
		Boolean archived,
		User archivedBy,
		OffsetDateTime archivedDate,
		String assigneeType,
		Boolean deleted,
		User deletedBy,
		OffsetDateTime deletedDate,
		String description,
		String email,
		Boolean favourite,
		String id,
		Boolean isPrivate,
		String key,
		User lead,
		String name,
		ProjectCategory projectCategory,
		String projectTypeKey,
		Map<String, Object> properties,
		OffsetDateTime retentionTillDate,
		Map<String, String> roles,
		String self,
		Boolean simplified,
		String style,
		String url,
		UUID uuid
) {
	public ProjectWrapper(Project project) {
		this(
				project.getArchived(),
				project.getArchivedBy() != null ? new User(project.getArchivedBy()) : null,
				project.getArchivedDate(),
				project.getAssigneeType() != null ? project.getAssigneeType().getValue() : null,
				project.getDeleted(),
				project.getDeletedBy() != null ? new User(project.getDeletedBy()) : null,
				project.getDeletedDate(),
				project.getDescription(),
				project.getEmail(),
				project.getFavourite(),
				project.getId(),
				project.getIsPrivate(),
				project.getKey(),
				project.getLead() != null ? new User(project.getLead()) : null,
				project.getName(),
				project.getProjectCategory() != null ? new ProjectCategory(project.getProjectCategory()) : null,
				project.getProjectTypeKey() != null ? project.getProjectTypeKey().getValue() : null,
				project.getProperties(),
				project.getRetentionTillDate(),
				project.getRoles() != null ?
						project.getRoles().entrySet().stream()
								.collect(Collectors.toMap(
										Map.Entry::getKey,
										e -> e.getValue() != null ? e.getValue().toString() : null
								)) :
						new HashMap<>(),
				Objects.requireNonNull( project.getSelf() ).toString(),
				project.getSimplified(),
				project.getStyle() != null ? project.getStyle().getValue() : null,
				project.getUrl(),
				project.getUuid()
		);
	}

	public record User(
			String accountId,
			String accountType,
			Boolean active,
			String displayName,
			String emailAddress,
			String self,
			String timeZone
	) {
		public User(com.blazebit.query.connector.jira.cloud.model.User user) {
			this(
					user.getAccountId(),
					user.getAccountType() != null ? user.getAccountType().getValue() : null,
					user.getActive(),
					user.getDisplayName(),
					user.getEmailAddress(),
					Objects.requireNonNull( user.getSelf() ).toString(),
					user.getTimeZone()
			);
		}
	}

	public record ProjectCategory(
			String description,
			String id,
			String name,
			String self
	) {
		public ProjectCategory(com.blazebit.query.connector.jira.cloud.model.ProjectCategory category) {
			this(
					category.getDescription(),
					category.getId(),
					category.getName(),
					category.getSelf() != null ? category.getSelf().toString() : null
			);
		}
	}
}
