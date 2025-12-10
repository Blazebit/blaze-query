/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Volume;
import software.amazon.awssdk.services.ec2.model.VolumeAttachment;

import java.time.Instant;
import java.util.Collections;

public final class TestObjects {

	private TestObjects() {
	}

	public static AwsEc2Instance instance() {
		Instance instance = Instance.builder().instanceId( "ami-0346fe82e44423dcb4" ).build();
		return new AwsEc2Instance(
				"123",
				Region.EU_CENTRAL_1.id(),
				instance.instanceId(),
				instance
		);
	}

	public static AwsEc2Volume volume() {
		Volume volume = Volume.builder()
				.attachments(
						Collections.singletonList(
								VolumeAttachment.builder()
										.attachTime( Instant.parse( "2024-07-15T12:41:28+00:00" ) )
										.device( "/dev/xvda" )
										.instanceId( "i-0346fe82e44423dcb4" )
										.state( "attached" )
										.volumeId( "vol-091fa36fd85941bf4" )
										.deleteOnTermination( true )
										.build() ) )
				.availabilityZone( "eu-central-1a" )
				.createTime( Instant.parse( "2024-07-15T12:41:28.896000+00:00" ) )
				.encrypted( false )
				.size( 8 )
				.snapshotId( "snap-09c6549dcab96f696" )
				.state( "in-use" )
				.volumeId( "vol-091fa36fd85941bf4" )
				.iops( 3000 )
				.volumeType( "gp3" )
				.multiAttachEnabled( false )
				.throughput( 125 )
				.build();
		return new AwsEc2Volume(
				"123",
				Region.EU_CENTRAL_1.id(),
				volume.volumeId(),
				volume
		);
	}
}
