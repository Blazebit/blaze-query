/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import java.lang.reflect.Member;

import com.azure.core.management.exception.ManagementError;
import com.azure.core.util.ExpandableStringEnum;
import com.blazebit.query.connector.base.ConventionContext;

/**
 * A method filter to exclude internal and cyclic methods from the Azure Resource Manager models.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureResourceManagerConventionContext implements ConventionContext {

	public static final ConventionContext INSTANCE = new AzureResourceManagerConventionContext();

	private AzureResourceManagerConventionContext() {
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		switch ( member.getName() ) {
			case "getDetails":
				return concreteClass != ManagementError.class ? this : NestedManagementErrorContext.INSTANCE;
			default:
				return this;
		}
	}

	@Override
	public boolean isEnumType(Class<?> typeClass) {
		return ConventionContext.super.isEnumType( typeClass )
				|| ExpandableStringEnum.class.isAssignableFrom( typeClass );
	}

	private static final class NestedManagementErrorContext implements ConventionContext {

		private static final NestedManagementErrorContext INSTANCE = new NestedManagementErrorContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				// Filter out cycles in the model
				case "getDetails":
					return concreteClass != ManagementError.class ? this : null;
				default:
					return this;
			}
		}
	}
}
