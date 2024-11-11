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

	private int groupPolicySizeQuota;
	private int instanceProfilesQuota;
	private int policies;
	private int groupsPerUserQuota;
	private int instanceProfiles;
	private int attachedPoliciesPerUserQuota;
	private int users;
	private int policiesQuota;
	private int providers;
	private int accountMFAEnabled;
	private int accessKeysPerUserQuota;
	private int assumeRolePolicySizeQuota;
	private int policyVersionsInUseQuota;
	private int globalEndpointTokenVersion;
	private int versionsPerPolicyQuota;
	private int attachedPoliciesPerGroupQuota;
	private int policySizeQuota;
	private int groups;
	private int accountSigningCertificatesPresent;
	private int usersQuota;
	private int serverCertificatesQuota;
	private int mfaDevices;
	private int userPolicySizeQuota;
	private int policyVersionsInUse;
	private int serverCertificates;
	private int roles;
	private int rolesQuota;
	private int signingCertificatesPerUserQuota;
	private int mfaDevicesInUse;
	private int rolePolicySizeQuota;
	private int attachedPoliciesPerRoleQuota;
	private int accountAccessKeysPresent;
	private int accountPasswordPresent;
	private int groupsQuota;

	/**
	 * Creates a new account summary from the {@link GetAccountSummaryResponse#summaryMap()} result.
	 *
	 * @param summaryKeyTypeIntegerMap The {@link GetAccountSummaryResponse#summaryMap()}
	 */
	public AccountSummary(Map<SummaryKeyType, Integer> summaryKeyTypeIntegerMap) {
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

	public int getGroupPolicySizeQuota() {
		return groupPolicySizeQuota;
	}

	public void setGroupPolicySizeQuota(int groupPolicySizeQuota) {
		this.groupPolicySizeQuota = groupPolicySizeQuota;
	}

	public int getInstanceProfilesQuota() {
		return instanceProfilesQuota;
	}

	public void setInstanceProfilesQuota(int instanceProfilesQuota) {
		this.instanceProfilesQuota = instanceProfilesQuota;
	}

	public int getPolicies() {
		return policies;
	}

	public void setPolicies(int policies) {
		this.policies = policies;
	}

	public int getGroupsPerUserQuota() {
		return groupsPerUserQuota;
	}

	public void setGroupsPerUserQuota(int groupsPerUserQuota) {
		this.groupsPerUserQuota = groupsPerUserQuota;
	}

	public int getInstanceProfiles() {
		return instanceProfiles;
	}

	public void setInstanceProfiles(int instanceProfiles) {
		this.instanceProfiles = instanceProfiles;
	}

	public int getAttachedPoliciesPerUserQuota() {
		return attachedPoliciesPerUserQuota;
	}

	public void setAttachedPoliciesPerUserQuota(int attachedPoliciesPerUserQuota) {
		this.attachedPoliciesPerUserQuota = attachedPoliciesPerUserQuota;
	}

	public int getUsers() {
		return users;
	}

	public void setUsers(int users) {
		this.users = users;
	}

	public int getPoliciesQuota() {
		return policiesQuota;
	}

	public void setPoliciesQuota(int policiesQuota) {
		this.policiesQuota = policiesQuota;
	}

	public int getProviders() {
		return providers;
	}

	public void setProviders(int providers) {
		this.providers = providers;
	}

	public int getAccountMFAEnabled() {
		return accountMFAEnabled;
	}

	public void setAccountMFAEnabled(int accountMFAEnabled) {
		this.accountMFAEnabled = accountMFAEnabled;
	}

	public int getAccessKeysPerUserQuota() {
		return accessKeysPerUserQuota;
	}

	public void setAccessKeysPerUserQuota(int accessKeysPerUserQuota) {
		this.accessKeysPerUserQuota = accessKeysPerUserQuota;
	}

	public int getAssumeRolePolicySizeQuota() {
		return assumeRolePolicySizeQuota;
	}

	public void setAssumeRolePolicySizeQuota(int assumeRolePolicySizeQuota) {
		this.assumeRolePolicySizeQuota = assumeRolePolicySizeQuota;
	}

	public int getPolicyVersionsInUseQuota() {
		return policyVersionsInUseQuota;
	}

	public void setPolicyVersionsInUseQuota(int policyVersionsInUseQuota) {
		this.policyVersionsInUseQuota = policyVersionsInUseQuota;
	}

	public int getGlobalEndpointTokenVersion() {
		return globalEndpointTokenVersion;
	}

	public void setGlobalEndpointTokenVersion(int globalEndpointTokenVersion) {
		this.globalEndpointTokenVersion = globalEndpointTokenVersion;
	}

	public int getVersionsPerPolicyQuota() {
		return versionsPerPolicyQuota;
	}

	public void setVersionsPerPolicyQuota(int versionsPerPolicyQuota) {
		this.versionsPerPolicyQuota = versionsPerPolicyQuota;
	}

	public int getAttachedPoliciesPerGroupQuota() {
		return attachedPoliciesPerGroupQuota;
	}

	public void setAttachedPoliciesPerGroupQuota(int attachedPoliciesPerGroupQuota) {
		this.attachedPoliciesPerGroupQuota = attachedPoliciesPerGroupQuota;
	}

	public int getPolicySizeQuota() {
		return policySizeQuota;
	}

	public void setPolicySizeQuota(int policySizeQuota) {
		this.policySizeQuota = policySizeQuota;
	}

	public int getGroups() {
		return groups;
	}

	public void setGroups(int groups) {
		this.groups = groups;
	}

	public int getAccountSigningCertificatesPresent() {
		return accountSigningCertificatesPresent;
	}

	public void setAccountSigningCertificatesPresent(int accountSigningCertificatesPresent) {
		this.accountSigningCertificatesPresent = accountSigningCertificatesPresent;
	}

	public int getUsersQuota() {
		return usersQuota;
	}

	public void setUsersQuota(int usersQuota) {
		this.usersQuota = usersQuota;
	}

	public int getServerCertificatesQuota() {
		return serverCertificatesQuota;
	}

	public void setServerCertificatesQuota(int serverCertificatesQuota) {
		this.serverCertificatesQuota = serverCertificatesQuota;
	}

	public int getMfaDevices() {
		return mfaDevices;
	}

	public void setMfaDevices(int mfaDevices) {
		this.mfaDevices = mfaDevices;
	}

	public int getUserPolicySizeQuota() {
		return userPolicySizeQuota;
	}

	public void setUserPolicySizeQuota(int userPolicySizeQuota) {
		this.userPolicySizeQuota = userPolicySizeQuota;
	}

	public int getPolicyVersionsInUse() {
		return policyVersionsInUse;
	}

	public void setPolicyVersionsInUse(int policyVersionsInUse) {
		this.policyVersionsInUse = policyVersionsInUse;
	}

	public int getServerCertificates() {
		return serverCertificates;
	}

	public void setServerCertificates(int serverCertificates) {
		this.serverCertificates = serverCertificates;
	}

	public int getRoles() {
		return roles;
	}

	public void setRoles(int roles) {
		this.roles = roles;
	}

	public int getRolesQuota() {
		return rolesQuota;
	}

	public void setRolesQuota(int rolesQuota) {
		this.rolesQuota = rolesQuota;
	}

	public int getSigningCertificatesPerUserQuota() {
		return signingCertificatesPerUserQuota;
	}

	public void setSigningCertificatesPerUserQuota(int signingCertificatesPerUserQuota) {
		this.signingCertificatesPerUserQuota = signingCertificatesPerUserQuota;
	}

	public int getMfaDevicesInUse() {
		return mfaDevicesInUse;
	}

	public void setMfaDevicesInUse(int mfaDevicesInUse) {
		this.mfaDevicesInUse = mfaDevicesInUse;
	}

	public int getRolePolicySizeQuota() {
		return rolePolicySizeQuota;
	}

	public void setRolePolicySizeQuota(int rolePolicySizeQuota) {
		this.rolePolicySizeQuota = rolePolicySizeQuota;
	}

	public int getAttachedPoliciesPerRoleQuota() {
		return attachedPoliciesPerRoleQuota;
	}

	public void setAttachedPoliciesPerRoleQuota(int attachedPoliciesPerRoleQuota) {
		this.attachedPoliciesPerRoleQuota = attachedPoliciesPerRoleQuota;
	}

	public int getAccountAccessKeysPresent() {
		return accountAccessKeysPresent;
	}

	public void setAccountAccessKeysPresent(int accountAccessKeysPresent) {
		this.accountAccessKeysPresent = accountAccessKeysPresent;
	}

	public int getAccountPasswordPresent() {
		return accountPasswordPresent;
	}

	public void setAccountPasswordPresent(int accountPasswordPresent) {
		this.accountPasswordPresent = accountPasswordPresent;
	}

	public int getGroupsQuota() {
		return groupsQuota;
	}

	public void setGroupsQuota(int groupsQuota) {
		this.groupsQuota = groupsQuota;
	}
}
