package com.example.demo.service;

import com.example.demo.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SimplePaymentService {
    public PaymentPendingResponseDto processPayment(PaymentProviderRequestDto request) {

        final String externalPaymentId = "SIMPLE_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
       // final String transactionId = "TXN_" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        
        PaymentPendingResponseDto response = new PaymentPendingResponseDto(
            request.paymentId(),
            externalPaymentId,
            "간편결제 처리가 시작되었습니다.",
            "PENDING"
        );
        
        //원래 여기서 response를 바탕으로 결제 로직이 이루어짐
        //여기서는 무조건 결제가 성공된다고 가정한다면
        //그냥 웹훅 서비스를 호출하면 된다.
        //TODO: 웹훅 서비스 구현 예정
        
        return response;
    }
}