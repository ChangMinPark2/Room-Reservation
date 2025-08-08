package com.room.reservation.system.api.persistence.entity;

import com.room.reservation.system.api.dto.reservation.ReservationReadDto;
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

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Builder
    private Reservation(Long id,
                        User user,
                        MeetingRoom meetingRoom,
                        LocalDateTime startTime,
                        LocalDateTime endTime,
                        Integer totalAmount,
                        ReservationStatus status,
                        Payment payment
    ) {
        this.id = id;
        this.user = user;
        this.meetingRoom = meetingRoom;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalAmount = totalAmount;
        this.status = status;
        this.payment = payment;
    }

    public static Reservation create(
            User user,
            MeetingRoom meetingRoom,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer totalAmount,
            Payment payment
    ) {
        return Reservation.builder()
                .user(user)
                .meetingRoom(meetingRoom)
                .startTime(startTime)
                .endTime(endTime)
                .totalAmount(totalAmount)
                .status(ReservationStatus.PENDING)
                .payment(payment)
                .build();
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public ReservationReadDto toReadDto() {
        return ReservationReadDto.builder()
                .reservationId(this.id)
                .userName(this.user.getName())
                .startDate(this.startTime)
                .endTime(this.endTime)
                .totalAmount(this.totalAmount)
                .reservationStatus(this.status.name())
                .build();
    }
}