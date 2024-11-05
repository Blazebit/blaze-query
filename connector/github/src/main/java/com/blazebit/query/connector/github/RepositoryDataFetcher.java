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
import org.kohsuke.github.GHRepository;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class RepositoryDataFetcher implements DataFetcher<GHRepository>, Serializable {

	public static final RepositoryDataFetcher INSTANCE = new RepositoryDataFetcher();

	private RepositoryDataFetcher() {
	}

	@Override
	public List<GHRepository> fetch(DataFetchContext context) {
		try {
			List<GHRepository> list = new ArrayList<>();
			for ( GHOrganization organization : context.getSession().getOrFetch( GHOrganization.class ) ) {
				list.addAll( organization.listRepositories().toList() );
			}
			return list;
		}
		catch (IOException | RuntimeException e) {
			throw new DataFetcherException( "Could not fetch repository list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GHRepository.class, GithubConventionContext.INSTANCE );
	}
}
