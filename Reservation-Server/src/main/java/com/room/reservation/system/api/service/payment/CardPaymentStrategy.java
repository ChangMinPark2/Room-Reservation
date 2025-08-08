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
 * A사 신용카드 결제 전략
 * F-Lab 방식: Strategy Pattern 구현체
 */
@Slf4j
@Component
public class CardPaymentStrategy implements PaymentStrategy {
    
    private final RestTemplate restTemplate;
    private static final String CARD_API_BASE_URL = "https://api.card-payment.com";
    
    public CardPaymentStrategy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public PaymentResponseDto pay(Reservation reservation, PaymentRequestDto request) {
        log.info("A사 신용카드 결제 시작 - 예약ID: {}, 금액: {}", 
                reservation.getId(), request.amount());
        
        try {
            // 실제 결제사 API 요청 데이터 생성
            CardPaymentRequest cardRequest = new CardPaymentRequest(
                request.cardNumber(),
                request.amount(),
                "A_COMPANY",
                reservation.getId().toString()
            );
            
            // 실제 결제사 API 호출
            CardPaymentResponse response = restTemplate.postForObject(
                CARD_API_BASE_URL + "/payment",
                cardRequest,
                CardPaymentResponse.class
            );
            
            if (response == null) {
                throw new RuntimeException("결제사 응답이 null입니다.");
            }
            
            // 응답을 DTO로 변환
            PaymentResponseDto result = new PaymentResponseDto(
                response.paymentId(),                    // externalPaymentId
                PaymentStatus.valueOf(response.status()), // status
                request.amount(),                        // amount
                PaymentProviderType.CARD_PAYMENT,        // providerType
                "A사",                                   // providerName
                CARD_API_BASE_URL,                      // apiEndpoint
                "{\"apiKey\":\"card_key_123\"}",        // authInfo
                LocalDateTime.now(),                     // processedAt
                response.failureReason()                 // failureReason
            );
            
            log.info("A사 신용카드 결제 완료 - 예약ID: {}, 상태: {}", 
                    reservation.getId(), result.status());
            
            return result;
            
        } catch (Exception e) {
            log.error("A사 신용카드 결제 실패 - 예약ID: {}, 오류: {}", 
                    reservation.getId(), e.getMessage());
            
            return new PaymentResponseDto(
                generateExternalPaymentId(),
                PaymentStatus.FAILED,
                request.amount(),
                PaymentProviderType.CARD_PAYMENT,
                "A사",
                CARD_API_BASE_URL,
                "{\"apiKey\":\"card_key_123\"}",
                LocalDateTime.now(),
                e.getMessage()
            );
        }
    }
    
    @Override
    public PaymentStatus checkPaymentStatus(String paymentId) {
        log.info("A사 결제 상태 조회 - 외부결제ID: {}", paymentId);
        
        try {
            CardPaymentStatusResponse response = restTemplate.getForObject(
                CARD_API_BASE_URL + "/status/" + paymentId,
                CardPaymentStatusResponse.class
            );
            
            if (response == null) {
                return PaymentStatus.FAILED;
            }
            
            return PaymentStatus.valueOf(response.status());
            
        } catch (Exception e) {
            log.error("A사 결제 상태 조회 실패 - 외부결제ID: {}, 오류: {}", 
                    paymentId, e.getMessage());
            return PaymentStatus.FAILED;
        }
    }
    
    @Override
    public PaymentResponseDto cancelPayment(String paymentId) {
        log.info("A사 결제 취소 시작 - 외부결제ID: {}", paymentId);
        
        try {
            CardPaymentCancelResponse response = restTemplate.postForObject(
                CARD_API_BASE_URL + "/cancel",
                new CardPaymentCancelRequest(paymentId),
                CardPaymentCancelResponse.class
            );
            
            if (response == null) {
                throw new RuntimeException("취소 응답이 null입니다.");
            }
            
            return new PaymentResponseDto(
                paymentId,
                PaymentStatus.CANCELLED,
                null, // amount is not available for cancellation
                PaymentProviderType.CARD_PAYMENT,
                "A사",
                CARD_API_BASE_URL,
                "{\"apiKey\":\"card_key_123\"}",
                LocalDateTime.now(),
                null
            );
            
        } catch (Exception e) {
            log.error("A사 결제 취소 실패 - 외부결제ID: {}, 오류: {}", 
                    paymentId, e.getMessage());
            
            return new PaymentResponseDto(
                paymentId,
                PaymentStatus.FAILED,
                null, // amount is not available for failed cancellation
                PaymentProviderType.CARD_PAYMENT,
                "A사",
                CARD_API_BASE_URL,
                "{\"apiKey\":\"card_key_123\"}",
                LocalDateTime.now(),
                e.getMessage()
            );
        }
    }
    
    @Override
    public String getPaymentType() {
        return "CARD_PAYMENT";
    }
    
    private String generateExternalPaymentId() {
        return "CARD_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // 실제 결제사 API 요청/응답 클래스들
    record CardPaymentRequest(
        String cardNumber,
        Integer amount,
        String merchantId,
        String reservationId
    ) {}
    
    record CardPaymentResponse(
        String paymentId,
        String status,
        String message,
        String transactionId,
        String failureReason
    ) {}
    
    record CardPaymentStatusResponse(
        String status
    ) {}
    
    record CardPaymentCancelRequest(
        String paymentId
    ) {}
    
    record CardPaymentCancelResponse(
        String message,
        String transactionId
    ) {}
} 