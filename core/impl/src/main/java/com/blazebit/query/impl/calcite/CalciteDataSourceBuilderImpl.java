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

package com.blazebit.query.impl.calcite;

import com.blazebit.query.calcite.CalciteDataSource;
import com.blazebit.query.calcite.CalciteDataSourceBuilder;
import java.util.Properties;

/**
 * @author Max Hovens
 * @since 1.0.0
 */
public class CalciteDataSourceBuilderImpl implements CalciteDataSourceBuilder {

    final Properties properties = new Properties();

    /**
     * Returns a new instance of {@link CalciteDataSourceBuilder} with default properties set.
     * The default property is "lex" with value "JAVA".
     *
     * @return A new instance of {@link CalciteDataSourceBuilder} with default properties set
     */
    public static CalciteDataSourceBuilder withDefaults() {
        CalciteDataSourceBuilderImpl calciteDataSourceBuilder = new CalciteDataSourceBuilderImpl();
        calciteDataSourceBuilder.setProperty("lex", "JAVA");
        return calciteDataSourceBuilder;
    }

    @Override
    public CalciteDataSourceBuilder setProperty(String name, String value) {
        properties.put(name, value);
        return this;
    }

    @Override
    public CalciteDataSource build() {
        return new CalciteDataSourceImpl(this);
    }
}
