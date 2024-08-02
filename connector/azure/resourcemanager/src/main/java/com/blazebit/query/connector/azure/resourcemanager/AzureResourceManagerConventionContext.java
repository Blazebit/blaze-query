/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        switch (member.getName()) {
            case "getDetails":
                return concreteClass != ManagementError.class ? this : NestedManagementErrorContext.INSTANCE;
            default:
                return this;
        }
    }

    @Override
    public boolean isEnumType(Class<?> typeClass) {
        return ConventionContext.super.isEnumType(typeClass)
                || ExpandableStringEnum.class.isAssignableFrom(typeClass);
    }

    private static final class NestedManagementErrorContext implements ConventionContext {

        private static final NestedManagementErrorContext INSTANCE = new NestedManagementErrorContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
            switch (member.getName()) {
                // Filter out cycles in the model
                case "getDetails":
                    return concreteClass != ManagementError.class ? this : null;
                default:
                    return this;
            }
        }
    }
}
