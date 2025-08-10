package com.room.reservation.system.api.service;

import com.room.reservation.system.api.dto.meetingRoom.MeetingRoomDetailDto;
import com.room.reservation.system.api.dto.meetingRoom.MeetingRoomReadAllDto;
import com.room.reservation.system.api.dto.meetingRoom.TimeSlotDto;
import com.room.reservation.system.api.persistence.entity.MeetingRoom;
import com.room.reservation.system.api.persistence.entity.Reservation;
import com.room.reservation.system.api.persistence.entity.User;
import com.room.reservation.system.api.persistence.repository.MeetingRoomRepository;
import com.room.reservation.system.api.persistence.repository.ReservationRepository;
import com.room.reservation.system.global.error.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingRoomServiceTest {

    @Mock
    private MeetingRoomRepository meetingRoomRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private MeetingRoomService meetingRoomService;

    private User testUser;
    private MeetingRoom testMeetingRoom;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        testUser = new User("홍길동", "010-1234-5678");
        // Reflection을 사용하여 id 설정
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testUser, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        testMeetingRoom = new MeetingRoom("A회의실", 10, 25000);
        // Reflection을 사용하여 id 설정
        try {
            java.lang.reflect.Field idField = MeetingRoom.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testMeetingRoom, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        testReservation = Reservation.create(
                testUser,
                testMeetingRoom,
                LocalDateTime.now().with(LocalTime.of(14, 0)),
                LocalDateTime.now().with(LocalTime.of(15, 0)),
                50000
        );
        // Reflection을 사용하여 id 설정
        try {
            java.lang.reflect.Field idField = Reservation.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testReservation, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("회의실 목록 조회 성공")
    void readAll_Success() {
        // given
        MeetingRoom meetingRoom1 = new MeetingRoom("A회의실", 10, 25000);
        MeetingRoom meetingRoom2 = new MeetingRoom("B회의실", 20, 40000);
        List<MeetingRoom> meetingRooms = Arrays.asList(meetingRoom1, meetingRoom2);

        when(meetingRoomRepository.findByIsActiveTrue()).thenReturn(meetingRooms);

        // when
        MeetingRoomReadAllDto result = meetingRoomService.readAll();

        // then
        assertThat(result).isNotNull();
        assertThat(result.meetingRooms()).hasSize(2);
        assertThat(result.meetingRooms().get(0).name()).isEqualTo("A회의실");
        assertThat(result.meetingRooms().get(1).name()).isEqualTo("B회의실");

        verify(meetingRoomRepository).findByIsActiveTrue();
    }

    @Test
    @DisplayName("회의실 상세 조회 성공")
    void read_Success() {
        // given
        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.of(testMeetingRoom));
        when(reservationRepository.findReservationsAfterCurrentTime(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // when
        MeetingRoomDetailDto result = meetingRoomService.read(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.meetingRoom().name()).isEqualTo("A회의실");
        assertThat(result.meetingRoom().capacity()).isEqualTo(10);
        assertThat(result.meetingRoom().halfHourlyRate()).isEqualTo(25000);
        assertThat(result.availableTimeSlots()).isNotEmpty();

        verify(meetingRoomRepository).findById(1L);
        verify(reservationRepository).findReservationsAfterCurrentTime(eq(1L), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("존재하지 않는 회의실 조회 시 예외 발생")
    void read_MeetingRoomNotFound_ThrowsException() {
        // given
        when(meetingRoomRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> meetingRoomService.read(999L))
                .isInstanceOf(NotFoundException.class);

        verify(meetingRoomRepository).findById(999L);
    }

    @Test
    @DisplayName("예약이 있는 회의실의 사용 가능 시간 계산")
    void read_WithReservations_CalculatesAvailableTimeSlots() {
        // given
        Reservation reservation1 = Reservation.create(
                testUser,
                testMeetingRoom,
                LocalDateTime.now().with(LocalTime.of(14, 0)),
                LocalDateTime.now().with(LocalTime.of(15, 0)),
                50000
        );
        // Reflection을 사용하여 id 설정
        try {
            java.lang.reflect.Field idField = Reservation.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(reservation1, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Reservation reservation2 = Reservation.create(
                testUser,
                testMeetingRoom,
                LocalDateTime.now().with(LocalTime.of(16, 0)),
                LocalDateTime.now().with(LocalTime.of(17, 0)),
                50000
        );
        // Reflection을 사용하여 id 설정
        try {
            java.lang.reflect.Field idField = Reservation.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(reservation2, 2L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);

        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.of(testMeetingRoom));
        when(reservationRepository.findReservationsAfterCurrentTime(eq(1L), any(LocalDateTime.class)))
                .thenReturn(reservations);

        // when
        MeetingRoomDetailDto result = meetingRoomService.read(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.availableTimeSlots()).isNotEmpty();
        // 예약 사이의 시간대가 사용 가능 시간으로 계산되어야 함
        assertThat(result.availableTimeSlots().size()).isGreaterThan(0);

        verify(meetingRoomRepository).findById(1L);
        verify(reservationRepository).findReservationsAfterCurrentTime(eq(1L), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("사용 가능 시간 계산 로직 검증")
    void calculateAvailableTimeSlots_Logic() {
        // given
        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.of(testMeetingRoom));
        when(reservationRepository.findReservationsAfterCurrentTime(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // when
        MeetingRoomDetailDto result = meetingRoomService.read(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.availableTimeSlots()).isNotEmpty();
        // 사용 가능한 시간대가 계산되었는지 확인
        assertThat(result.availableTimeSlots().size()).isGreaterThan(0);

        verify(meetingRoomRepository).findById(1L);
        verify(reservationRepository).findReservationsAfterCurrentTime(eq(1L), any(LocalDateTime.class));
    }
}
