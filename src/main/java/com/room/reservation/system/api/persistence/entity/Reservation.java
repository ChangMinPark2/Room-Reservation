package com.room.reservation.system.api.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Getter
@Table(name = "tbl_reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Payment payment;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public Reservation(User user, MeetingRoom meetingRoom, LocalDateTime startTime, 
                      LocalDateTime endTime, Integer totalAmount) {
        this.user = user;
        this.meetingRoom = meetingRoom;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalAmount = totalAmount;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
} 