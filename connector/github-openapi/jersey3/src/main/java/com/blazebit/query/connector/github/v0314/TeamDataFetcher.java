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

package com.blazebit.query.connector.github.v0314;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.github.v0314.api.TeamsApi;
import com.blazebit.query.connector.github.v0314.invoker.ApiClient;
import com.blazebit.query.connector.github.v0314.invoker.ApiException;
import com.blazebit.query.connector.github.v0314.model.OrganizationSimple;
import com.blazebit.query.connector.github.v0314.model.Team;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class TeamDataFetcher implements DataFetcher<Team>, Serializable {

    public static final TeamDataFetcher INSTANCE = new TeamDataFetcher();

    private TeamDataFetcher() {
    }

    @Override
    public List<Team> fetch(DataFetchContext context) {
        try {
            List<ApiClient> apiClients = GithubConnectorConfig.API_CLIENT.getAll(context);
            List<? extends OrganizationSimple> organizations = context.getSession().getOrFetch(OrganizationSimple.class);
            List<Team> list = new ArrayList<>();
            for (ApiClient apiClient : apiClients) {
                TeamsApi reposApi = new TeamsApi(apiClient);
                for (OrganizationSimple organization : organizations) {
                    for (int page = 1; ; page++) {
                        List<Team> teams = reposApi.teamsList(
                                organization.getLogin(),
                                100,
                                page
                        );
                        list.addAll(teams);
                        if (teams.size() != 100) {
                            break;
                        }
                    }
                }
            }
            return list;
        } catch (ApiException e) {
            throw new DataFetcherException("Could not fetch team list", e);
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.beansConvention(Team.class, GithubConventionContext.INSTANCE);
    }
}
