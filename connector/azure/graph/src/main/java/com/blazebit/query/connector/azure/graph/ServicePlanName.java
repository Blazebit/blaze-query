package com.blazebit.query.connector.azure.graph;

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
}
