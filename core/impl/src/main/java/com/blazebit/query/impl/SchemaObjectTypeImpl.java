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
package com.blazebit.query.impl;

import com.blazebit.query.metamodel.SchemaObjectType;
import com.blazebit.query.spi.DataFetcher;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class SchemaObjectTypeImpl<T> implements SchemaObjectType<T> {

	private final int id;
	private final Class<T> schemaObjectType;
	private final DataFetcher<T> dataFetcher;

	public SchemaObjectTypeImpl(Class<T> schemaObjectType, DataFetcher<T> dataFetcher) {
		this(-1, schemaObjectType, dataFetcher);
	}

	public SchemaObjectTypeImpl(int id, Class<T> schemaObjectType, DataFetcher<T> dataFetcher) {
		this.id = id;
		this.schemaObjectType = schemaObjectType;
		this.dataFetcher = dataFetcher;
	}

	public int getId() {
		return id;
	}

	@Override
	public Class<T> getType() {
		return schemaObjectType;
	}

	@Override
	public DataFetcher<T> getDataFetcher() {
		return dataFetcher;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public final boolean equals(Object o) {
		return this == o;
	}
}
