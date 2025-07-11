/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import com.blazebit.query.connector.jira.cloud.model.IssueBean;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static com.blazebit.query.connector.jira.cloud.DateUtils.parseIsoOffsetDateTime;

/**
 * A wrapper for IssueBean that provides structured access to fields
 * to avoid problems with Apache Calcite SQL when handling LinkedHashMap with mixed value types.
 *
 * @author Dimitar Prisadnikov
 * @since 1.0.9
 */
public record IssueBeanWrapper(
		String id,
		String summary,
		Priority priority,
		String selfUri,
		IssueType issueType,
		String projectId,
		String projectSelfUri,
		StatusCategory statusCategory,
		Security security,
		OffsetDateTime created,
		OffsetDateTime dueDate,
		OffsetDateTime updated,
		OffsetDateTime resolutionDate,
		Status status,
		Assignee assignee
)  {

	public IssueBeanWrapper(IssueBean issueBean) {
		this(
				issueBean.getId(),
				extractStringField(issueBean.getFields(), "summary"),
				extractPriority(issueBean.getFields()),
				Objects.requireNonNull( issueBean.getSelf() ).toString(),
				extractIssueType(issueBean.getFields()),
				extractProjectId(issueBean.getFields()),
				extractProjectSelfUri(issueBean.getFields()),
				extractStatusCategory(issueBean.getFields()),
				extractSecurity(issueBean.getFields()),
				parseIsoOffsetDateTime(extractStringField(issueBean.getFields(), "created")),
				parseIsoOffsetDateTime(extractStringField(issueBean.getFields(), "duedate")),
				parseIsoOffsetDateTime(extractStringField(issueBean.getFields(), "updated")),
				parseIsoOffsetDateTime(extractStringField(issueBean.getFields(), "resolutiondate")),
				extractStatus(issueBean.getFields()),
				extractAssignee(issueBean.getFields())
		);
	}

	/**
	 * Extract a string field from the fields map
	 * @param fields the fields map
	 * @param fieldName the field name to extract
	 * @return the string value or null if not present
	 */
	private static String extractStringField(Map<String, Object> fields, String fieldName) {
		return fields != null ? (String) fields.get(fieldName) : null;
	}

	/**
	 * Extract a nested map from the fields map
	 * @param fields the fields map
	 * @param fieldName the field name to extract
	 * @return the map or empty map if not present
	 */
	private static Map<String, Object> extractMapField(Map<String, Object> fields, String fieldName) {
		if (fields != null && fields.get(fieldName) instanceof Map) {
			return (Map<String, Object>) fields.get(fieldName);
		}
		return Collections.emptyMap();
	}

	/**
	 * Extract priority information from the fields map
	 * @param fields the fields map
	 * @return the Priority object or null if not present
	 */
	private static Priority extractPriority(Map<String, Object> fields) {
		Map<String, Object> priorityMap = extractMapField(fields, "priority");
		if (!priorityMap.isEmpty()) {
			return new Priority(
					(String) priorityMap.get("self"),
					(String) priorityMap.get("iconUrl"),
					(String) priorityMap.get("name"),
					(String) priorityMap.get("id")
			);
		}
		return null;
	}

	/**
	 * Extract issue type information from the fields map
	 * @param fields the fields map
	 * @return the IssueType object or null if not present
	 */
	private static IssueType extractIssueType(Map<String, Object> fields) {
		Map<String, Object> typeMap = extractMapField(fields, "issuetype");
		if (!typeMap.isEmpty()) {
			return new IssueType(
					(String) typeMap.get("self"),
					(String) typeMap.get("id"),
					(String) typeMap.get("description"),
					(String) typeMap.get("iconUrl"),
					(String) typeMap.get("name"),
					Boolean.TRUE.equals(typeMap.get("subtask")),
					typeMap.get("avatarId") != null ? typeMap.get("avatarId").toString() : null,
					(String) typeMap.get("entityId"),
					typeMap.get("hierarchyLevel") instanceof Number ?
							((Number) typeMap.get("hierarchyLevel")).intValue() : 0
			);
		}
		return null;
	}

	/**
	 * Extract project ID from the fields map.
	 * <p>
	 * Note that project IDs are unique among projects within the same Jira site (instance),
	 * but not globally across different instances.
	 * Also, Jira uses separate ID spaces for different entity types — meaning the same numeric ID
	 * may be used for a project, issue, or other entity type within the same site without conflict.
	 *
	 * @param fields the fields map
	 * @return the project ID or null if not present
	 */
	private static String extractProjectId(Map<String, Object> fields) {
		Map<String, Object> projectMap = extractMapField(fields, "project");
		if (!projectMap.isEmpty()) {
			return (String) projectMap.get("id");
		}
		return null;
	}

	/**
	 * Extract project self URI from the fields map.
	 * <p>
	 * The self URI provides a globally unique identifier for the project,
	 * as it contains both the Jira instance URL and the project's path.
	 * This is necessary because project IDs are only unique within a single
	 * Jira site (instance), not across different Jira installations.
	 *
	 * @param fields the fields map
	 * @return the project self URI as a string or null if not present
	 */
	private static String extractProjectSelfUri(Map<String, Object> fields) {
		Map<String, Object> projectMap = extractMapField(fields, "project");

		return Objects.requireNonNull(projectMap.get("self").toString());
	}

	/**
	 * Extract status category information from the fields map
	 * @param fields the fields map
	 * @return the StatusCategory object or null if not present
	 */
	private static StatusCategory extractStatusCategory(Map<String, Object> fields) {
		Map<String, Object> categoryMap = extractMapField(fields, "statusCategory");
		if (!categoryMap.isEmpty()) {
			return new StatusCategory(
					(String) categoryMap.get("self"),
					categoryMap.get("id") != null ? categoryMap.get("id").toString() : null,
					(String) categoryMap.get("key"),
					(String) categoryMap.get("colorName"),
					(String) categoryMap.get("name")
			);
		}
		return null;
	}

	/**
	 * Extract security information from the fields map
	 * @param fields the fields map
	 * @return the Security object or null if not present
	 */
	private static Security extractSecurity(Map<String, Object> fields) {
		Map<String, Object> securityMap = extractMapField(fields, "security");
		if (!securityMap.isEmpty()) {
			return new Security(
					(String) securityMap.get("self"),
					(String) securityMap.get("id"),
					(String) securityMap.get("description"),
					(String) securityMap.get("name")
			);
		}
		return null;
	}

	/**
	 * Extract status information from the fields map
	 * @param fields the fields map
	 * @return the Status object or null if not present
	 */
	private static Status extractStatus(Map<String, Object> fields) {
		Map<String, Object> statusMap = extractMapField(fields, "status");
		if (!statusMap.isEmpty()) {
			Map<String, Object> categoryMap = extractMapField(statusMap, "statusCategory");
			StatusCategory category = null;

			if (!categoryMap.isEmpty()) {
				category = new StatusCategory(
						(String) categoryMap.get("self"),
						categoryMap.get("id") != null ? categoryMap.get("id").toString() : null,
						(String) categoryMap.get("key"),
						(String) categoryMap.get("colorName"),
						(String) categoryMap.get("name")
				);
			}

			return new Status(
					(String) statusMap.get("self"),
					(String) statusMap.get("description"),
					(String) statusMap.get("iconUrl"),
					(String) statusMap.get("name"),
					(String) statusMap.get("id"),
					category
			);
		}
		return null;
	}

	/**
	 * Extract assignee information from the fields map
	 * @param fields the fields map
	 * @return the Assignee object or null if not present
	 */
	private static Assignee extractAssignee(Map<String, Object> fields) {
		Map<String, Object> assigneeMap = extractMapField(fields, "assignee");
		if (!assigneeMap.isEmpty()) {
			Map<String, Object> avatarUrls = extractMapField(assigneeMap, "avatarUrls");

			return new Assignee(
					(String) assigneeMap.get("self"),
					(String) assigneeMap.get("accountId"),
					(String) assigneeMap.get("emailAddress"),
					new AvatarUrls(
							(String) avatarUrls.get("48x48"),
							(String) avatarUrls.get("24x24"),
							(String) avatarUrls.get("16x16"),
							(String) avatarUrls.get("32x32")
					),
					(String) assigneeMap.get("displayName"),
					Boolean.TRUE.equals(assigneeMap.get("active")),
					(String) assigneeMap.get("timeZone"),
					(String) assigneeMap.get("accountType")
			);
		}
		return null;
	}

	public record Priority(
			String self,
			String iconUrl,
			String name,
			String id
	)  {}

	public record IssueType(
			String self,
			String id,
			String description,
			String iconUrl,
			String name,
			boolean subtask,
			String avatarId,
			String entityId,
			int hierarchyLevel
	)  {}

	public record AvatarUrls(
			String large,
			String small,
			String xsmall,
			String medium
	)  {}

	public record StatusCategory(
			String self,
			String id,
			String key,
			String colorName,
			String name
	)  {}

	public record Security(
			String self,
			String id,
			String description,
			String name
	)  {}

	public record Status(
			String self,
			String description,
			String iconUrl,
			String name,
			String id,
			StatusCategory statusCategory
	)  {}

	public record Assignee(
			String self,
			String accountId,
			String emailAddress,
			AvatarUrls avatarUrls,
			String displayName,
			boolean active,
			String timeZone,
			String accountType
	)  {}
}
