package com.room.reservation.system.api.dto;

import java.util.List;

public record MeetingRoomReadAllDto(
    List<MeetingRoomReadDto> meetingRooms
) {
}
