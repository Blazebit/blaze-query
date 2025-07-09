/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import com.blazebit.query.connector.jira.cloud.model.IssueBean;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
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
	private final URI self;
	private final IssueType issueType;
	private final String projectId;
	private final URI projectSelf;
	private final StatusCategory statusCategory;
	private final Security security;
	private final OffsetDateTime created;
	private final OffsetDateTime dueDate;
	private final OffsetDateTime updated;
	private final OffsetDateTime resolutionDate;
	private final Status status;
	private final Assignee assignee;

	public IssueBeanWrapper(IssueBean issueBean) throws URISyntaxException {
		this.issueBean = Objects.requireNonNull(issueBean, "issueBean cannot be null");
		Map<String, Object> fields = issueBean.getFields();
		this.self = new URI(issueBean.getSelf().toString());
		this.summary = extractStringField(fields, "summary");
		this.priority = extractPriority(fields);
		this.issueType = extractIssueType(fields);
		this.projectId = extractProjectId(fields);
		this.projectSelf = extractProjectSelfUri(fields);
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
	 * Note that Jira project IDs are not globally unique across different Jira instances,
	 * but they are guaranteed to be unique within a single Jira site (instance).
	 * For cross-instance identification, the project self URI should be used.
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
	 * @return the project self URI or null if not present
	 */
	private URI extractProjectSelfUri(Map<String, Object> fields) {
		Map<String, Object> projectMap = extractMapField(fields, "project");
		if (!projectMap.isEmpty() && projectMap.get("self") != null) {
			try {
				return new URI(projectMap.get("self").toString());
			} catch (URISyntaxException e) {
				return null;
			}
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

	/**
	 * Get the wrapped IssueBean
	 * @return the original IssueBean
	 */
	public IssueBean getIssueBean() {
		return issueBean;
	}

	/**
	 * Get the issue key
	 * @return the issue key
	 */
	public String getKey() {
		return issueBean.getKey();
	}

	/**
	 * Get the issue id
	 * @return the issue id
	 */
	public String getId() {
		return issueBean.getId();
	}

	/**
	 * Get the issue self URL
	 * @return the self URL
	 */
	public URI getSelf() {
		return self;
	}

	/**
	 * Get the issue summary
	 * @return the summary field value
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * Get the issue priority
	 * @return the priority information
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * Get the issue type
	 * @return the issue type information
	 */
	public IssueType getIssueType() {
		return issueType;
	}

	/**
	 * Get the project ID string
	 * @return the project ID
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * Get the project self URI
	 * @return the project URI
	 */
	public URI getProjectSelf() {
		return projectSelf;
	}

	/**
	 * Get the status category
	 * @return the status category information
	 */
	public StatusCategory getStatusCategory() {
		return statusCategory;
	}

	/**
	 * Get the security
	 * @return the security information
	 */
	public Security getSecurity() {
		return security;
	}

	/**
	 * Get the created date
	 * @return the created date as string
	 */
	public OffsetDateTime getCreated() {
		return created;
	}

	/**
	 * Get the due date
	 * @return the due date as string
	 */
	public OffsetDateTime getDueDate() {
		return dueDate;
	}

	/**
	 * Get the updated date
	 * @return the updated date as string
	 */
	public OffsetDateTime getUpdated() {
		return updated;
	}

	/**
	 * Get the status
	 * @return the status information
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Get the resolution date
	 * @return the resolution date as string
	 */
	public OffsetDateTime getResolutionDate() {
		return resolutionDate;
	}

	/**
	 * Get the assignee
	 * @return the assignee information or null if not assigned
	 */
	public Assignee getAssignee() {
		return assignee;
	}


	/**
	 * Priority information for an issue
	 */
		public record Priority(String self, String iconUrl, String name, String id) implements Serializable {


	}

	/**
	 * Issue type information
	 */
		public record IssueType(String self, String id, String description, String iconUrl, String name, boolean subtask,
								String avatarId, String entityId, int hierarchyLevel) implements Serializable {


	}

	/**
	 * Avatar URLs
	 */
		public record AvatarUrls(String large, String small, String xsmall, String medium) implements Serializable {


	}

	/**
	 * Status category information
	 */
		public record StatusCategory(String self, String id, String key, String colorName, String name)
			implements Serializable {


	}

	/**
	 * Security information
	 */
		public record Security(String self, String id, String description, String name) implements Serializable {


	}

	/**
	 * Status information
	 */
		public record Status(String self, String description, String iconUrl, String name, String id,
							StatusCategory statusCategory) implements Serializable {


	}

	/**
	 * Assignee information
	 */
		public record Assignee(String self, String accountId, String emailAddress, AvatarUrls avatarUrls,
							String displayName, boolean active, String timeZone, String accountType)
			implements Serializable {


	}
}
