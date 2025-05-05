package dev.esteban.msrental.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String status;
}
