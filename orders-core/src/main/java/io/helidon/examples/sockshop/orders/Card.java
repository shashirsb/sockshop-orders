/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.orders;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Credit card information.
 */
@Data
@NoArgsConstructor
@Embeddable
public class Card implements Serializable {
    /**
     * Credit card number.
     */
    @Schema(description = "Credit card number")
    public String longNum;

    /**
     * Expiration date.
     */
    @Schema(description = "Expiration date")
    public String expires;

    /**
     * CCV code.
     */
    @Schema(description = "CCV code")
    public String ccv;

    @Builder
    Card(String longNum, String expires, String ccv) {
        this.longNum = longNum;
        this.expires = expires;
        this.ccv = ccv;
    }
}
