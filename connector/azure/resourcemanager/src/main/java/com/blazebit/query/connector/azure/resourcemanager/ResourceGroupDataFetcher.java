/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.query.connector.azure.resourcemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.resources.fluent.models.ResourceGroupInner;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ResourceGroupDataFetcher implements DataFetcher<ResourceGroupInner>, Serializable {

    public static final ResourceGroupDataFetcher INSTANCE = new ResourceGroupDataFetcher();

    private ResourceGroupDataFetcher() {
    }

    @Override
    public List<ResourceGroupInner> fetch(DataFetchContext context) {
        try {
            List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll( context);
            List<ResourceGroupInner> list = new ArrayList<>();
            for (AzureResourceManager resourceManager : resourceManagers) {
                for (ResourceGroup resourceGroup : resourceManager.resourceGroups().list()) {
                    list.add(resourceGroup.innerModel());
                }
            }
            return list;
        } catch (RuntimeException e) {
            throw new DataFetcherException("Could not fetch resource group list", e);
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.componentMethodConvention(ResourceGroupInner.class, AzureResourceManagerConventionContext.INSTANCE);
    }
}
