package com.room.reservation.system.api.controller;

import com.room.reservation.system.api.dto.reservation.ReservationCreateDto;
import com.room.reservation.system.api.dto.reservation.ReservationReadDto;
import com.room.reservation.system.api.dto.reservation.ReservationReadAllDto;
import com.room.reservation.system.api.dto.reservation.ReservationReadRequestDto;
import com.room.reservation.system.api.dto.reservation.ReservationDeleteDto;
import com.room.reservation.system.api.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody ReservationCreateDto dto) {
        reservationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("예약이 성공적으로 생성되었습니다.");
    }

    @GetMapping
    public ResponseEntity<ReservationReadAllDto> readAllReservations(@Valid @RequestBody ReservationReadRequestDto dto) {
        ReservationReadAllDto reservations = reservationService.readAllReservations(dto);
        return ResponseEntity.ok(reservations);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteReservation(@Valid @RequestBody ReservationDeleteDto dto) {
        reservationService.delete(dto);
        return ResponseEntity.ok("예약이 성공적으로 삭제되었습니다.");
    }
}
