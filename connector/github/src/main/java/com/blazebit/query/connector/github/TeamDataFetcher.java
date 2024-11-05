/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
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
			for ( GHOrganization organization : context.getSession().getOrFetch( GHOrganization.class ) ) {
				list.addAll( organization.listTeams().toList() );
			}
			return list;
		}
		catch (IOException | RuntimeException e) {
			throw new DataFetcherException( "Could not fetch team list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GHTeam.class, GithubConventionContext.INSTANCE );
	}
}
