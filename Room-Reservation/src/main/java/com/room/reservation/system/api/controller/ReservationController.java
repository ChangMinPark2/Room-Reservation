package com.room.reservation.system.api.controller;

import com.room.reservation.system.api.dto.reservation.ReservationCreateDto;
import com.room.reservation.system.api.dto.reservation.ReservationReadDto;
import com.room.reservation.system.api.dto.reservation.ReservationReadAllDto;
import com.room.reservation.system.api.dto.reservation.ReservationReadRequestDto;
import com.room.reservation.system.api.dto.reservation.ReservationDeleteDto;
import com.room.reservation.system.api.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Tag(name = "예약 관리", description = "회의실 예약 생성, 조회, 삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(
        summary = "예약 생성",
        description = "새로운 회의실 예약을 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "예약 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "409", description = "예약 시간 중복")
    })
    @PostMapping
    public ResponseEntity<String> create(
        @Parameter(description = "예약 생성 정보", required = true)
        @Valid @RequestBody ReservationCreateDto dto
    ) {
        reservationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("예약이 성공적으로 생성되었습니다.");
    }

    @Operation(
        summary = "예약 목록 조회",
        description = "사용자의 예약 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "예약 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping("/search")
    public ResponseEntity<ReservationReadAllDto> readAllReservations(
        @Parameter(description = "예약 조회 조건", required = true)
        @Valid @RequestBody ReservationReadRequestDto dto
    ) {
        ReservationReadAllDto reservations = reservationService.readAllReservations(dto);
        return ResponseEntity.ok(reservations);
    }

    @Operation(
        summary = "예약 삭제",
        description = "기존 예약을 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "예약 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음")
    })
    @DeleteMapping
    public ResponseEntity<String> deleteReservation(
        @Parameter(description = "예약 삭제 정보", required = true)
        @Valid @RequestBody ReservationDeleteDto dto
    ) {
        reservationService.delete(dto);
        return ResponseEntity.ok("예약이 성공적으로 삭제되었습니다.");
    }
}
