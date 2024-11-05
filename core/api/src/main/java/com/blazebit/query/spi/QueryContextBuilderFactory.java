/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.spi;

/**
 * Interface implemented by the Blaze-Query provider.
 * <p>
 * It is invoked to create {@link QueryContextBuilder}.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface QueryContextBuilderFactory {

	/**
	 * Returns a new {@link QueryContextBuilder}.
	 *
	 * @return a new {@link QueryContextBuilder}
	 */
	QueryContextBuilder creatBuilder();
}
