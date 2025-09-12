package dev.esteban.msrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessDto {
    private String paymentMethod;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String cardHolderName;
}
