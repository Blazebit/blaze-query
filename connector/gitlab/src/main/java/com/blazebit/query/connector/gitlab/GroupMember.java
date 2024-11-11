/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Member;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class GroupMember extends Member {

	private Group group;

	/**
	 * Creates a new group member.
	 *
	 * @param member The base member
	 * @param group The group
	 */
	public GroupMember(Member member, Group group) {
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
		setGroup( group );
	}

	/**
	 * Returns the group this user is a member of.
	 *
	 * @return the group this user is a member of
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Sets the group this user is a member of.
	 *
	 * @param group the group this user is a member of
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	/**
	 * Sets the group this user is a member of.
	 *
	 * @param group the group this user is a member of
	 * @return {@code this} for method chaining
	 */
	public GroupMember withGroup(Group group) {
		this.group = group;
		return this;
	}
}
