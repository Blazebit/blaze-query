/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v1.model.Organization;
import com.datadog.api.client.v1.model.OrganizationSettings;

import java.util.List;

/**
 * Represents Datadog organization-level security settings. Used for compliance checks
 * such as SAML enforcement, strict mode (SSO required for all users), and widget sharing.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public record DatadogOrganizationSettings(
		String publicId,
		String name,
		Boolean samlEnabled,
		Boolean samlStrictMode,
		Boolean samlIdpInitiatedLoginEnabled,
		Boolean samlAutocreateUsersEnabled,
		List<String> samlAutocreateUsersDomains,
		Boolean samlIdpMetadataUploaded,
		Boolean privateWidgetShare
) {

	/**
	 * Maps a Datadog SDK {@link Organization} to a {@link DatadogOrganizationSettings} record.
	 */
	public static DatadogOrganizationSettings from(Organization org) {
		OrganizationSettings settings = org.getSettings();
		if ( settings == null ) {
			return new DatadogOrganizationSettings(
					org.getPublicId(), org.getName(),
					null, null, null, null, List.of(), null, null );
		}
		Boolean samlEnabled = settings.getSaml() != null ? settings.getSaml().getEnabled() : null;
		Boolean samlStrictMode = settings.getSamlStrictMode() != null ? settings.getSamlStrictMode().getEnabled() : null;
		Boolean samlIdpInitiatedLogin = settings.getSamlIdpInitiatedLogin() != null ? settings.getSamlIdpInitiatedLogin().getEnabled() : null;
		Boolean samlAutocreateUsers = settings.getSamlAutocreateUsersDomains() != null ? settings.getSamlAutocreateUsersDomains().getEnabled() : null;
		List<String> samlDomains = settings.getSamlAutocreateUsersDomains() != null
				&& settings.getSamlAutocreateUsersDomains().getDomains() != null
				? settings.getSamlAutocreateUsersDomains().getDomains()
				: List.of();
		return new DatadogOrganizationSettings(
				org.getPublicId(),
				org.getName(),
				samlEnabled,
				samlStrictMode,
				samlIdpInitiatedLogin,
				samlAutocreateUsers,
				samlDomains,
				settings.getSamlIdpMetadataUploaded(),
				settings.getPrivateWidgetShare()
		);
	}
}
