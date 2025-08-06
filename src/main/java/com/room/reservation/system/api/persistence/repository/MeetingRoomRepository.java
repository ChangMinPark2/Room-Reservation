package com.room.reservation.system.api.persistence.repository;

import com.room.reservation.system.api.persistence.entity.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {
    List<MeetingRoom> findByIsActiveTrue();
} 