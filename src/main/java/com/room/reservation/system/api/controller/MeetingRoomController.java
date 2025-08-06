package com.room.reservation.system.api.controller;

import com.room.reservation.system.api.dto.meetingRoom.MeetingRoomReadAllDto;
import com.room.reservation.system.api.dto.meetingRoom.MeetingRoomDetailDto;
import com.room.reservation.system.api.service.MeetingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting-rooms")
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    @GetMapping
    public ResponseEntity<MeetingRoomReadAllDto> readAll() {
        MeetingRoomReadAllDto meetingRooms = meetingRoomService.readAll();
        return ResponseEntity.ok(meetingRooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingRoomDetailDto> read(@PathVariable Long id) {
        MeetingRoomDetailDto meetingRoom = meetingRoomService.read(id);
        return ResponseEntity.ok(meetingRoom);
    }
}
