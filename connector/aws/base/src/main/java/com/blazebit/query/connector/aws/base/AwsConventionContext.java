/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.base;

import java.lang.reflect.Member;

import com.blazebit.query.connector.base.ConventionContext;

/**
 * A method filter to exclude internal and cyclic methods from the AWS models.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsConventionContext implements ConventionContext {

	public static final ConventionContext INSTANCE = new AwsConventionContext();

	private AwsConventionContext() {
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		return switch ( member.getName() ) {
			case "sdkFields", "toBuilder", "serializableBuilderClass", "getValueForField", "sdkHttpResponse",
				"base32StringSeed", "sdkFieldNameToField", "qrCodePNG", "getter", "setter", "copy" -> null;
			default -> this;
		};
	}

}
