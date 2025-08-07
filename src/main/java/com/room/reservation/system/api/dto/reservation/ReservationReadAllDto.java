package com.room.reservation.system.api.dto.reservation;

import lombok.Builder;
import java.util.List;

@Builder
public record ReservationReadAllDto(
        List<ReservationReadDto> reservations
) {
} 