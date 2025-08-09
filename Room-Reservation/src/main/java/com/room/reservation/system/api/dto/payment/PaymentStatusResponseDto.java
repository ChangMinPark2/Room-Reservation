package com.room.reservation.system.api.dto.payment;

public record PaymentStatusResponseDto(
    Long reservationId,      // 예약 번호
    Long paymentId,          // 결제 번호 (내부 ID)
    String externalPaymentId, // 외부 결제 번호 (결제사 ID)
    String paymentStatus,    // 결제 상태 (PENDING, SUCCESS, FAILED, CANCELLED)
    String providerName,     // 결제사 이름 (예: "토스페이", "KB카드")
    Integer amount,          // 결제 금액
    String message           // 결제 완료 메시지
) {
}
