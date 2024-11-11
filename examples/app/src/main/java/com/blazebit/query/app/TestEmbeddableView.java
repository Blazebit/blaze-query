/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.app;

import com.blazebit.persistence.view.EntityView;

@EntityView(TestEmbeddable.class)
public interface TestEmbeddableView {
	String getText1();

	String getText2();

}
