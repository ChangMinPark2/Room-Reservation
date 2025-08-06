package com.room.reservation.system.api.dto.meetingRoom;

import java.util.List;

public record MeetingRoomDetailDto(
        MeetingRoomReadDto meetingRoom,
        List<TimeSlotDto> availableTimeSlots
) {
} 