package com.example.demo.dto;

/**
 * Mock 서버에서 메인 서버로 보내는 웹훅 요청 DTO
 * 결제 완료/실패를 알리는 용도
 */
public record WebhookPaymentDto(
    String externalPaymentId,    // 외부 결제사 ID (Mock에서 생성한 ID)
    String status,               // SUCCESS, FAILED, CANCELLED
    Integer amount,              // 결제 금액
    String transactionId,        // 거래 ID
    String message,              // 결과 메시지
    String providerName,         // "토스페이", "KB카드", "신한은행"
    String apiEndpoint,          // "http://localhost:8081/simple"
    String authInfo,            // "mock_simple_auth_key_2024"
    String providerType,        // "SIMPLE_PAYMENT"
    String paymentId            // 내부 결제 ID
) {}
