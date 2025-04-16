package dev.esteban.msrental.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum StatusVehicle {
    AVAILABLE("Available"),
    UNAVAILABLE("Unavailable"),
    MAINTENANCE("Maintenance"),
    RENTED("Rented");

    private final String status;
}
