/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.directory;

import com.google.api.services.directory.model.Member;

public class GoogleMember extends GoogleWrapper<Member> {
	public GoogleMember(String resourceId, Member member) {
		super(resourceId, member);
	}

	@Override
	public Member getPayload() {
		return super.getPayload();
	}
}
