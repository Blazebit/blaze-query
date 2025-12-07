/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.observatory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class ObservatoryTestObjects {

	private ObservatoryTestObjects() {
	}

	public static ObservatoryScan tidalcontrolScan() {
		return new ObservatoryScan(
				79627603L,
				"tidalcontrol.com",
				"https://developer.mozilla.org/en-US/observatory/analyze?host=tidalcontrol.com",
				5,
				// 2025-12-05T16:52:10.602Z
				OffsetDateTime.of(2025, 12, 5, 16, 52, 10, 602_000_000, ZoneOffset.UTC),
				null,
				"B",
				70,
				200,
				2,
				8,
				10
		);
	}
}
