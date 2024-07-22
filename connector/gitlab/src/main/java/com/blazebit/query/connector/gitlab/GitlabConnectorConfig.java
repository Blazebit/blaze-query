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

import com.blazebit.query.spi.DataFetcherConfig;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.UserApi;

/**
 * The configuration properties for the Gitlab connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GitlabConnectorConfig {

    /**
     * Specifies the {@link GitLabApi} to use for querying data.
     */
    public static final DataFetcherConfig<GitLabApi> GITLAB_API = DataFetcherConfig.forPropertyName( "gitlabApi" );
    /**
     * Specifies whether to query users all users of the server instance or only the users that are members of the visible projects.
     * @see UserApi#getUsers()
     * @see org.gitlab4j.api.ProjectApi#getAllMembers(Object)
     */
    public static final DataFetcherConfig<Boolean> FETCH_ALL_USERS = DataFetcherConfig.forPropertyName( "gitlabFetchAllUsers" );

    private GitlabConnectorConfig() {
    }
}
