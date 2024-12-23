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
import com.google.api.services.directory.model.User;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
			List<Directory> directoryServices = GoogleDirectoryConnectorConfig.GOOGLE_DIRECTORY_SERVICE.getAll( context );
			List<User> list = new ArrayList<>();
			for ( Directory directory : directoryServices ) {
				list.addAll( directory.users().list().setCustomer( "my_customer" ).execute().getUsers() );
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch user list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( User.class, GoogleDirectoryConventionContext.INSTANCE );
	}
}
