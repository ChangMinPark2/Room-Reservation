package com.room.reservation.system.api.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 상태 조회 요청 정보")
public record PaymentStatusRequestDto(
    @Schema(description = "사용자 이름", example = "홍길동", required = true)
    String userName,
    
    @Schema(description = "사용자 전화번호", example = "010-1234-5678", required = true)
    String phoneNumber
) {
}
