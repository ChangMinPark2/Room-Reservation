package com.room.reservation.system.api.service.payment;

import com.room.reservation.system.api.dto.payment.PaymentRequestDto;
import com.room.reservation.system.api.dto.payment.PaymentResponseDto;
import com.room.reservation.system.api.persistence.entity.PaymentProviderType;
import com.room.reservation.system.api.persistence.entity.PaymentStatus;
import com.room.reservation.system.api.persistence.entity.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * B사 간편결제 전략
 * F-Lab 방식: Strategy Pattern 구현체
 */
@Slf4j
@Component
public class SimplePaymentStrategy implements PaymentStrategy {
    
    private final RestTemplate restTemplate;
    private static final String SIMPLE_API_BASE_URL = "https://api.simple-payment.com";
    
    public SimplePaymentStrategy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public PaymentResponseDto pay(Reservation reservation, PaymentRequestDto request) {
        log.info("B사 간편결제 시작 - 예약ID: {}, 금액: {}", 
                reservation.getId(), request.amount());
        
        try {
            // 실제 결제사 API 요청 데이터 생성
            SimplePaymentRequest simpleRequest = new SimplePaymentRequest(
                request.simplePayType(),
                request.amount(),
                "B_COMPANY",
                reservation.getId().toString()
            );
        
            SimplePaymentResponse response = restTemplate.postForObject(
                SIMPLE_API_BASE_URL + "/payment",
                simpleRequest,
                SimplePaymentResponse.class
            );
            
            if (response == null) {
                throw new RuntimeException("결제사 응답이 null입니다.");
            }
            
            // 응답을 DTO로 변환
            PaymentResponseDto result = new PaymentResponseDto(
                response.paymentId(),                    // externalPaymentId
                PaymentStatus.valueOf(response.status()), // status
                request.amount(),                        // amount
                PaymentProviderType.SIMPLE_PAYMENT,      // providerType
                "B사",                                   // providerName
                SIMPLE_API_BASE_URL,                    // apiEndpoint
                "{\"apiKey\":\"simple_key_456\"}",      // authInfo
                LocalDateTime.now(),                     // processedAt
                response.failureReason()                 // failureReason
            );
            
            log.info("B사 간편결제 완료 - 예약ID: {}, 상태: {}", 
                    reservation.getId(), result.status());
            
            return result;
            
        } catch (Exception e) {
            log.error("B사 간편결제 실패 - 예약ID: {}, 오류: {}", 
                    reservation.getId(), e.getMessage());
            
            return new PaymentResponseDto(
                generateExternalPaymentId(),
                PaymentStatus.FAILED,
                request.amount(),
                PaymentProviderType.SIMPLE_PAYMENT,
                "B사",
                SIMPLE_API_BASE_URL,
                "{\"apiKey\":\"simple_key_456\"}",
                LocalDateTime.now(),
                e.getMessage()
            );
        }
    }
    
    @Override
    public PaymentStatus checkPaymentStatus(String paymentId) {
        log.info("B사 결제 상태 조회 - 외부결제ID: {}", paymentId);
        
        try {
            SimplePaymentStatusResponse response = restTemplate.getForObject(
                SIMPLE_API_BASE_URL + "/status/" + paymentId,
                SimplePaymentStatusResponse.class
            );
            
            if (response == null) {
                return PaymentStatus.FAILED;
            }
            
            return PaymentStatus.valueOf(response.status());
            
        } catch (Exception e) {
            log.error("B사 결제 상태 조회 실패 - 외부결제ID: {}, 오류: {}", 
                    paymentId, e.getMessage());
            return PaymentStatus.FAILED;
        }
    }
    
    @Override
    public PaymentResponseDto cancelPayment(String paymentId) {
        log.info("B사 결제 취소 시작 - 외부결제ID: {}", paymentId);
        
        try {
            SimplePaymentCancelResponse response = restTemplate.postForObject(
                SIMPLE_API_BASE_URL + "/cancel",
                new SimplePaymentCancelRequest(paymentId),
                SimplePaymentCancelResponse.class
            );
            
            if (response == null) {
                throw new RuntimeException("취소 응답이 null입니다.");
            }
            
            return new PaymentResponseDto(
                paymentId,
                PaymentStatus.CANCELLED,
                null, // amount is not available for cancellation
                PaymentProviderType.SIMPLE_PAYMENT,
                "B사",
                SIMPLE_API_BASE_URL,
                "{\"apiKey\":\"simple_key_456\"}",
                LocalDateTime.now(),
                null
            );
            
        } catch (Exception e) {
            log.error("B사 결제 취소 실패 - 외부결제ID: {}, 오류: {}", 
                    paymentId, e.getMessage());
            
            return new PaymentResponseDto(
                paymentId,
                PaymentStatus.FAILED,
                null, // amount is not available for failed cancellation
                PaymentProviderType.SIMPLE_PAYMENT,
                "B사",
                SIMPLE_API_BASE_URL,
                "{\"apiKey\":\"simple_key_456\"}",
                LocalDateTime.now(),
                e.getMessage()
            );
        }
    }
    
    @Override
    public String getPaymentType() {
        return "SIMPLE_PAYMENT";
    }
    
    private String generateExternalPaymentId() {
        return "SIMPLE_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // 실제 결제사 API 요청/응답 클래스들
    record SimplePaymentRequest(
        String paymentMethod,
        Integer amount,
        String merchantId,
        String reservationId
    ) {}
    
    record SimplePaymentResponse(
        String paymentId,
        String status,
        String message,
        String transactionId,
        String failureReason
    ) {}
    
    record SimplePaymentStatusResponse(
        String status
    ) {}
    
    record SimplePaymentCancelRequest(
        String paymentId
    ) {}
    
    record SimplePaymentCancelResponse(
        String message,
        String transactionId
    ) {}
} 