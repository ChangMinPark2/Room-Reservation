package com.example.demo.config;

import java.util.Map;

/**
 * 결제사별 설정 정보 맵
 * 각 결제사 타입에 따른 Mock 응답 데이터 설정을 관리
 */
public final class PaymentProviderConfigMap {
    
    // 결제사별 설정 정보를 담은 Map
    public static final Map<String, PaymentProviderConfig> PROVIDER_CONFIG = Map.of(
        "SIMPLE_PAYMENT", new PaymentProviderConfig(
            "SIMPLE_", 
            "간편결제 처리가 시작되었습니다.", 
            "http://localhost:8081/simple", 
            "토스페이", 
            "mock_simple_auth_key_2024"
        ),
        "CARD_PAYMENT", new PaymentProviderConfig(
            "CARD_", 
            "카드결제 처리가 시작되었습니다.", 
            "http://localhost:8081/card", 
            "KB카드", 
            "mock_card_merchant_id_kb"
        ),
        "VIRTUAL_ACCOUNT", new PaymentProviderConfig(
            "VIRTUAL_", 
            "가상계좌 발급이 시작되었습니다.", 
            "http://localhost:8081/virtual", 
            "신한은행", 
            "mock_virtual_bank_code_shinhan"
        )
    );
    
    // 유틸리티 클래스이므로 인스턴스화 방지
    private PaymentProviderConfigMap() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * 결제사 타입에 해당하는 설정을 조회
     * @param providerType 결제사 타입 (SIMPLE_PAYMENT, CARD_PAYMENT, VIRTUAL_ACCOUNT)
     * @return 결제사 설정 정보
     * @throws IllegalArgumentException 지원하지 않는 결제사 타입인 경우
     */
    public static PaymentProviderConfig getConfig(String providerType) {
        PaymentProviderConfig config = PROVIDER_CONFIG.get(providerType);
        if (config == null) {
            throw new IllegalArgumentException("지원하지 않는 결제사 타입: " + providerType);
        }
        return config;
    }
}
