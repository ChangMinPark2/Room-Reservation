package com.room.reservation.system.api.dto.payment.request;

import com.room.reservation.system.api.persistence.entity.PaymentProviderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 결제 요청 DTO
 */
@Schema(description = "결제 요청 정보")
public record PaymentRequestDto(
    @Schema(description = "결제사 타입", example = "SIMPLE_PAYMENT", required = true)
    @NotNull(message = "결제사 타입은 필수입니다.")
    PaymentProviderType providerType,
    
    @Schema(description = "결제 방법", example = "KAKAO_PAY", required = true)
    @NotNull(message = "결제 방법은 필수입니다.")
    String paymentMethod,
    
    @Schema(description = "결제 금액", example = "50000", required = true)
    @NotNull(message = "결제 금액은 필수입니다.")
    Integer amount,
    
    @Schema(description = "사용자 이름", example = "홍길동", required = true)
    String userName,
    
    @Schema(description = "전화번호", example = "010-1234-5678", required = true)
    String phoneNumber,
    
    @Schema(description = "신용카드 번호 (카드결제용)", example = "1234-5678-9012-3456", hidden = true)
    String cardNumber,
    
    @Schema(description = "간편결제 타입 (간편결제용)", example = "KAKAO_PAY", required = true)
    String simplePayType,
    
    @Schema(description = "계좌번호 (가상계좌용)", example = "123456789012", hidden = true)
    String accountNumber
) {} 