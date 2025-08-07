package com.room.reservation.system.api.controller;

import com.room.reservation.system.api.dto.reservation.ReservationCreateDto;
import com.room.reservation.system.api.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody ReservationCreateDto dto) {
        reservationService.create(dto);
        return ResponseEntity.ok("OK");
    }
}
