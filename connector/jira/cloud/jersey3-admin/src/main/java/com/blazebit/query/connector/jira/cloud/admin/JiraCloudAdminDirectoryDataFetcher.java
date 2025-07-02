/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud.admin;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.cloud.admin.api.DirectoryApi;
import com.blazebit.query.connector.jira.cloud.admin.invoker.ApiClient;
import com.blazebit.query.connector.jira.cloud.admin.invoker.ApiException;
import com.blazebit.query.connector.jira.cloud.admin.model.LinkPageCursor;
import com.blazebit.query.connector.jira.cloud.admin.model.MultiDirectoryUserDirectory;
import com.blazebit.query.connector.jira.cloud.admin.model.MultiDirectoryUserDirectoryPage;
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
 * @since 1.0.9
 */
public class JiraCloudAdminDirectoryDataFetcher implements DataFetcher<MultiDirectoryUserDirectory>, Serializable {

	public static final JiraCloudAdminDirectoryDataFetcher INSTANCE = new JiraCloudAdminDirectoryDataFetcher();

	private JiraCloudAdminDirectoryDataFetcher() {
	}

	@Override
	public List<MultiDirectoryUserDirectory> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraCloudAdminConnectorConfig.API_CLIENT.getAll(context);
			List<MultiDirectoryUserDirectory> directoryList = new ArrayList<>();

			for (ApiClient apiClient : apiClients) {
				DirectoryApi api = new DirectoryApi(apiClient);

				for ( OrgModel org : context.getSession()
						.getOrFetch( OrgModel.class ) ) {
					String cursor = null;
					boolean hasMorePages = true;

					while ( hasMorePages ) {
						MultiDirectoryUserDirectoryPage directoryPage = api.getDirectoriesForOrg( org.getId(), null, null, cursor, null );

						if ( directoryPage.getData() != null ) {
							directoryList.addAll( directoryPage.getData() );
						}

						LinkPageCursor links = directoryPage.getLinks();
						if ( links != null && links.getNext() != null ) {
							cursor = links.getNext();
						}
						else {
							hasMorePages = false;
						}
					}
				}
			}

			return directoryList;
		} catch (ApiException e) {
			throw new DataFetcherException("Could not fetch directory list", e);
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention(MultiDirectoryUserDirectory.class, JiraCloudAdminConventionContext.INSTANCE);
	}
}
