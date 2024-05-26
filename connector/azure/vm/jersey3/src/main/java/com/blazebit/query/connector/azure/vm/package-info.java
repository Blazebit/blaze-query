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

/**
 * The Blaze-Expression enabled Blaze-Domain model for the Azure VirtualMachine model.
 * <p/>
 * The setup involves creating an OAuth object and then an ApiClient:
 * <pre>
 * String tenantId = "...";
 * String basePath = "https://login.microsoftonline.com/" + tenantId;
 * String clientId = "...";
 * String clientSecret = "..";
 * OAuth oAuth = new OAuth( basePath, "/oauth2/v2.0/token" )
 *     .setCredentials( clientId, clientSecret, false )
 *     // Default scope
 *     .setScope( "https://management.core.windows.net//.default" );
 * ApiClient apiClient = new ApiClient(Map.of("azure_auth", oAuth));
 * </pre>
 * <p/>
 * For data querying, create new Api objects and pass the ApiClient.
 * <pre>
 * String apiVersion = "2024-03-01";
 * String subscriptionId = "...";
 * VirtualMachinesApi api = new VirtualMachinesApi(apiClient);
 * VirtualMachineListResult virtualMachineListResult = api.virtualMachinesListAll(
 *     apiVersion,
 *     subscriptionId,
 *     null,
 *     null,
 *     null
 * );
 * </pre>
 */
package com.blazebit.query.connector.azure.vm;