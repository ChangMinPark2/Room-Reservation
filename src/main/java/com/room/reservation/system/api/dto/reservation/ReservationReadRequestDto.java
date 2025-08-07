package com.room.reservation.system.api.dto.reservation;

public record ReservationReadRequestDto(
        String userName,
        String phoneNumber
) {
} 