package com.room.reservation.system.api.controller;

import com.room.reservation.system.api.dto.payment.request.PaymentRequestDto;
import com.room.reservation.system.api.dto.payment.response.PaymentPendingResponseDto;
import com.room.reservation.system.api.dto.payment.PaymentStatusRequestDto;
import com.room.reservation.system.api.dto.payment.PaymentStatusResponseDto;
import com.room.reservation.system.api.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;

    @PostMapping("/reservations/{reservationId}/payment")
    public ResponseEntity<PaymentPendingResponseDto> processPayment(
        @PathVariable("reservationId") Long reservationId,
        @Valid @RequestBody PaymentRequestDto request
    ) {
        PaymentPendingResponseDto response = paymentService.processPayment(reservationId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payments/{paymentId}/status")
    public ResponseEntity<PaymentStatusResponseDto> getPaymentStatus(
        @PathVariable("paymentId") Long paymentId,
        @Valid @RequestBody PaymentStatusRequestDto request
    ) {
        PaymentStatusResponseDto response = paymentService.getPaymentStatus(paymentId, request);
        return ResponseEntity.ok(response);
    }
} 