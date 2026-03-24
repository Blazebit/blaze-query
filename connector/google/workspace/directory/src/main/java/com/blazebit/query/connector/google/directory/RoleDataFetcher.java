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
import com.google.api.services.directory.model.Role;
import com.google.api.services.directory.model.Roles;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class RoleDataFetcher implements DataFetcher<GoogleRole>, Serializable {

	public static final RoleDataFetcher INSTANCE = new RoleDataFetcher();

	private RoleDataFetcher() {
	}

	@Override
	public List<GoogleRole> fetch(DataFetchContext context) {
		try {
			List<Directory> directoryServices = GoogleDirectoryConnectorConfig.GOOGLE_DIRECTORY_SERVICE.getAll( context );
			List<GoogleRole> list = new ArrayList<>();
			for ( Directory directory : directoryServices ) {
				String pageToken = null;
				do {
					Roles response = directory.roles().list( "my_customer" )
							.setPageToken( pageToken )
							.execute();
					List<Role> roles = response.getItems();
					if ( roles != null ) {
						for ( Role role : roles ) {
							list.add( new GoogleRole( String.valueOf( role.getRoleId() ), role ) );
						}
					}
					pageToken = response.getNextPageToken();
				}
				while ( pageToken != null );
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch role list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GoogleRole.class, GoogleDirectoryConventionContext.INSTANCE );
	}
}
