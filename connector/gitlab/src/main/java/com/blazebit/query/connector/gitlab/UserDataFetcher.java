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
import org.gitlab4j.api.models.User;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class UserDataFetcher implements DataFetcher<User>, Serializable {

    public static final UserDataFetcher INSTANCE = new UserDataFetcher();

    private UserDataFetcher() {
    }

    @Override
    public List<User> fetch(DataFetchContext context) {
        try {
            Boolean fetchAll = GitlabConnectorConfig.FETCH_ALL_USERS.find(context);
            List<GitLabApi> gitlabApis = GitlabConnectorConfig.GITLAB_API.getAll(context);
            List<User> list = new ArrayList<>();
            for (GitLabApi gitLabApi : gitlabApis) {
                if (fetchAll == Boolean.TRUE) {
                    list.addAll(gitLabApi.getUserApi().getUsers());
                } else {
                    for (ProjectMember member : context.getSession().get(ProjectMember.class)) {
                        list.add(gitLabApi.getUserApi().getUser(member.getId()));
                    }
                }
            }
            return list;
        } catch (GitLabApiException | RuntimeException e) {
            throw new DataFetcherException("Could not fetch user list", e);
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.beansConvention(User.class, GitlabConventionContext.INSTANCE);
    }
}
