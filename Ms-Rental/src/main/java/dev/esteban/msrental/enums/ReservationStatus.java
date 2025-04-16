package dev.esteban.msrental.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    PENDING("Pending"),
    CONFIRMED("Approved"),
    CANCELLED("Cancelled");

    private final String status;
}
