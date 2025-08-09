package com.example.demo.service;

import com.example.demo.dto.WebhookPaymentDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Mock 서버에서 메인 서버로 웹훅을 전송하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final RestTemplate restTemplate;
    private static final String MAIN_SERVER_WEBHOOK_URL = "http://localhost:8080/webhooks/payments";

    public void sendPaymentWebhook(
            String externalPaymentId,
            String status, Integer amount,
            String transactionId,
            String providerType,
            String providerName,
            String apiEndpoint,
            String authInfo,
            String paymentId
    ) {
        try {
            WebhookPaymentDto webhookData = new WebhookPaymentDto(
                    externalPaymentId,
                    status,  // SUCCESS 또는 FAILED
                    amount,
                    transactionId,
                    getStatusMessage(status),
                    providerName,
                    apiEndpoint,
                    authInfo,
                    providerType,
                    paymentId
            );

            // 메인 서버로 웹훅 전송 (PathVariable로 provider 타입 전달)
            final String webhookUrl = MAIN_SERVER_WEBHOOK_URL + "/" + providerType;

            log.info("웹훅 전송 시작 - URL: {}, 외부결제ID: {}, 상태: {}, 결제사: {}",
                    webhookUrl, externalPaymentId, status, providerName);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    webhookUrl,
                    webhookData,
                    String.class
            );

            log.info("웹훅 전송 완료 - 응답코드: {}, 외부결제ID: {}",
                    response.getStatusCode(), externalPaymentId);

        } catch (Exception e) {
            log.error("웹훅 전송 실패 - 외부결제ID: {}, 오류: {}",
                    externalPaymentId, e.getMessage(), e);
        }
    }

    private String getStatusMessage(String status) {
        return switch (status) {
            case "SUCCESS" -> "결제가 성공적으로 완료되었습니다.";
            case "FAILED" -> "결제 처리 중 오류가 발생했습니다.";
            default -> "결제 상태가 업데이트되었습니다.";
        };
    }
}
