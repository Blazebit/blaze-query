package com.blazebit.query.connector.azure.graph;

import java.util.Optional;

public enum ServicePlanName {
    AAD_PREMIUM("AAD_PREMIUM"),
    AAD_PREMIUM_P2("AAD_PREMIUM_P2");

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
}
