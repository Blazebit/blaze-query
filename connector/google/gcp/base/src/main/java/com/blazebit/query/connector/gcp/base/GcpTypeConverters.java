/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.base;

import java.time.Duration;
import java.time.Instant;

/**
 * Static conversion methods for protobuf types to java.time types.
 * Default/unset protobuf instances (0s, 0ns) are converted to {@code null}.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GcpTypeConverters {

	private GcpTypeConverters() {
	}

	/**
	 * Converts a protobuf {@link com.google.protobuf.Duration} to a {@link Duration}.
	 *
	 * @param d The protobuf duration
	 * @return The java.time duration, or {@code null} if the protobuf duration is the default instance
	 */
	public static Duration convertDuration(com.google.protobuf.Duration d) {
		if ( d == null || d.equals( com.google.protobuf.Duration.getDefaultInstance() ) ) {
			return null;
		}
		return Duration.ofSeconds( d.getSeconds(), d.getNanos() );
	}

	/**
	 * Converts a protobuf {@link com.google.protobuf.Timestamp} to an {@link Instant}.
	 *
	 * @param t The protobuf timestamp
	 * @return The java.time instant, or {@code null} if the protobuf timestamp is the default instance
	 */
	public static Instant convertTimestamp(com.google.protobuf.Timestamp t) {
		if ( t == null || t.equals( com.google.protobuf.Timestamp.getDefaultInstance() ) ) {
			return null;
		}
		return Instant.ofEpochSecond( t.getSeconds(), t.getNanos() );
	}
}
