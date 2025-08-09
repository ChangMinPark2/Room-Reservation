package com.room.reservation.system.api.dto.payment;

public record PaymentStatusRequestDto(
    String userName,         // 사용자 이름
    String phoneNumber      // 사용자 전화번호
) {
}
