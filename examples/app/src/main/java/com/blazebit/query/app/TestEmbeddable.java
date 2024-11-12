/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.app;

import jakarta.persistence.Embeddable;

@Embeddable
public class TestEmbeddable {
	String text1;
	String text2;

	public TestEmbeddable() {
	}

	public TestEmbeddable(String text1, String text2) {
		this.text1 = text1;
		this.text2 = text2;
	}
}
