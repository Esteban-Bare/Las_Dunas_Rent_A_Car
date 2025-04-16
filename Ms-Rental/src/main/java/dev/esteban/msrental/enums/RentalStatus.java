package dev.esteban.msrental.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RentalStatus {
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED"),
    CANCELED("CANCELED"),
    RETURNED("RETURNED"),
    OVERDUE("OVERDUE");

    private final String status;
}
