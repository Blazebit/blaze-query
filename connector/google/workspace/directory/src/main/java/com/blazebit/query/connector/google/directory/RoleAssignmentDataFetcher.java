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
import com.google.api.services.directory.model.RoleAssignment;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class RoleAssignmentDataFetcher implements DataFetcher<GoogleRoleAssignment>, Serializable {

	public static final RoleAssignmentDataFetcher INSTANCE = new RoleAssignmentDataFetcher();

	private RoleAssignmentDataFetcher() {
	}

	@Override
	public List<GoogleRoleAssignment> fetch(DataFetchContext context) {
		try {
			List<Directory> directoryServices = GoogleDirectoryConnectorConfig.GOOGLE_DIRECTORY_SERVICE.getAll( context );
			List<GoogleRoleAssignment> list = new ArrayList<>();
			for ( Directory directory : directoryServices ) {
				for ( RoleAssignment roleAssignment : directory.roleAssignments().list("my_customer" ).execute().getItems() ) {
					list.add( new GoogleRoleAssignment( String.valueOf( roleAssignment.getRoleAssignmentId() ), roleAssignment ) );
				}
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch role assignments list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GoogleRoleAssignment.class, GoogleDirectoryConventionContext.INSTANCE );
	}
}
