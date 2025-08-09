package com.room.reservation.system.api.dto.payment.request;

/**
 * 결제사 결제 요청 DTO
 * 모든 결제사에서 공통으로 사용하는 요청 데이터
 */
public record PaymentProviderRequestDto(
    String paymentId,          // 내부 결제 ID
    String paymentMethod,    // 결제 방법 (simplePayType, cardNumber, accountNumber 등)
    Integer amount,          // 금액
    String merchantId,       // 상점 ID ("A_COMPANY", "B_COMPANY", "C_COMPANY")
    String reservationId,    // 예약 ID
    String userName,         // 사용자 이름
    String phoneNumber       // 사용자 전화번호
) {}

