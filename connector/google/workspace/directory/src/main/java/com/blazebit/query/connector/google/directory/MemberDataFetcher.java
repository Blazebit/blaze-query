/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.directory;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.services.directory.Directory;
import com.google.api.services.directory.model.Member;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class MemberDataFetcher implements DataFetcher<GoogleMember>, Serializable {

	public static final MemberDataFetcher INSTANCE = new MemberDataFetcher();

	private MemberDataFetcher() {
	}

	@Override
	public List<GoogleMember> fetch(DataFetchContext context) {
		try {
			List<Directory> directoryServices = GoogleDirectoryConnectorConfig.GOOGLE_DIRECTORY_SERVICE.getAll( context );
			List<GoogleMember> list = new ArrayList<>();
			List<? extends GoogleGroup> groups = context.getSession().getOrFetch( GoogleGroup.class );
			for ( Directory directory : directoryServices ) {
				for ( GoogleGroup group : groups ) {
					for ( Member member : directory.members().list( group.getPayload().getId() ).execute().getMembers() ) {
						list.add( new GoogleMember( member.getId(), member ) );
					}
				}
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch member list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GoogleMember.class, GoogleDirectoryConventionContext.INSTANCE );
	}
}
