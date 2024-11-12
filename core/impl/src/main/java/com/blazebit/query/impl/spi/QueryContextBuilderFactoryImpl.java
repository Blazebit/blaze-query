/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.spi;

import com.blazebit.query.impl.QueryContextBuilderImpl;
import com.blazebit.query.spi.QueryContextBuilder;
import com.blazebit.query.spi.QueryContextBuilderFactory;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class QueryContextBuilderFactoryImpl implements QueryContextBuilderFactory {
	@Override
	public QueryContextBuilder creatBuilder() {
		return new QueryContextBuilderImpl();
	}
}
