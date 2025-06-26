/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud.admin;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.cloud.admin.api.DirectoryApi;
import com.blazebit.query.connector.jira.cloud.admin.api.UsersApi;
import com.blazebit.query.connector.jira.cloud.admin.invoker.ApiClient;
import com.blazebit.query.connector.jira.cloud.admin.invoker.ApiException;
import com.blazebit.query.connector.jira.cloud.admin.model.LinkPageCursor;
import com.blazebit.query.connector.jira.cloud.admin.model.MultiDirectoryUser;
import com.blazebit.query.connector.jira.cloud.admin.model.MultiDirectoryUserDirectory;
import com.blazebit.query.connector.jira.cloud.admin.model.MultiDirectoryUserDirectoryPage;
import com.blazebit.query.connector.jira.cloud.admin.model.MultiDirectoryUserPage;
import com.blazebit.query.connector.jira.cloud.admin.model.OrgModel;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.8
 */
public class JiraCloudAdminUserDataFetcher implements DataFetcher<MultiDirectoryUser>, Serializable {

	public static final JiraCloudAdminUserDataFetcher INSTANCE = new JiraCloudAdminUserDataFetcher();

	private JiraCloudAdminUserDataFetcher() {
	}

	@Override
	public List<MultiDirectoryUser> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraCloudAdminConnectorConfig.API_CLIENT.getAll(context);
			List<MultiDirectoryUser> userList = new ArrayList<>();

			for (ApiClient apiClient : apiClients) {
				DirectoryApi directoryApi = new DirectoryApi(apiClient);
				UsersApi usersApi = new UsersApi(apiClient);

				for ( OrgModel org : context.getSession()
						.getOrFetch( OrgModel.class ) ) {
					String directoryCursor = null;
					boolean directoryHasMorePages = true;

					while ( directoryHasMorePages ) {
						MultiDirectoryUserDirectoryPage directoryPage = directoryApi.getDirectoriesForOrg( org.getId(), null, null, directoryCursor, null );

						if ( directoryPage.getData() != null ) {
							for (MultiDirectoryUserDirectory directory : directoryPage.getData()) {
								String userCursor = null;
								boolean userHasMorePages = true;

								while ( userHasMorePages ) {
									MultiDirectoryUserPage userPage = usersApi.getDirectoryUsers(
											org.getId(),
											directory.getDirectoryId(),
											userCursor,
											null,
											null,
											null,
											null,
											null,
											null,
											null,
											null,
											null,
											null,
											null,
											null
									);

									if ( userPage.getData() != null ) {
										userList.addAll( userPage.getData() );
									}

									LinkPageCursor links = userPage.getLinks();
									if ( links != null && links.getNext() != null ) {
										userCursor = links.getNext();
									}
									else {
										userHasMorePages = false;
									}
								}
							}
						}

						LinkPageCursor links = directoryPage.getLinks();
						if (links != null && links.getNext() != null) {
							directoryCursor = links.getNext();
						} else {
							directoryHasMorePages = false;
						}
					}
				}
			}
			return userList;
		} catch (ApiException e) {
			throw new DataFetcherException("Could not fetch user list", e);
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention(MultiDirectoryUser.class, JiraCloudAdminConventionContext.INSTANCE);
	}
}
