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
import com.blazebit.query.connector.github.v0314.api.ReposApi;
import com.blazebit.query.connector.github.v0314.invoker.ApiClient;
import com.blazebit.query.connector.github.v0314.invoker.ApiException;
import com.blazebit.query.connector.github.v0314.model.Repository;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class RepositoryDataFetcher implements DataFetcher<Repository>, Serializable {

    public static final RepositoryDataFetcher INSTANCE = new RepositoryDataFetcher();

    private RepositoryDataFetcher() {
    }

    @Override
    public List<Repository> fetch(DataFetchContext context) {
        try {
            List<ApiClient> apiClients = GithubConnectorConfig.API_CLIENT.getAll(context);
            List<Repository> list = new ArrayList<>();
            for (ApiClient apiClient : apiClients) {
                ReposApi reposApi = new ReposApi(apiClient);
                for (int page = 1; ; page++) {
                    List<Repository> repositories = reposApi.reposListForAuthenticatedUser(
                            null,
                            null,
                            null,
                            null,
                            null,
                            100,
                            page,
                            null,
                            null
                    );
                    list.addAll(repositories);
                    if (repositories.size() != 100) {
                        break;
                    }
                }
            }
            return list;
        } catch (ApiException e) {
            throw new DataFetcherException("Could not fetch repository list", e);
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.beansConvention(Repository.class, GithubConventionContext.INSTANCE);
    }
}
