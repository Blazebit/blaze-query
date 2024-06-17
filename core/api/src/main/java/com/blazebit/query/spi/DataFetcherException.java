/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.query.spi;

/**
 * Represents an exception that occurs during data fetching. This exception can be used to handle
 * errors that occur during data retrieval or processing.
 *
 * @author Max Hovens
 * @since 1.0.0
 */
public class DataFetcherException extends Throwable {

    /**
     * Constructs a new DataFetcherException with the specified detail message.
     *
     * @param message the detail message
     * @throws NullPointerException if the message is null
     */
    public DataFetcherException(String message) {
        super(message);
    }

    /**
     * Represents an exception that occurs during data fetching. This exception can be used to
     * handle errors that occur during data retrieval or processing.
     *
     * @param message the description of the exception
     * @param cause   the underlying cause of the exception
     */
    public DataFetcherException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DataFetcherException with the specified cause.
     *
     * @param cause the cause of the exception
     * @see Throwable#Throwable(Throwable)
     */
    public DataFetcherException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new DataFetcherException with the specified detail message, cause, suppression
     * enabled or disabled, and writable stack trace enabled or disabled.
     *
     * @param message            the detail message (which is saved for later retrieval by the
     *                           getMessage method).
     * @param cause              the cause (which is saved for later retrieval by the getCause
     *                           method). (A null value is permitted, and indicates that the cause
     *                           is nonexistent or unknown).
     * @param enableSuppression  whether suppression is enabled or disabled.
     * @param writableStackTrace whether the stack trace should be writable.
     */
    public DataFetcherException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
