/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.accessanalyzer;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.accessanalyzer.model.AnalyzerSummary;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsAccessAnalyzerAnalyzer extends AwsWrapper<AnalyzerSummary> {

	public AwsAccessAnalyzerAnalyzer(String accountId, String region, String resourceId, AnalyzerSummary payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public AnalyzerSummary getPayload() {
		return super.getPayload();
	}
}
