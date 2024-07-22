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

import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ProjectMember extends Member {

    private Project project;

    /**
     * Creates a new project member.
     *
     * @param member The base member
     * @param project The project
     */
    public ProjectMember(Member member, Project project) {
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
        setProject(project);
    }

    /**
     * Returns the project this user is a member of.
     *
     * @return the project this user is a member of
     */
    public Project getProject() {
        return project;
    }

    /**
     * Sets the project this user is a member of.
     *
     * @param project the project this user is a member of
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Sets the project this user is a member of.
     *
     * @param project the project this user is a member of
     * @return {@code this} for method chaining
     */
    public ProjectMember withProject(Project project) {
        this.project = project;
        return this;
    }
}
