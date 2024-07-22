/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        setAvatarUrl(member.getAvatarUrl());
        setCreatedAt(member.getCreatedAt());
        setEmail(member.getEmail());
        setId(member.getId());
        setName(member.getName());
        setState(member.getState());
        setUsername(member.getUsername());
        setWebUrl(member.getWebUrl());
        setAccessLevel(member.getAccessLevel());
        setExpiresAt(member.getExpiresAt());
        setGroupSamlIdentity(member.getGroupSamlIdentity());
        setGroup(group);
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
