/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud.admin;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.cloud.admin.api.OrgsApi;
import com.blazebit.query.connector.jira.cloud.admin.invoker.ApiClient;
import com.blazebit.query.connector.jira.cloud.admin.invoker.ApiException;
import com.blazebit.query.connector.jira.cloud.admin.model.OrgPage;
import com.blazebit.query.connector.jira.cloud.admin.model.OrgModel;
import com.blazebit.query.connector.jira.cloud.admin.model.LinkPageModel;
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
public class JiraCloudAdminOrgDataFetcher implements DataFetcher<OrgModel>, Serializable {

	public static final JiraCloudAdminOrgDataFetcher INSTANCE = new JiraCloudAdminOrgDataFetcher();

	private JiraCloudAdminOrgDataFetcher() {
	}

	@Override
	public List<OrgModel> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraCloudAdminConnectorConfig.API_CLIENT.getAll(context);
			List<OrgModel> orgList = new ArrayList<>();

			for (ApiClient apiClient : apiClients) {
				OrgsApi api = new OrgsApi(apiClient);

				String cursor = null;
				boolean hasMorePages = true;

				while (hasMorePages) {
					OrgPage orgPage = api.getOrgs(cursor);

					if (orgPage.getData() != null) {
						orgList.addAll(orgPage.getData());
					}

					LinkPageModel links = orgPage.getLinks();
					if (links != null && links.getNext() != null) {
						cursor = links.getNext();
					} else {
						hasMorePages = false;
					}
				}
			}

			return orgList;
		} catch (ApiException e) {
			throw new DataFetcherException("Could not fetch org list", e);
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention(OrgModel.class, JiraCloudAdminConventionContext.INSTANCE);
	}
}
