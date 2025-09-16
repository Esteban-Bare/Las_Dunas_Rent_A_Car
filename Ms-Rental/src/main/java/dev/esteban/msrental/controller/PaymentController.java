package dev.esteban.msrental.controller;

import dev.esteban.msrental.dto.PaymentDto;
import dev.esteban.msrental.dto.PaymentProcessDto;
import dev.esteban.msrental.model.Payment;
import dev.esteban.msrental.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rental/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/{paymentId}/process")
    public ResponseEntity<?> processPayment(@PathVariable String paymentId, @RequestBody PaymentProcessDto paymentProcessDto) {
        try {
            Payment processedPayment = paymentService.processPayment(Long.valueOf(paymentId), paymentProcessDto.getPaymentMethod());
            return ResponseEntity.ok(processedPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<?> cancelPayment(@PathVariable Long paymentId) {
        try {
            Payment canceledPayment = paymentService.cancelPayment(paymentId);
            return ResponseEntity.ok(canceledPayment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDto>> getUserPayments(@PathVariable Integer userId) {
        List<Payment> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments.stream().map(PaymentDto::new).toList());
    }

    @GetMapping("/user/{userId}/total")
    public ResponseEntity<BigDecimal> getUserTotalPayments(@PathVariable Integer userId) {
        BigDecimal total = paymentService.getTotalPaymentsByUser(userId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<BigDecimal> getUserPendingPayments(@PathVariable Integer userId) {
        BigDecimal pending = paymentService.getPendingPaymentsByUser(userId);
        return ResponseEntity.ok(pending);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPayment(@PathVariable Long paymentId) {
        try {
            Payment payment = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Payment not found: " + e.getMessage());
        }
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<?> refundPayment(@PathVariable Long paymentId, @RequestParam BigDecimal amount) {
        try {
            Payment refund = paymentService.refundPayment(paymentId, amount);
            return ResponseEntity.ok(refund);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing refund: " + e.getMessage());
        }
    }
}
