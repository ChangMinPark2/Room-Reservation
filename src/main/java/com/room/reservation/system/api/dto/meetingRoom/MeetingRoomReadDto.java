package com.room.reservation.system.api.dto.meetingRoom;

import lombok.Builder;

@Builder
public record MeetingRoomReadDto(
        Long id,
        String name,
        Integer capacity,
        Integer halfHourlyRate,
        Boolean isActive
) {
} 