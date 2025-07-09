/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud.admin;

import com.blazebit.query.connector.jira.cloud.admin.model.MultiDirectoryUserDirectory;

/**
 * A wrapper record that encapsulates a Jira Cloud user directory along with their organization ID.
 *
 * @param userDirectory The Jira Cloud directory data
 * @param organizationId The ID of the organization the directory belongs to
 *
 * @author Dimitar Prisadnikov
 * @since 1.0.9
 */
public record JiraCloudAdminDirectoryWrapper(
		MultiDirectoryUserDirectory userDirectory,
		String organizationId
) {
}
