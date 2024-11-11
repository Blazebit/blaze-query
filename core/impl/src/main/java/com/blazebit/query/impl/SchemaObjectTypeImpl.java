/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
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
		this( -1, schemaObjectType, dataFetcher );
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
