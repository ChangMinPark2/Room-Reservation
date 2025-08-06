package com.room.reservation.system.api.persistence.entity;

public enum ReservationStatus {
    PENDING,    // 결제 대기
    CONFIRMED,  // 예약 확정
    CANCELLED,  // 예약 취소
    COMPLETED   // 예약 완료
} 