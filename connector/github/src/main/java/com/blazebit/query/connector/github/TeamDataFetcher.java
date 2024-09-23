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

package com.blazebit.query.connector.github;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHTeam;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class TeamDataFetcher implements DataFetcher<GHTeam>, Serializable {

    public static final TeamDataFetcher INSTANCE = new TeamDataFetcher();

    private TeamDataFetcher() {
    }

    @Override
    public List<GHTeam> fetch(DataFetchContext context) {
        try {
            List<GHTeam> list = new ArrayList<>();
            for (GHOrganization organization : context.getSession().getOrFetch(GHOrganization.class)) {
                list.addAll(organization.listTeams().toList());
            }
            return list;
        } catch (IOException | RuntimeException e) {
            throw new DataFetcherException("Could not fetch team list", e);
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.beansConvention(GHTeam.class, GithubConventionContext.INSTANCE);
    }
}
