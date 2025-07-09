/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import com.blazebit.query.connector.jira.cloud.model.IssueBean;

import java.io.Serializable;
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
public class IssueBeanWrapper implements Serializable {
	private final IssueBean issueBean;
	private final String summary;
	private final Priority priority;
	private final String selfUri;
	private final IssueType issueType;
	private final String projectId;
	private final String projectSelfUri;
	private final StatusCategory statusCategory;
	private final Security security;
	private final OffsetDateTime created;
	private final OffsetDateTime dueDate;
	private final OffsetDateTime updated;
	private final OffsetDateTime resolutionDate;
	private final Status status;
	private final Assignee assignee;

	public IssueBeanWrapper(IssueBean issueBean) {
		this.issueBean = Objects.requireNonNull(issueBean, "issueBean cannot be null");
		Map<String, Object> fields = issueBean.getFields();
		this.selfUri = issueBean.getSelf() != null ? issueBean.getSelf().toString() : null;
		this.summary = extractStringField(fields, "summary");
		this.priority = extractPriority(fields);
		this.issueType = extractIssueType(fields);
		this.projectId = extractProjectId(fields);
		this.projectSelfUri = extractProjectSelfUri(fields);
		this.statusCategory = extractStatusCategory(fields);
		this.security = extractSecurity(fields);
		this.created = parseIsoOffsetDateTime(extractStringField(fields, "created") );
		this.dueDate = parseIsoOffsetDateTime(extractStringField(fields, "duedate") );
		this.updated = parseIsoOffsetDateTime(extractStringField(fields, "updated") );
		this.resolutionDate = parseIsoOffsetDateTime(extractStringField(fields, "resolutiondate") );
		this.status = extractStatus(fields);
		this.assignee = extractAssignee(fields);
	}

	/**
	 * Extract a string field from the fields map
	 * @param fields the fields map
	 * @param fieldName the field name to extract
	 * @return the string value or null if not present
	 */
	private String extractStringField(Map<String, Object> fields, String fieldName) {
		return fields != null ? (String) fields.get(fieldName) : null;
	}

	/**
	 * Extract a nested map from the fields map
	 * @param fields the fields map
	 * @param fieldName the field name to extract
	 * @return the map or empty map if not present
	 */
	private Map<String, Object> extractMapField(Map<String, Object> fields, String fieldName) {
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
	private Priority extractPriority(Map<String, Object> fields) {
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
	private IssueType extractIssueType(Map<String, Object> fields) {
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
	private String extractProjectId(Map<String, Object> fields) {
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
	private String extractProjectSelfUri(Map<String, Object> fields) {
		Map<String, Object> projectMap = extractMapField(fields, "project");
		if (!projectMap.isEmpty() && projectMap.get("self") != null) {
			return projectMap.get("self").toString();
		}
		return null;
	}

	/**
	 * Extract status category information from the fields map
	 * @param fields the fields map
	 * @return the StatusCategory object or null if not present
	 */
	private StatusCategory extractStatusCategory(Map<String, Object> fields) {
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
	private Security extractSecurity(Map<String, Object> fields) {
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
	private Status extractStatus(Map<String, Object> fields) {
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
	private Assignee extractAssignee(Map<String, Object> fields) {
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

	public IssueBean getIssueBean() {
		return issueBean;
	}

	public String getKey() {
		return issueBean.getKey();
	}

	public String getId() {
		return issueBean.getId();
	}

	public String getSelfUri() {
		return selfUri;
	}

	public String getSummary() {
		return summary;
	}

	public Priority getPriority() {
		return priority;
	}

	public IssueType getIssueType() {
		return issueType;
	}

	public String getProjectId() {
		return projectId;
	}

	public String getProjectSelfUri() {
		return projectSelfUri;
	}

	public StatusCategory getStatusCategory() {
		return statusCategory;
	}

	public Security getSecurity() {
		return security;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public OffsetDateTime getDueDate() {
		return dueDate;
	}

	public OffsetDateTime getUpdated() {
		return updated;
	}

	public Status getStatus() {
		return status;
	}

	public OffsetDateTime getResolutionDate() {
		return resolutionDate;
	}

	public Assignee getAssignee() {
		return assignee;
	}


	/**
	 * Priority information for an issue
	 */
	public static class Priority implements Serializable {
		private final String self;
		private final String iconUrl;
		private final String name;
		private final String id;

		public Priority(String self, String iconUrl, String name, String id) {
			this.self = self;
			this.iconUrl = iconUrl;
			this.name = name;
			this.id = id;
		}

		public String getSelf() {
			return self;
		}

		public String getIconUrl() {
			return iconUrl;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}
	}

	/**
	 * Issue type information
	 */
	public static class IssueType implements Serializable {
		private final String self;
		private final String id;
		private final String description;
		private final String iconUrl;
		private final String name;
		private final boolean subtask;
		private final String avatarId;
		private final String entityId;
		private final int hierarchyLevel;

		public IssueType(String self, String id, String description, String iconUrl, String name, boolean subtask,
						String avatarId, String entityId, int hierarchyLevel) {
			this.self = self;
			this.id = id;
			this.description = description;
			this.iconUrl = iconUrl;
			this.name = name;
			this.subtask = subtask;
			this.avatarId = avatarId;
			this.entityId = entityId;
			this.hierarchyLevel = hierarchyLevel;
		}

		public String getSelf() {
			return self;
		}

		public String getId() {
			return id;
		}

		public String getDescription() {
			return description;
		}

		public String getIconUrl() {
			return iconUrl;
		}

		public String getName() {
			return name;
		}

		public boolean isSubtask() {
			return subtask;
		}

		public String getAvatarId() {
			return avatarId;
		}

		public String getEntityId() {
			return entityId;
		}

		public int getHierarchyLevel() {
			return hierarchyLevel;
		}
	}

	/**
	 * Avatar URLs
	 */
	public static class AvatarUrls implements Serializable {
		private final String large;
		private final String small;
		private final String xsmall;
		private final String medium;

		public AvatarUrls(String large, String small, String xsmall, String medium) {
			this.large = large;
			this.small = small;
			this.xsmall = xsmall;
			this.medium = medium;
		}

		public String getLarge() {
			return large;
		}

		public String getSmall() {
			return small;
		}

		public String getXsmall() {
			return xsmall;
		}

		public String getMedium() {
			return medium;
		}
	}

	/**
	 * Status category information
	 */
	public static class StatusCategory implements Serializable {
		private final String self;
		private final String id;
		private final String key;
		private final String colorName;
		private final String name;

		public StatusCategory(String self, String id, String key, String colorName, String name) {
			this.self = self;
			this.id = id;
			this.key = key;
			this.colorName = colorName;
			this.name = name;
		}

		public String getSelf() {
			return self;
		}

		public String getId() {
			return id;
		}

		public String getKey() {
			return key;
		}

		public String getColorName() {
			return colorName;
		}

		public String getName() {
			return name;
		}
	}

	/**
	 * Security information
	 */
	public static class Security implements Serializable {
		private final String self;
		private final String id;
		private final String description;
		private final String name;

		public Security(String self, String id, String description, String name) {
			this.self = self;
			this.id = id;
			this.description = description;
			this.name = name;
		}

		public String getSelf() {
			return self;
		}

		public String getId() {
			return id;
		}

		public String getDescription() {
			return description;
		}

		public String getName() {
			return name;
		}
	}

	/**
	 * Status information
	 */
	public static class Status implements Serializable {
		private final String self;
		private final String description;
		private final String iconUrl;
		private final String name;
		private final String id;
		private final StatusCategory statusCategory;

		public Status(String self, String description, String iconUrl, String name, String id,
					StatusCategory statusCategory) {
			this.self = self;
			this.description = description;
			this.iconUrl = iconUrl;
			this.name = name;
			this.id = id;
			this.statusCategory = statusCategory;
		}

		public String getSelf() {
			return self;
		}

		public String getDescription() {
			return description;
		}

		public String getIconUrl() {
			return iconUrl;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}

		public StatusCategory getStatusCategory() {
			return statusCategory;
		}
	}

	/**
	 * Assignee information
	 */
	public static class Assignee implements Serializable {
		private final String self;
		private final String accountId;
		private final String emailAddress;
		private final AvatarUrls avatarUrls;
		private final String displayName;
		private final boolean active;
		private final String timeZone;
		private final String accountType;

		public Assignee(String self, String accountId, String emailAddress, AvatarUrls avatarUrls,
						String displayName, boolean active, String timeZone, String accountType) {
			this.self = self;
			this.accountId = accountId;
			this.emailAddress = emailAddress;
			this.avatarUrls = avatarUrls;
			this.displayName = displayName;
			this.active = active;
			this.timeZone = timeZone;
			this.accountType = accountType;
		}

		public String getSelf() {
			return self;
		}

		public String getAccountId() {
			return accountId;
		}

		public String getEmailAddress() {
			return emailAddress;
		}

		public AvatarUrls getAvatarUrls() {
			return avatarUrls;
		}

		public String getDisplayName() {
			return displayName;
		}

		public boolean isActive() {
			return active;
		}

		public String getTimeZone() {
			return timeZone;
		}

		public String getAccountType() {
			return accountType;
		}
	}
}
