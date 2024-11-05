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
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class BranchDataFetcher implements DataFetcher<GHBranch>, Serializable {

	public static final BranchDataFetcher INSTANCE = new BranchDataFetcher();

	private BranchDataFetcher() {
	}

	@Override
	public List<GHBranch> fetch(DataFetchContext context) {
		try {
			List<GHBranch> list = new ArrayList<>();
			for ( GHRepository repository : context.getSession().getOrFetch( GHRepository.class ) ) {
				list.addAll( repository.getBranches().values() );
			}
			return list;
		}
		catch (IOException | RuntimeException e) {
			throw new DataFetcherException( "Could not fetch branch list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GHBranch.class, GithubConventionContext.INSTANCE );
	}
}
