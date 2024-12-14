/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.drive;

import com.blazebit.query.connector.base.ConventionContext;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;

import java.lang.reflect.Member;
import java.util.AbstractMap;

/**
 * A method filter to exclude internal and cyclic methods from the Google Workspace models.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class GoogleDriveConventionContext implements ConventionContext {

	public static final ConventionContext INSTANCE = new GoogleDriveConventionContext();

	private GoogleDriveConventionContext() {
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		if ( member.getDeclaringClass() == GenericJson.class
				|| member.getDeclaringClass() == GenericData.class
				|| member.getDeclaringClass() == AbstractMap.class ) {
			return null;
		}
		return this;
	}
}
