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
				AwsEc2InstanceDataFetcher.INSTANCE,
				AwsEc2InstanceStatusDataFetcher.INSTANCE,
				AwsEc2AddressDataFetcher.INSTANCE,
				AwsEc2SubnetDataFetcher.INSTANCE,
				AwsEc2CustomerGatewayDataFetcher.INSTANCE,
				AwsEc2InternetGatewayDataFetcher.INSTANCE,
				AwsEc2NatGatewayDataFetcher.INSTANCE,
				AwsEc2RouteTableDataFetcher.INSTANCE,
				AwsEc2VpcEndpointServiceDataFetcher.INSTANCE,
				AwsEc2VpcPeeringConnectionDataFetcher.INSTANCE,
				AwsEc2VpnGatewayDataFetcher.INSTANCE,
				AwsEc2ClientVpnEndpointDataFetcher.INSTANCE,
				AwsEc2VpnConnectionDataFetcher.INSTANCE,
				AwsEc2VpnConnectionTunnelOptionDataFetcher.INSTANCE,
				AwsEc2VpcBlockPublicAccessOptionsDataFetcher.INSTANCE,
				AwsEc2TransitGatewayDataFetcher.INSTANCE,
				AwsEc2TransitGatewayAttachmentDataFetcher.INSTANCE,
				AwsEc2TransitGatewayRouteTableDataFetcher.INSTANCE,
				AwsEc2NetworkInterfaceDataFetcher.INSTANCE,
				AwsEc2SpotFleetRequestDataFetcher.INSTANCE,
				AwsEc2SpotFleetRequestLaunchSpecificationDataFetcher.INSTANCE,
				AwsEc2DhcpOptionsDataFetcher.INSTANCE,
				AwsEc2LaunchTemplateDataFetcher.INSTANCE,
				AwsEc2LaunchTemplateVersionDataFetcher.INSTANCE,
				AwsEc2ManagedPrefixListDataFetcher.INSTANCE,
				AwsEc2TrafficMirrorSessionDataFetcher.INSTANCE,
				AwsEc2TrafficMirrorFilterDataFetcher.INSTANCE,
				AwsEc2TrafficMirrorTargetDataFetcher.INSTANCE,
				AwsEc2VpcDataFetcher.INSTANCE,
				AwsEc2SecurityGroupDataFetcher.INSTANCE,
				AwsEc2SecurityGroupIpPermissionDataFetcher.INSTANCE,
				AwsEc2VolumeDataFetcher.INSTANCE,
				AwsEc2SnapshotDataFetcher.INSTANCE,
				AwsEc2SnapshotAttributeDataFetcher.INSTANCE,
				AwsEc2NetworkAclDataFetcher.INSTANCE,
				AwsEc2NetworkAclEntryDataFetcher.INSTANCE,
				AwsEc2EbsEncryptionByDefaultDataFetcher.INSTANCE,
				AwsEc2FlowLogsDataFetcher.INSTANCE,
				AwsEc2VpcEndpointDataFetcher.INSTANCE
		);
	}
}
