package com.room.reservation.system.api.persistence.repository;

import com.room.reservation.system.api.persistence.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
} 