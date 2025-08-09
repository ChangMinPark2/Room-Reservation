package com.example.demo.config;

/**
 * 결제사별 설정 정보를 담는 설정 클래스
 * Mock 서버에서 각 결제사별 응답 데이터를 생성하기 위한 메타데이터
 */
public record PaymentProviderConfig(
    String idPrefix,        // 외부 결제 ID 생성용 접두사 (예: "SIMPLE_", "CARD_")
    String message,         // 결제 처리 시작 메시지
    String apiEndpoint,     // 결제사 API 엔드포인트 (Mock)
    String providerName,    // 결제사명 (예: "토스페이", "KB카드")
    String authInfo         // 인증 정보 (Mock용 키)
) {
}
