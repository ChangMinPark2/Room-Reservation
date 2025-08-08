package com.room.reservation.system.api.persistence.repository;

import com.room.reservation.system.api.persistence.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * 특정 회의실의 현재 시간 이후 예약 조회
     */
    @Query("SELECT r FROM Reservation r WHERE r.meetingRoom.id = :meetingRoomId " +
            "AND r.startTime >= :currentDateTime " +
            "ORDER BY r.startTime")
    List<Reservation> findReservationsAfterCurrentTime(
            @Param("meetingRoomId") Long meetingRoomId,
            @Param("currentDateTime") LocalDateTime currentDateTime);

    /**
     * 사용자 이름과 폰번호로 모든 예약 조회
     */
    @Query("SELECT r FROM Reservation r WHERE r.user.name = :userName " +
            "AND r.user.phoneNumber = :phoneNumber " +
            "ORDER BY r.startTime ASC")
    List<Reservation> findAllReservationsByUser(
            @Param("userName") String userName,
            @Param("phoneNumber") String phoneNumber);

    /**
     * 사용자 이름, 폰번호, 예약 ID로 특정 예약 조회
     */
    @Query("SELECT r FROM Reservation r WHERE r.user.name = :userName " +
           "AND r.user.phoneNumber = :phoneNumber " +
           "AND r.id = :reservationId")
    Optional<Reservation> findReservationByUserAndId(
            @Param("userName") String userName,
            @Param("phoneNumber") String phoneNumber,
            @Param("reservationId") Long reservationId);
} 