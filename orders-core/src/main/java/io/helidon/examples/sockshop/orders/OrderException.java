/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.orders;

/**
 * Base class for all business-level order processing exceptions.
 */
public class OrderException extends IllegalStateException {
    /**
     * Construct {@code OrderException} instance.
     *
     * @param message error message
     */
    public OrderException(String message) {
        super(message);
    }
}
