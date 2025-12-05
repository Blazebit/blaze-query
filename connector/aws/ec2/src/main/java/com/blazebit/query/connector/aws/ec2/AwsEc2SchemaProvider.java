/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the AWS EC2 connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AwsEc2SchemaProvider implements QuerySchemaProvider {
	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				InstanceDataFetcher.INSTANCE,
				InstanceStatusDataFetcher.INSTANCE,
				AddressDataFetcher.INSTANCE,
				SubnetDataFetcher.INSTANCE,
				CustomerGatewayDataFetcher.INSTANCE,
				InternetGatewayDataFetcher.INSTANCE,
				NatGatewayDataFetcher.INSTANCE,
				RouteTableDataFetcher.INSTANCE,
				VpcEndpointServiceDataFetcher.INSTANCE,
				VpcPeeringConnectionDataFetcher.INSTANCE,
				VpnGatewayDataFetcher.INSTANCE,
				ClientVpnEndpointDataFetcher.INSTANCE,
				VpnConnectionDataFetcher.INSTANCE,
				AwsVpnConnectionTunnelOptionDataFetcher.INSTANCE,
				VpcBlockPublicAccessOptionsDataFetcher.INSTANCE,
				TransitGatewayDataFetcher.INSTANCE,
				TransitGatewayAttachmentDataFetcher.INSTANCE,
				TransitGatewayRouteTableDataFetcher.INSTANCE,
				NetworkInterfaceDataFetcher.INSTANCE,
				SpotFleetRequestDataFetcher.INSTANCE,
				AwsSpotFleetRequestLaunchSpecificationDataFetcher.INSTANCE,
				DhcpOptionsDataFetcher.INSTANCE,
				LaunchTemplateDataFetcher.INSTANCE,
				LaunchTemplateDataFromInstanceDataFetcher.INSTANCE,
				PrefixListDataFetcher.INSTANCE,
				ManagedPrefixListDataFetcher.INSTANCE,
				TrafficMirrorSessionDataFetcher.INSTANCE,
				TrafficMirrorFilterDataFetcher.INSTANCE,
				TrafficMirrorTargetDataFetcher.INSTANCE,
				LatestConsoleOutputDataFetcher.INSTANCE,
				VpcDataFetcher.INSTANCE,
				SecurityGroupDataFetcher.INSTANCE,
				AwsEc2SecurityGroupIpPermissionDataFetcher.INSTANCE,
				VolumeDataFetcher.INSTANCE,
				SnapshotDataFetcher.INSTANCE,
				SnapshotAttributeDataFetcher.INSTANCE,
				NetworkAclDataFetcher.INSTANCE,
				AwsEc2NetworkAclEntryDataFetcher.INSTANCE,
				EbsEncryptionByDefaultDataFetcher.INSTANCE,
				FlowLogsDataFetcher.INSTANCE,
				VpcEndpointsDataFetcher.INSTANCE
		);
	}
}
