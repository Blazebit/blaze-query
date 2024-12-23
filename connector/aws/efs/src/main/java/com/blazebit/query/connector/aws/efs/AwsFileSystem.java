/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.efs;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.efs.model.FileSystemDescription;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsFileSystem extends AwsWrapper<FileSystemDescription> {
	public AwsFileSystem(String arn, FileSystemDescription payload) {
		super( arn, payload );
	}

	@Override
	public FileSystemDescription getPayload() {
		return super.getPayload();
	}
}
