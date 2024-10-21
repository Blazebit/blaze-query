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

package com.blazebit.query.connector.github.v0314;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import com.blazebit.query.connector.base.ConventionContext;
import com.blazebit.query.connector.github.v0314.model.AbstractOpenApiSchema;

/**
 * A method filter to exclude internal methods from the Github models.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class GithubConventionContext implements ConventionContext {

    public static final ConventionContext INSTANCE = new GithubConventionContext();

    private GithubConventionContext() {
    }

    @Override
    public boolean nullOnException(Method method) {
        // An OpenAPI schema is a union type, so some getters may throw exceptions based on actual instance
        return AbstractOpenApiSchema.class.isAssignableFrom(method.getDeclaringClass());
    }

    @Override
    public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
        if (AbstractOpenApiSchema.class.isAssignableFrom(concreteClass)) {
            switch (member.getName()) {
                case "getSchemas":
                case "getActualInstance":
                case "getActualInstanceRecursively":
                case "getSchemaType":
                case "isNullable":
                    return null;
                default:
                    return this;
            }
        }
        if (member instanceof Method) {
            Method method = (Method) member;
            if (method.getName().endsWith("_JsonNullable")) {
                return null;
            }
        }
        return this;
    }
}
