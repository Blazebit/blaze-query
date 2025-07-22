/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import software.amazon.awssdk.services.iam.model.GetAccountSummaryResponse;
import software.amazon.awssdk.services.iam.model.SummaryKeyType;

import java.util.Map;

/**
 * @author Martijn Sprengers
 * @since 1.0.0
 */
public class AccountSummary {

	private final String accountId;
	private final int groupPolicySizeQuota;
	private final int policies;
	private final int groupsPerUserQuota;
	private final int attachedPoliciesPerUserQuota;
	private final int users;
	private final int policiesQuota;
	private final int accountMFAEnabled;
	private final int accessKeysPerUserQuota;
	private final int policyVersionsInUseQuota;
	private final int globalEndpointTokenVersion;
	private final int versionsPerPolicyQuota;
	private final int attachedPoliciesPerGroupQuota;
	private final int policySizeQuota;
	private final int groups;
	private final int accountSigningCertificatesPresent;
	private final int usersQuota;
	private final int serverCertificatesQuota;
	private final int mfaDevices;
	private final int userPolicySizeQuota;
	private final int policyVersionsInUse;
	private final int serverCertificates;
	private final int signingCertificatesPerUserQuota;
	private final int mfaDevicesInUse;
	private final int attachedPoliciesPerRoleQuota;
	private final int accountAccessKeysPresent;
	private final int groupsQuota;

	/**
	 * Creates a new account summary from the {@link GetAccountSummaryResponse#summaryMap()} result for an account.
	 *
	 * @param accountId The account id
	 * @param summaryKeyTypeIntegerMap The {@link GetAccountSummaryResponse#summaryMap()}
	 */
	public AccountSummary(String accountId, Map<SummaryKeyType, Integer> summaryKeyTypeIntegerMap) {
		this.accountId = accountId;
		this.groupPolicySizeQuota = summaryKeyTypeIntegerMap.get( SummaryKeyType.GROUP_POLICY_SIZE_QUOTA );
		this.policies = summaryKeyTypeIntegerMap.get( SummaryKeyType.POLICIES );
		this.groupsPerUserQuota = summaryKeyTypeIntegerMap.get( SummaryKeyType.GROUPS_PER_USER_QUOTA );
		this.attachedPoliciesPerUserQuota = summaryKeyTypeIntegerMap.get(
				SummaryKeyType.ATTACHED_POLICIES_PER_USER_QUOTA );
		this.users = summaryKeyTypeIntegerMap.get( SummaryKeyType.USERS );
		this.policiesQuota = summaryKeyTypeIntegerMap.get( SummaryKeyType.POLICIES_QUOTA );
		this.accountMFAEnabled = summaryKeyTypeIntegerMap.get( SummaryKeyType.ACCOUNT_MFA_ENABLED );
		this.accessKeysPerUserQuota = summaryKeyTypeIntegerMap.get( SummaryKeyType.ACCESS_KEYS_PER_USER_QUOTA );
		this.policyVersionsInUseQuota = summaryKeyTypeIntegerMap.get( SummaryKeyType.POLICY_VERSIONS_IN_USE_QUOTA );
		this.globalEndpointTokenVersion = summaryKeyTypeIntegerMap.get( SummaryKeyType.GLOBAL_ENDPOINT_TOKEN_VERSION );
		this.versionsPerPolicyQuota = summaryKeyTypeIntegerMap.get( SummaryKeyType.VERSIONS_PER_POLICY_QUOTA );
		this.attachedPoliciesPerGroupQuota = summaryKeyTypeIntegerMap.get(
				SummaryKeyType.ATTACHED_POLICIES_PER_GROUP_QUOTA );
		this.policySizeQuota = summaryKeyTypeIntegerMap.get( SummaryKeyType.POLICY_SIZE_QUOTA );
		this.groups = summaryKeyTypeIntegerMap.get( SummaryKeyType.GROUPS );
		this.accountSigningCertificatesPresent = summaryKeyTypeIntegerMap.get(
				SummaryKeyType.ACCOUNT_SIGNING_CERTIFICATES_PRESENT );
		this.usersQuota = summaryKeyTypeIntegerMap.get( SummaryKeyType.USERS_QUOTA );
		this.serverCertificatesQuota = summaryKeyTypeIntegerMap.get( SummaryKeyType.SERVER_CERTIFICATES_QUOTA );
		this.mfaDevices = summaryKeyTypeIntegerMap.get( SummaryKeyType.MFA_DEVICES );
		this.userPolicySizeQuota = summaryKeyTypeIntegerMap.get( SummaryKeyType.USER_POLICY_SIZE_QUOTA );
		this.policyVersionsInUse = summaryKeyTypeIntegerMap.get( SummaryKeyType.POLICY_VERSIONS_IN_USE );
		this.serverCertificates = summaryKeyTypeIntegerMap.get( SummaryKeyType.SERVER_CERTIFICATES );
		this.signingCertificatesPerUserQuota = summaryKeyTypeIntegerMap.get(
				SummaryKeyType.SIGNING_CERTIFICATES_PER_USER_QUOTA );
		this.mfaDevicesInUse = summaryKeyTypeIntegerMap.get( SummaryKeyType.MFA_DEVICES_IN_USE );
		this.attachedPoliciesPerRoleQuota = summaryKeyTypeIntegerMap.get(
				SummaryKeyType.ATTACHED_POLICIES_PER_ROLE_QUOTA );
		this.accountAccessKeysPresent = summaryKeyTypeIntegerMap.get( SummaryKeyType.ACCOUNT_ACCESS_KEYS_PRESENT );
		this.groupsQuota = summaryKeyTypeIntegerMap.get( SummaryKeyType.GROUPS_QUOTA );
	}

	public String getAccountId() {
		return accountId;
	}

	public int groupPolicySizeQuota() {
		return groupPolicySizeQuota;
	}

	public int policies() {
		return policies;
	}

	public int groupsPerUserQuota() {
		return groupsPerUserQuota;
	}

	public int attachedPoliciesPerUserQuota() {
		return attachedPoliciesPerUserQuota;
	}

	public int users() {
		return users;
	}

	public int policiesQuota() {
		return policiesQuota;
	}

	public int accountMFAEnabled() {
		return accountMFAEnabled;
	}

	public int accessKeysPerUserQuota() {
		return accessKeysPerUserQuota;
	}

	public int policyVersionsInUseQuota() {
		return policyVersionsInUseQuota;
	}

	public int globalEndpointTokenVersion() {
		return globalEndpointTokenVersion;
	}

	public int versionsPerPolicyQuota() {
		return versionsPerPolicyQuota;
	}

	public int attachedPoliciesPerGroupQuota() {
		return attachedPoliciesPerGroupQuota;
	}

	public int policySizeQuota() {
		return policySizeQuota;
	}

	public int groups() {
		return groups;
	}

	public int accountSigningCertificatesPresent() {
		return accountSigningCertificatesPresent;
	}

	public int usersQuota() {
		return usersQuota;
	}

	public int serverCertificatesQuota() {
		return serverCertificatesQuota;
	}

	public int mfaDevices() {
		return mfaDevices;
	}

	public int userPolicySizeQuota() {
		return userPolicySizeQuota;
	}

	public int policyVersionsInUse() {
		return policyVersionsInUse;
	}

	public int serverCertificates() {
		return serverCertificates;
	}

	public int signingCertificatesPerUserQuota() {
		return signingCertificatesPerUserQuota;
	}

	public int mfaDevicesInUse() {
		return mfaDevicesInUse;
	}

	public int attachedPoliciesPerRoleQuota() {
		return attachedPoliciesPerRoleQuota;
	}

	public int accountAccessKeysPresent() {
		return accountAccessKeysPresent;
	}

	public int groupsQuota() {
		return groupsQuota;
	}
}
