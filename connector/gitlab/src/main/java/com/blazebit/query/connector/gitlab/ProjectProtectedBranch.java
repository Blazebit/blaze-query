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

import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.ProtectedBranch;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ProjectProtectedBranch extends ProtectedBranch {

    private Project project;

    /**
     * Creates a new project protected branch.
     *
     * @param protectedBranch The base protected branch
     * @param project The project
     */
    public ProjectProtectedBranch(ProtectedBranch protectedBranch, Project project) {
        setId(protectedBranch.getId());
        setName(protectedBranch.getName());
        setPushAccessLevels(protectedBranch.getPushAccessLevels());
        setMergeAccessLevels(protectedBranch.getMergeAccessLevels());
        setUnprotectAccessLevels(protectedBranch.getUnprotectAccessLevels());
        setCodeOwnerApprovalRequired(protectedBranch.getCodeOwnerApprovalRequired());
        setAllowForcePush(protectedBranch.getAllowForcePush());
        setProject(project);
    }

    /**
     * Returns the project for this protected branch.
     *
     * @return the project fpr this protected branch
     */
    public Project getProject() {
        return project;
    }

    /**
     * Sets the project for this protected branch.
     *
     * @param project the project for this protected branch
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Sets the project for this protected branch.
     *
     * @param project the project for this protected branch
     * @return {@code this} for method chaining
     */
    public ProjectProtectedBranch withProject(Project project) {
        this.project = project;
        return this;
    }
}
