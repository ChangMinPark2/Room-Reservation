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
public class VirtualAccountPaymentStrategy implements PaymentStrategy {
    
    private final RestTemplate restTemplate;
    private static final String VIRTUAL_API_BASE_URL = "http://localhost:8081";
    
    public VirtualAccountPaymentStrategy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public PaymentPendingResponseDto pay(Long paymentId, Reservation reservation, PaymentRequestDto request) {
        final PaymentProviderRequestDto providerRequest = new PaymentProviderRequestDto(
            paymentId.toString(),
            request.accountNumber(),
            request.amount(),
            "C_COMPANY",
            reservation.getId().toString(),
            request.userName(),
            request.phoneNumber()
        );

        final PaymentPendingResponseDto mockResponse = restTemplate.postForObject(
            VIRTUAL_API_BASE_URL + "/payment",
            providerRequest,
            PaymentPendingResponseDto.class
        );

        return new PaymentPendingResponseDto(
            paymentId.toString(),
            mockResponse.externalPaymentId(),
            "C사 가상계좌 결제가 진행중입니다.",
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
        return "VIRTUAL_ACCOUNT";
    }
} 