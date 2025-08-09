package com.example.demo.dto;

public record PaymentProviderRequestDto(
    String paymentId,          // 내부 결제 ID
    String paymentMethod,    // 결제 방법
    Integer amount,          // 금액
    String merchantId,       // 상점 ID
    String reservationId,    // 예약 ID
    String userName,         // 사용자 이름
    String phoneNumber,      // 사용자 전화번호
    String providerType      // 결제사 타입 (SIMPLE_PAYMENT, CARD_PAYMENT, VIRTUAL_ACCOUNT)
) {}
