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
public class CardPaymentStrategy implements PaymentStrategy {
    
    private final RestTemplate restTemplate;
    private static final String CARD_API_BASE_URL = "http://localhost:8081";
    
    public CardPaymentStrategy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
        @Override
    public PaymentPendingResponseDto pay(Long paymentId, Reservation reservation, PaymentRequestDto request) {
        final PaymentProviderRequestDto providerRequest = new PaymentProviderRequestDto(
            paymentId.toString(),
            request.cardNumber(),
            request.amount(),
            "A_COMPANY",
            reservation.getId().toString(),
            request.userName(),
            request.phoneNumber(),
            "CARD_PAYMENT"
        );

        final PaymentPendingResponseDto mockResponse = restTemplate.postForObject(
            CARD_API_BASE_URL + "/payment",
            providerRequest,
            PaymentPendingResponseDto.class
        );

        return new PaymentPendingResponseDto(
            paymentId.toString(),
            mockResponse.externalPaymentId(),
            "A사 신용카드 결제가 진행중입니다.",
            "PENDING"
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
        return "CARD_PAYMENT";
    }
} 