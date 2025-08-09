package com.example.demo.controller;

import com.example.demo.dto.PaymentProviderRequestDto;
import com.example.demo.dto.PaymentPendingResponseDto;
import com.example.demo.service.SimplePaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class ToTalPaymentController {

    private final SimplePaymentService simplePaymentService;

    @PostMapping
    public ResponseEntity<PaymentPendingResponseDto> processPayment(@RequestBody PaymentProviderRequestDto request) {
        PaymentPendingResponseDto response = simplePaymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }
} 