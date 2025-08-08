package com.room.reservation.system.api.dto.payment;

import com.room.reservation.system.api.persistence.entity.PaymentProviderType;
import com.room.reservation.system.api.persistence.entity.PaymentStatus;

import java.time.LocalDateTime;

/**
 * 결제 응답 DTO
 */
public record PaymentResponseDto(
    String externalPaymentId,        // 외부 결제 ID
    PaymentStatus status,            // 결제 상태
    Integer amount,                  // 결제 금액
    PaymentProviderType providerType, // 결제사 타입
    String providerName,             // 결제사 명
    String apiEndpoint,              // API 엔드포인트
    String authInfo,                 // 인증정보
    LocalDateTime processedAt,       // 처리 시간
    String failureReason             // 실패 사유 (선택적)
) {}