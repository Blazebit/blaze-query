/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import java.util.Optional;

public enum ServicePlanName {
	AAD_PREMIUM("AAD_PREMIUM"),
	AAD_PREMIUM_P2("AAD_PREMIUM_P2"),
	INTUNE_A("INTUNE_A"),
	INTUNE_A_GOV("INTUNE_A_GOV"),
	INTUNE_A_VL("INTUNE_A_VL"),
	INTUNE_O365("INTUNE_O365"),
	INTUNE_Defender("Intune_Defender"),
	INTUNE_AdvancedEA("Intune_AdvancedEA"),
	INTUNE_EPM("Intune-EPM"),
	INTUNE_MAMTunnel("Intune-MAMTunnel"),
	INTUNE_EDU("INTUNE_EDU"),
	INTUNE_P2("INTUNE_P2"),
	INTUNE_SMBIZ("INTUNE_SMBIZ");

	private final String name;

	ServicePlanName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static Optional<ServicePlanName> fromName(String name) {
		for (ServicePlanName plan : values()) {
			if (plan.name.equals(name)) {
				return Optional.of(plan);
			}
		}
		return Optional.empty();
	}

	public boolean isAad() {
		return this.name.toUpperCase().startsWith("AAD");
	}

	public boolean isIntune() {
		return this.name.toUpperCase().startsWith("INTUNE");
	}
}
