/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ecs;

import software.amazon.awssdk.services.ecs.model.ContainerDefinition;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public record AwsContainerDefinition(
		String taskDefinitionArn, String name, ContainerDefinition payload
) {
}
