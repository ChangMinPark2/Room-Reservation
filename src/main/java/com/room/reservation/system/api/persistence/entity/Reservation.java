package com.room.reservation.system.api.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "tbl_reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Reservation {
        
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_room_id", nullable = false)
    private MeetingRoom meetingRoom;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    @Column(nullable = false)
    private LocalDateTime endTime;
    
    @Column(nullable = false)
    private Integer totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Reservation(Long id,
                       User user,
                       MeetingRoom meetingRoom,
                       LocalDateTime startTime,
                       LocalDateTime endTime,
                       Integer totalAmount,
                       ReservationStatus status
    ) {
        this.id = id;
        this.user = user;
        this.meetingRoom = meetingRoom;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public static Reservation create(
            User user,
            MeetingRoom meetingRoom,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer totalAmount
    ) {
        return Reservation.builder()
            .user(user)
            .meetingRoom(meetingRoom)
            .startTime(startTime)
            .endTime(endTime)
            .totalAmount(totalAmount)
            .status(ReservationStatus.PENDING)
            .build();
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }
}