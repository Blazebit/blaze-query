/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite;

import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.sql.type.BasicSqlType;
import org.apache.calcite.sql.type.SqlTypeName;

import java.lang.reflect.Type;

public class CustomJavaTypeFactory extends JavaTypeFactoryImpl {

	public CustomJavaTypeFactory(RelDataTypeSystem typeSystem) {
		super( typeSystem );
	}

	@Override
	public Type getJavaClass(RelDataType type) {
		if ( type instanceof BasicSqlType basicSqlType && basicSqlType.getSqlTypeName().equals( SqlTypeName.UUID ) ) {
			return java.util.UUID.class;
		}

		return super.getJavaClass( type );
	}
}
