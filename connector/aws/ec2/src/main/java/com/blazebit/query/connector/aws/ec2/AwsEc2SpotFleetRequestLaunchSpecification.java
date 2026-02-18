/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import software.amazon.awssdk.services.ec2.model.SpotFleetLaunchSpecification;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public record AwsEc2SpotFleetRequestLaunchSpecification(
		String accountId, String regionId, String spotFleetRequestId, SpotFleetLaunchSpecification payload
) {
}
