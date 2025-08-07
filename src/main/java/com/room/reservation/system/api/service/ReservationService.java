package com.room.reservation.system.api.service;

import com.room.reservation.system.api.dto.reservation.ReservationCreateDto;
import com.room.reservation.system.api.persistence.entity.MeetingRoom;
import com.room.reservation.system.api.persistence.entity.Payment;
import com.room.reservation.system.api.persistence.entity.Reservation;
import com.room.reservation.system.api.persistence.entity.User;
import com.room.reservation.system.api.persistence.repository.MeetingRoomRepository;
import com.room.reservation.system.api.persistence.repository.PaymentRepository;
import com.room.reservation.system.api.persistence.repository.ReservationRepository;
import com.room.reservation.system.api.persistence.repository.UserRepository;
import com.room.reservation.system.global.error.exception.BadRequestException;
import com.room.reservation.system.global.error.exception.NotFoundException;
import com.room.reservation.system.global.error.model.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final MeetingRoomRepository meetingRoomRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public void create(ReservationCreateDto dto) {
        final MeetingRoom meetingRoom = meetingRoomRepository.findById(dto.meetingRoomId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_MEETING_ROOM));
        final User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER));
        final LocalDateTime now = LocalDateTime.now();
        final List<Reservation> todayReservations = reservationRepository.findReservationsAfterCurrentTime(
                dto.meetingRoomId(),
                now
        );

        validateAfterTime(dto.startTime());
        validateBeforeTime(dto.startTime(), dto.endTime());
        validateTimeFormat(dto.startTime());
        validateTimeFormat(dto.endTime());
        validateAlreadyReservation(dto.startTime(), dto.endTime(), todayReservations);

        final Integer totalAmount = calculateTotalAmount(meetingRoom, dto.startTime(), dto.endTime());
        final LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        final LocalDateTime startDateTime = today.with(dto.startTime());
        final LocalDateTime endDateTime = today.with(dto.endTime());

        final Reservation reservation = Reservation.create(
            user, meetingRoom, startDateTime, endDateTime, totalAmount);
        final Payment payment = Payment.create(reservation, totalAmount);

        reservationRepository.save(reservation);
        paymentRepository.save(payment);
    }

    private void validateAfterTime(LocalTime startTime) {
        final LocalTime now = LocalTime.now();
        if (startTime.isBefore(now)) {
            throw new BadRequestException(ErrorCode.PAST_TIME_RESERVATION);
        }
    }

    private void validateBeforeTime(LocalTime startTime, LocalTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new BadRequestException(ErrorCode.INVALID_TIME_RANGE);
        }
    }

    private void validateTimeFormat(LocalTime time) {
        int minute = time.getMinute();
        if (minute != 0 && minute != 30) {
            throw new BadRequestException(ErrorCode.INVALID_TIME_FORMAT);
        }
    }

    private void validateAlreadyReservation(LocalTime startTime, LocalTime endTime, List<Reservation> existingReservations) {
        for (Reservation reservation : existingReservations) {
            LocalTime reservationStart = reservation.getStartTime().toLocalTime();
            LocalTime reservationEnd = reservation.getEndTime().toLocalTime();

            if (isTimeOverlap(startTime, endTime, reservationStart, reservationEnd)) {
                throw new BadRequestException(ErrorCode.ALREADY_RESERVED_TIME);
            }
        }
    }

    private boolean isTimeOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private Integer calculateTotalAmount(MeetingRoom meetingRoom, LocalTime startTime, LocalTime endTime) {
        final long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        final long halfHourUnits = (minutes + 29) / 30;

        return (int) (halfHourUnits * meetingRoom.getHalfHourlyRate());
    }
}
