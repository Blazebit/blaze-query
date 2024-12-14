/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.datacenter;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class GroupMember {

	private final String groupName;
	private final String userName;

	public GroupMember(String groupName, String userName) {
		this.groupName = groupName;
		this.userName = userName;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getUserName() {
		return userName;
	}
}
