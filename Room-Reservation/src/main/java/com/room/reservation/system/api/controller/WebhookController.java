package com.room.reservation.system.api.controller;

import com.room.reservation.system.api.dto.payment.WebhookPaymentDto;
import com.room.reservation.system.api.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 웹훅 수신 컨트롤러
 * 외부 결제사에서 보내는 웹훅을 처리
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/webhooks")
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/payments/{provider}")
    public ResponseEntity<String> receivePaymentWebhook(
            @PathVariable("provider") String provider,
            @RequestBody WebhookPaymentDto request
    ) {
        log.info("=== 메인 서버 웹훅 수신 ===");
        log.info("웹훅 수신 - 결제사: {}", provider);

        webhookService.processPaymentWebhook(provider, request);

        log.info("웹훅 처리 완료 - 결제사: {}", provider);
        log.info("=== 메인 서버 웹훅 처리 완료 ===");
        return ResponseEntity.ok("OK");
    }
}
