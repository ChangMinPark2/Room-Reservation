package com.room.reservation.system.api.service.payment;

import com.room.reservation.system.api.dto.payment.request.PaymentRequestDto;
import com.room.reservation.system.api.dto.payment.response.PaymentResponseDto;
import com.room.reservation.system.api.dto.payment.response.PaymentPendingResponseDto;
import com.room.reservation.system.api.dto.payment.request.PaymentProviderRequestDto;
import com.room.reservation.system.api.persistence.entity.PaymentStatus;
import com.room.reservation.system.api.persistence.entity.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class SimplePaymentStrategy implements PaymentStrategy {
    
    private final RestTemplate restTemplate;
    private static final String SIMPLE_API_BASE_URL = "http://localhost:8081";
    
    public SimplePaymentStrategy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public PaymentPendingResponseDto pay(Long paymentId, Reservation reservation, PaymentRequestDto request) {
        final PaymentProviderRequestDto providerRequest = new PaymentProviderRequestDto(
            paymentId.toString(),                      // 내부 결제 ID
            request.simplePayType(),        // 결제 방법
            request.amount(),               // 금액
            "B_COMPANY",                    // 상점 ID
            reservation.getId().toString(), // 예약 ID
            request.userName(),             // 사용자 이름
            request.phoneNumber(),          // 사용자 전화번호
            "SIMPLE_PAYMENT"                // 결제사 타입
        );

        final PaymentPendingResponseDto mockResponse = restTemplate.postForObject(
            SIMPLE_API_BASE_URL + "/payment",
            providerRequest,
            PaymentPendingResponseDto.class
        );

        return new PaymentPendingResponseDto(
            paymentId.toString(),                // paymentId (내부 결제 ID)
            mockResponse.externalPaymentId(),    // externalPaymentId (Mock 서버에서 받은 ID)
            "B사 간편결제가 진행중입니다.",      // message
            "PENDING"                            // status
        );
    }

    @Override
    public PaymentStatus checkPaymentStatus(String paymentId) {
        return null;
    }

    @Override
    public PaymentResponseDto cancelPayment(String paymentId) {
        return null;
    }

    @Override
    public String getPaymentType() {
        return "SIMPLE_PAYMENT";
    }
} 