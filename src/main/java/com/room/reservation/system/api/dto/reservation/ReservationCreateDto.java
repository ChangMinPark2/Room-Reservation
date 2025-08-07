package com.room.reservation.system.api.dto.reservation;

import java.time.LocalTime;

public record ReservationCreateDto(
    Long meetingRoomId,
    LocalTime startTime,
    LocalTime endTime,
    Long userId,
    String name,
    String phoneNumber
) {
}
