package com.room.reservation.system.api.dto.meetingRoom;

import java.util.List;

public record MeetingRoomReadAllDto(
    List<MeetingRoomReadDto> meetingRooms
) {
}
