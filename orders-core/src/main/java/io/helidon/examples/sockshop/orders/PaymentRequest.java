/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.orders;

import lombok.Builder;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Payment request that is sent to Payment service for authorization.
 */
@Data
@Builder
public class PaymentRequest {
    /**
     * Order identifier.
     */
    @Schema(description = "Order identifier")
    private String orderId;

    /**
     * Customer information.
     */
    @Schema(description = "Customer information")
    private Customer customer;

    /**
     * Billing address.
     */
    @Schema(description = "Billing address")
    private Address address;

    /**
     * Payment card details.
     */
    @Schema(description = "Payment card details")
    private Card card;

    /**
     * Payment amount.
     */
    @Schema(description = "Payment amount")
    private float amount;
}
