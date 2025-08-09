package com.example.demo.service;

import com.example.demo.config.PaymentProviderConfig;
import com.example.demo.config.PaymentProviderConfigMap;
import com.example.demo.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final WebhookService webhookService;
    
    public PaymentPendingResponseDto processPayment(PaymentProviderRequestDto request) {
        final PaymentProviderConfig config = PaymentProviderConfigMap.getConfig(request.providerType());
        final String externalPaymentId = config.idPrefix() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        final String message = config.message();
        final String apiEndpoint = config.apiEndpoint();
        final String providerName = config.providerName();
        final String authInfo = config.authInfo();
        final String transactionId = "TXN_" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        final PaymentPendingResponseDto response = new PaymentPendingResponseDto(
            request.paymentId(),
            externalPaymentId,
            message,
            "PENDING"
        );

        log.info("외부결제ID 생성: {}, 거래ID: {}", externalPaymentId, transactionId);

        /*
         * 실제 결제사 환경 시뮬레이션을 위한 비동기 처리
         * 
         * [실제 환경]
         * - 결제 요청 → 즉시 PENDING 응답 → 메인 서버 트랜잭션 커밋 완료
         * - 수 초~수십 초 후 → 실제 카드사 승인 완료 → 웹훅 전송
         * 
         * [Mock 환경의 문제점]
         * - 결제 요청과 동시에 웹훅 전송 → 메인 서버 트랜잭션이 아직 커밋되지 않음
         * - Payment 엔티티 조회 실패 발생
         * 
         * [해결책]
         * - 1초 지연으로 실제 결제사의 처리 시간을 시뮬레이션
         * - 메인 서버 트랜잭션 커밋 후 웹훅 수신 보장
         * - 실제 비동기 결제 플로우와 동일한 환경 재현
         */
        
        new Thread(() -> {
            try {
                Thread.sleep(1000); // 1초 대기
                webhookService.sendPaymentWebhook(
                    externalPaymentId, 
                    "SUCCESS", 
                    request.amount(), 
                    transactionId, 
                    request.providerType(),
                    providerName,
                    apiEndpoint,
                    authInfo,
                    request.paymentId()
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("웹훅 전송 지연 중 인터럽트 발생", e);
            }
        }).start();

        log.info("=== Mock 서버 결제 처리 완료 ===");
        return response;
    }
}