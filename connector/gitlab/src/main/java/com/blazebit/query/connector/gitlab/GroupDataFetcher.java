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
import org.gitlab4j.api.models.Group;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class GroupDataFetcher implements DataFetcher<Group>, Serializable {

    public static final GroupDataFetcher INSTANCE = new GroupDataFetcher();

    private GroupDataFetcher() {
    }

    @Override
    public List<Group> fetch(DataFetchContext context) {
        try {
            List<GitLabApi> gitlabApis = GitlabConnectorConfig.GITLAB_API.getAll( context );
            List<Group> list = new ArrayList<>();
            for ( GitLabApi gitLabApi : gitlabApis) {
                list.addAll(gitLabApi.getGroupApi().getGroups());
            }
            return list;
        } catch (GitLabApiException | RuntimeException e) {
            throw new DataFetcherException("Could not fetch group list", e);
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.beansConvention(Group.class, GitlabConventionContext.INSTANCE);
    }
}
