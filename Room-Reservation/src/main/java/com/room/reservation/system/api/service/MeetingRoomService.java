package com.room.reservation.system.api.service;

import com.room.reservation.system.api.dto.meetingRoom.MeetingRoomReadAllDto;
import com.room.reservation.system.api.dto.meetingRoom.MeetingRoomReadDto;
import com.room.reservation.system.api.dto.meetingRoom.MeetingRoomDetailDto;
import com.room.reservation.system.api.dto.meetingRoom.TimeSlotDto;
import com.room.reservation.system.api.persistence.entity.MeetingRoom;
import com.room.reservation.system.api.persistence.entity.Reservation;
import com.room.reservation.system.api.persistence.repository.MeetingRoomRepository;
import com.room.reservation.system.api.persistence.repository.ReservationRepository;
import com.room.reservation.system.global.error.exception.NotFoundException;
import com.room.reservation.system.global.error.model.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public MeetingRoomReadAllDto readAll() {
        final List<MeetingRoom> meetingRooms = meetingRoomRepository.findByIsActiveTrue();

        return new MeetingRoomReadAllDto(meetingRooms.stream()
                .map(MeetingRoom::toMeetingRoomReadDto)
                .toList()
        );
    }

    @Transactional(readOnly = true)
    public MeetingRoomDetailDto read(Long id) {
        final MeetingRoom meetingRoom = meetingRoomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_MEETING_ROOM));
        final List<TimeSlotDto> availableTimeSlots = calculateAvailableTimeSlots(id);
        final MeetingRoomReadDto meetingRoomDto = meetingRoom.toMeetingRoomReadDto();

        return new MeetingRoomDetailDto(meetingRoomDto, availableTimeSlots);
    }

    private List<TimeSlotDto> calculateAvailableTimeSlots(Long meetingRoomId) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalTime currentTime = now.toLocalTime();
        final LocalTime adjustedTime = adjustToNextTimeSlot(currentTime);
        final LocalTime endTime = LocalTime.of(23, 30);
        final List<Reservation> reservations = reservationRepository.findReservationsAfterCurrentTime(
                meetingRoomId, 
                now
        ).stream()
         .sorted(Comparator.comparing(Reservation::getStartTime))
         .toList();
        final List<TimeSlotDto> availableSlots = new ArrayList<>();

        addAvailableTime(adjustedTime, reservations, availableSlots, endTime);

        return availableSlots;
    }

    private static void addAvailableTime(LocalTime adjustedTime, List<Reservation> reservations, List<TimeSlotDto> availableSlots, LocalTime endTime) {
        LocalTime currentSlot = adjustedTime;

        for (Reservation reservation : reservations) {
            final LocalTime reservationStart = reservation.getStartTime().toLocalTime();

            if (adjustedTime.isBefore(reservationStart)) {
                availableSlots.add(new TimeSlotDto(currentSlot, reservationStart));
            }

            currentSlot = reservation.getEndTime().toLocalTime();
        }

        if (currentSlot.isBefore(endTime)) {
            availableSlots.add(new TimeSlotDto(currentSlot, endTime));
        }
    }

    private LocalTime adjustToNextTimeSlot(LocalTime currentTime) {
        final int currentMinute = currentTime.getMinute();
        int adjustedMinute;
        int adjustedHour = currentTime.getHour();
        
        if (currentMinute <= 30) {
            adjustedMinute = 30;
        } else {
            adjustedMinute = 0;
            adjustedHour = currentTime.getHour() + 1;
        }
        
        return LocalTime.of(adjustedHour, adjustedMinute);
    }
} 