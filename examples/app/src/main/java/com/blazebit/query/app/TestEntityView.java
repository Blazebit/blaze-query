/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.app;

import java.util.Set;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;

@EntityView(TestEntity.class)
public interface TestEntityView {
	@IdMapping
	Long getId();

	String getName();

	TestEmbeddableView getEmbedded();

	Set<TestEmbeddableView> getElements();
}
