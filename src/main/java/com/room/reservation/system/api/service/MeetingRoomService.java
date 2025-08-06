package com.room.reservation.system.api.service;

import com.room.reservation.system.api.dto.meetingRoom.MeetingRoomReadAllDto;
import com.room.reservation.system.api.dto.meetingRoom.MeetingRoomReadDto;
import com.room.reservation.system.api.persistence.entity.MeetingRoom;
import com.room.reservation.system.api.persistence.repository.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;

    public MeetingRoomReadAllDto readAll() {
        final List<MeetingRoom> meetingRooms = meetingRoomRepository.findByIsActiveTrue();
        final List<MeetingRoomReadDto> meetingRoomDtos = meetingRooms.stream()
                .map(MeetingRoom::toMeetingRoomReadDto)
                .toList();

        return new MeetingRoomReadAllDto(meetingRoomDtos);
    }
} 