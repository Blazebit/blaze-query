/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite;

import java.util.Map;

import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.util.NameMap;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class SubSchema extends AbstractSchema {
	private final NameMap<Schema> subSchemaMap = new NameMap<>();

	public void add(String name, Schema schema) {
		subSchemaMap.put( name, schema );
	}

	@Override
	protected Map<String, Schema> getSubSchemaMap() {
		return subSchemaMap.map();
	}
}
