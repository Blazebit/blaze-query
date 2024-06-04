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

package com.blazebit.query.impl.calcite.converter;

/**
 * Accessor for an attribute of an object.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface Converter<Source, Target> {

    /**
     * Returns the attribute value for the attribute accessed by this accessor of the given object.
     * @param o The object
     * @return The attribute value
     */
    Target convert(Source o);
}