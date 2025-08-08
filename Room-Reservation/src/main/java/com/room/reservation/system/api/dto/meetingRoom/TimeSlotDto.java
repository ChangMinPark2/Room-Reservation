package com.room.reservation.system.api.dto.meetingRoom;

import java.time.LocalTime;

public record TimeSlotDto(
        LocalTime startTime,
        LocalTime endTime
) {
} 