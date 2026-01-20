/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import software.amazon.awssdk.services.ec2.model.TunnelOption;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public record AwsEc2VpnConnectionTunnelOption(
		String accountId, String regionId, String vpnConnectionId, TunnelOption payload
) {
}
