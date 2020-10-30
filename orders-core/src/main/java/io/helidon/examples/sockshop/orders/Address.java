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
 * Shipping or billing address.
 */
@Data
@NoArgsConstructor
@Embeddable
public class Address implements Serializable {
    /**
     * Street number.
     */
    @Schema(description = "Street number")
    public String number;

    /**
     * Street name.
     */
    @Schema(description = "Street name")
    public String street;

    /**
     * City name.
     */
    @Schema(description = "City name")
    public String city;

    /**
     * Postal code.
     */
    @Schema(description = "Postal code")
    public String postcode;

    /**
     * Country name.
     */
    @Schema(description = "County name")
    public String country;

    @Builder
    Address(String number, String street, String city, String postcode, String country) {
        this.number = number;
        this.street = street;
        this.city = city;
        this.postcode = postcode;
        this.country = country;
    }
}
