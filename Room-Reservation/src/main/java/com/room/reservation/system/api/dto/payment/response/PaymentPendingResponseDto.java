package com.room.reservation.system.api.dto.payment.response;

/**
 * 결제 대기 상태 응답 DTO
 * 결제 요청 시 즉시 반환되는 응답
 */
public record PaymentPendingResponseDto(
    String paymentId,           // 내부 결제 조회용 ID (필수)
    String externalPaymentId,   // 외부 결제사 ID (Mock 서버에서 제공)
    String message,             // "결제가 진행중입니다"
    String status               // "PENDING"
) {}
