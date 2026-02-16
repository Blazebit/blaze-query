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
import com.google.api.services.directory.model.Group;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class GroupDataFetcher implements DataFetcher<GoogleGroup>, Serializable {

	public static final GroupDataFetcher INSTANCE = new GroupDataFetcher();

	private GroupDataFetcher() {
	}

	@Override
	public List<GoogleGroup> fetch(DataFetchContext context) {
		try {
			List<Directory> directoryServices = GoogleDirectoryConnectorConfig.GOOGLE_DIRECTORY_SERVICE.getAll( context );
			List<GoogleGroup> list = new ArrayList<>();
			for ( Directory directory : directoryServices ) {
				for ( Group group : directory.groups().list().setCustomer( "my_customer" ).execute().getGroups() ) {
					list.add( new GoogleGroup( group.getId(), group ) );
				}
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch group list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GoogleGroup.class, GoogleDirectoryConventionContext.INSTANCE );
	}
}
