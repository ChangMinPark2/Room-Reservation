package com.room.reservation.system.api.dto.payment;

import com.room.reservation.system.api.persistence.entity.PaymentStatus;

import java.time.LocalDateTime;

/**
 * 웹훅 요청 DTO
 */
public record WebhookRequestDto(
    String paymentId,           // 결제 ID
    PaymentStatus status,       // 결제 상태
    String transactionId,       // 거래 ID
    Integer amount,             // 결제 금액
    String message,             // 메시지
    LocalDateTime processedAt,  // 처리 시간
    String signature            // 서명 (검증용)
) {} 