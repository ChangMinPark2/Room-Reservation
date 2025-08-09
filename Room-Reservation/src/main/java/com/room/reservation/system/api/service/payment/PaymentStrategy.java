package com.room.reservation.system.api.service.payment;

import com.room.reservation.system.api.dto.payment.request.PaymentRequestDto;
import com.room.reservation.system.api.dto.payment.response.PaymentResponseDto;
import com.room.reservation.system.api.dto.payment.response.PaymentPendingResponseDto;
import com.room.reservation.system.api.persistence.entity.PaymentStatus;
import com.room.reservation.system.api.persistence.entity.Reservation;

public interface PaymentStrategy {
    
    /**
     * 결제 처리
     * @param paymentId 내부 결제 ID
     * @param reservation 예약 정보
     * @param request 결제 요청 정보
     * @return 결제 대기 상태
     */
    PaymentPendingResponseDto pay(Long paymentId, Reservation reservation, PaymentRequestDto request);
    
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