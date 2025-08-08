package com.room.reservation.system.api.service.payment;

import com.room.reservation.system.api.dto.payment.PaymentRequestDto;
import com.room.reservation.system.api.dto.payment.PaymentResponseDto;
import com.room.reservation.system.api.persistence.entity.PaymentStatus;
import com.room.reservation.system.api.persistence.entity.Reservation;

/**
 * 결제 전략 인터페이스
 * F-Lab 방식: Strategy Pattern을 사용한 결제 방식 추상화
 */
public interface PaymentStrategy {
    
    /**
     * 결제 처리
     * @param reservation 예약 정보
     * @param request 결제 요청 정보
     * @return 결제 결과
     */
    PaymentResponseDto pay(Reservation reservation, PaymentRequestDto request);
    
    /**
     * 결제 상태 조회
     * @param paymentId 결제 ID
     * @return 결제 상태
     */
    PaymentStatus checkPaymentStatus(String paymentId);
    
    /**
     * 결제 취소
     * @param paymentId 결제 ID
     * @return 취소 결과
     */
    PaymentResponseDto cancelPayment(String paymentId);
    
    /**
     * 해당 결제사 타입 반환
     * @return 결제사 타입
     */
    String getPaymentType();
} 