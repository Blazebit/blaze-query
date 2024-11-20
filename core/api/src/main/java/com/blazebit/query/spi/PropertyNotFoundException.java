/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.spi;

/**
 * Represents an exception that occurs during fetching of a property from the {@link DataFetchContext}.
 * This exception can be used to handle errors that occur during data retrieval or processing.
 *
 * @author Max Hovens
 * @since 1.0.0
 */
public class PropertyNotFoundException extends RuntimeException {

	/**
	 * Constructs a new PropertyNotFoundException with the specified detail message.
	 *
	 * @param message the detail message
	 * @throws NullPointerException if the message is null
	 */
	public PropertyNotFoundException(String message) {
		super( message );
	}

	/**
	 * Represents an exception that occurs during data fetching. This exception can be used to
	 * handle errors that occur during data retrieval or processing.
	 *
	 * @param message the description of the exception
	 * @param cause the underlying cause of the exception
	 */
	public PropertyNotFoundException(String message, Throwable cause) {
		super( message, cause );
	}

	/**
	 * Constructs a new PropertyNotFoundException with the specified cause.
	 *
	 * @param cause the cause of the exception
	 * @see Throwable#Throwable(Throwable)
	 */
	public PropertyNotFoundException(Throwable cause) {
		super( cause );
	}

	/**
	 * Constructs a new PropertyNotFoundException with the specified detail message, cause, suppression
	 * enabled or disabled, and writable stack trace enabled or disabled.
	 *
	 * @param message the detail message (which is saved for later retrieval by the
	 * getMessage method).
	 * @param cause the cause (which is saved for later retrieval by the getCause
	 * method). (A null value is permitted, and indicates that the cause
	 * is nonexistent or unknown).
	 * @param enableSuppression whether suppression is enabled or disabled.
	 * @param writableStackTrace whether the stack trace should be writable.
	 */
	public PropertyNotFoundException(
			String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super( message, cause, enableSuppression, writableStackTrace );
	}
}
