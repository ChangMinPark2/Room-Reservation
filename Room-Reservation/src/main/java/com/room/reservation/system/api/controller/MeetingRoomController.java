package com.room.reservation.system.api.controller;

import com.room.reservation.system.api.dto.meetingRoom.MeetingRoomReadAllDto;
import com.room.reservation.system.api.dto.meetingRoom.MeetingRoomDetailDto;
import com.room.reservation.system.api.service.MeetingRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회의실 관리", description = "회의실 정보 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting-rooms")
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    @Operation(
        summary = "회의실 목록 조회",
        description = "사용 가능한 모든 회의실 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회의실 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<MeetingRoomReadAllDto> readAll() {
        MeetingRoomReadAllDto meetingRooms = meetingRoomService.readAll();
        return ResponseEntity.ok(meetingRooms);
    }

    @Operation(
        summary = "회의실 상세 조회",
        description = "특정 회의실의 상세 정보와 예약 가능 시간을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회의실 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "회의실을 찾을 수 없음")
    })
                @GetMapping("/{id}")
            public ResponseEntity<MeetingRoomDetailDto> read(
                @Parameter(description = "회의실 ID", required = true, example = "1")
                @PathVariable("id") Long id
    ) {
        MeetingRoomDetailDto meetingRoom = meetingRoomService.read(id);
        return ResponseEntity.ok(meetingRoom);
    }
}
