package com.room.reservation.system.api.dto.reservation;

import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ReservationReadDto(
        Long reservationId,
        String userName,
        LocalDateTime startDate,
        LocalDateTime endTime,
        Integer totalAmount,
        String reservationStatus
) {
} 