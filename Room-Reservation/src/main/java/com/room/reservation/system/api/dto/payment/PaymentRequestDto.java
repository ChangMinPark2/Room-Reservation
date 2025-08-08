package com.room.reservation.system.api.dto.payment;

import com.room.reservation.system.api.persistence.entity.PaymentProviderType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 결제 요청 DTO
 */
public record PaymentRequestDto(
    @NotNull(message = "결제사 타입은 필수입니다.")
    PaymentProviderType providerType,
    
    @NotNull(message = "결제 방법은 필수입니다.")
    String paymentMethod,
    
    @NotNull(message = "결제 금액은 필수입니다.")
    Integer amount,
    String userName,
    String phoneNumber,
    
    // 신용카드 결제용
    String cardNumber,
    
    // 간편결제용
    String simplePayType,
    
    // 가상계좌 결제용
    String accountNumber
) {} 