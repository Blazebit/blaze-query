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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ProjectDataFetcher implements DataFetcher<Project>, Serializable {

    public static final ProjectDataFetcher INSTANCE = new ProjectDataFetcher();

    private ProjectDataFetcher() {
    }

    @Override
    public List<Project> fetch(DataFetchContext context) {
        try {
            List<GitLabApi> gitlabApis = GitlabConnectorConfig.GITLAB_API.getAll( context );
            List<Project> list = new ArrayList<>();
            for (GitLabApi gitLabApi : gitlabApis) {
                if (gitLabApi.getGitLabServerUrl().startsWith(GitlabConventionContext.GITLAB_HOST)) {
                    list.addAll(gitLabApi.getProjectApi().getMemberProjects());
                } else {
                    list.addAll(gitLabApi.getProjectApi().getProjects());
                }
            }
            return list;
        } catch (GitLabApiException | RuntimeException e) {
            throw new DataFetcherException("Could not fetch project list", e);
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.beansConvention(Project.class, GitlabConventionContext.INSTANCE);
    }
}
