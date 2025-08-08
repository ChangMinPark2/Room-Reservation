package com.room.reservation.system.api.controller;

import com.room.reservation.system.api.dto.payment.PaymentRequestDto;
import com.room.reservation.system.api.dto.payment.PaymentResponseDto;
import com.room.reservation.system.api.dto.payment.WebhookRequestDto;
import com.room.reservation.system.api.persistence.entity.PaymentStatus;
import com.room.reservation.system.api.service.PaymentService;
import com.room.reservation.system.api.service.WebhookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 결제 관련 API 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    private final WebhookService webhookService;
    
    /**
     * 결제 처리
     * @param reservationId 예약 ID
     * @param request 결제 요청 정보
     * @return 결제 결과
     */
    @PostMapping("/reservations/{reservationId}/payment")
    public ResponseEntity<PaymentResponseDto> processPayment(
        @PathVariable Long reservationId,
        @Valid @RequestBody PaymentRequestDto request
    ) {
        PaymentResponseDto response = paymentService.processPayment(reservationId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 결제 상태 조회
     * @param paymentId 결제 ID
     * @return 결제 상태
     */
    @GetMapping("/payments/{paymentId}/status")
    public ResponseEntity<PaymentStatus> getPaymentStatus(@PathVariable String paymentId) {
        PaymentStatus status = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(status);
    }
    
    /**
     * 결제사별 웹훅 수신
     * @param provider 결제사 식별자
     * @param webhookRequest 웹훅 요청 정보
     * @return 처리 결과
     */
    @PostMapping("/webhooks/payments/{provider}")
    public ResponseEntity<String> handleWebhook(
        @PathVariable String provider,
        @Valid @RequestBody WebhookRequestDto webhookRequest
    ) {
        String result = webhookService.handleWebhook(provider, webhookRequest);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 웹훅 시뮬레이션 - A사 카드결제
     * @param paymentId 결제 ID
     * @return 시뮬레이션 결과
     */
    @PostMapping("/webhooks/simulate/card/{paymentId}")
    public ResponseEntity<String> simulateCardWebhook(@PathVariable String paymentId) {
        WebhookRequestDto webhookRequest = new WebhookRequestDto(
            paymentId,
            PaymentStatus.SUCCESS,
            "TXN_" + paymentId,
            50000,
            "카드결제 성공",
            java.time.LocalDateTime.now(),
            "card_signature_123"
        );
        
        String result = webhookService.handleWebhook("card", webhookRequest);
        return ResponseEntity.ok("카드결제 웹훅 시뮬레이션 완료: " + result);
    }
    
    /**
     * 웹훅 시뮬레이션 - B사 간편결제
     * @param paymentId 결제 ID
     * @return 시뮬레이션 결과
     */
    @PostMapping("/webhooks/simulate/simple/{paymentId}")
    public ResponseEntity<String> simulateSimpleWebhook(@PathVariable String paymentId) {
        WebhookRequestDto webhookRequest = new WebhookRequestDto(
            paymentId,
            PaymentStatus.SUCCESS,
            "TXN_" + paymentId,
            50000,
            "간편결제 성공",
            java.time.LocalDateTime.now(),
            "simple_signature_456"
        );
        
        String result = webhookService.handleWebhook("simple", webhookRequest);
        return ResponseEntity.ok("간편결제 웹훅 시뮬레이션 완료: " + result);
    }
    
    /**
     * 웹훅 시뮬레이션 - C사 가상계좌
     * @param paymentId 결제 ID
     * @return 시뮬레이션 결과
     */
    @PostMapping("/webhooks/simulate/virtual/{paymentId}")
    public ResponseEntity<String> simulateVirtualWebhook(@PathVariable String paymentId) {
        WebhookRequestDto webhookRequest = new WebhookRequestDto(
            paymentId,
            PaymentStatus.SUCCESS,
            "TXN_" + paymentId,
            50000,
            "가상계좌 결제 성공",
            java.time.LocalDateTime.now(),
            "virtual_signature_789"
        );
        
        String result = webhookService.handleWebhook("virtual", webhookRequest);
        return ResponseEntity.ok("가상계좌 웹훅 시뮬레이션 완료: " + result);
    }
} 