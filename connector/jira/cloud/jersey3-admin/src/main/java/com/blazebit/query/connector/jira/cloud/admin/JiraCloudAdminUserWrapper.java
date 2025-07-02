/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud.admin;

import com.blazebit.query.connector.jira.cloud.admin.model.MultiDirectoryUser;

/**
 * A wrapper record that encapsulates a Jira Cloud user along with their organization and directory details.
 * This includes the MultiDirectoryUser object, organization ID and name, as well as directory ID and name.
 *
 * @param user The Jira Cloud user data
 * @param organizationId The ID of the organization the user belongs to
 * @param organizationName The name of the organization the user belongs to
 * @param directoryId The ID of the directory the user belongs to
 * @param directoryName The name of the directory the user belongs to
 *
 * @author Dimitar Prisadnikov
 * @since 1.0.9
 */
public record JiraCloudAdminUserWrapper(
		MultiDirectoryUser user,
		String organizationId,
		String organizationName,
		String directoryId,
		String directoryName
) {
}
