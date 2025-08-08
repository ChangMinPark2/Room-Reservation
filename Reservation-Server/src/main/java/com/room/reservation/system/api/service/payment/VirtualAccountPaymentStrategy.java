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
 * C사 가상계좌 결제 전략
 * F-Lab 방식: Strategy Pattern 구현체
 */
@Slf4j
@Component
public class VirtualAccountPaymentStrategy implements PaymentStrategy {
    
    private final RestTemplate restTemplate;
    private static final String VIRTUAL_API_BASE_URL = "https://api.virtual-account.com";
    
    public VirtualAccountPaymentStrategy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public PaymentResponseDto pay(Reservation reservation, PaymentRequestDto request) {
        log.info("C사 가상계좌 결제 시작 - 예약ID: {}, 금액: {}", 
                reservation.getId(), request.amount());
        
        try {
            // 실제 결제사 API 요청 데이터 생성
            VirtualAccountPaymentRequest virtualRequest = new VirtualAccountPaymentRequest(
                request.accountNumber(),
                request.amount(),
                "C_COMPANY",
                reservation.getId().toString()
            );
            
            // 실제 결제사 API 호출
            VirtualAccountPaymentResponse response = restTemplate.postForObject(
                VIRTUAL_API_BASE_URL + "/payment",
                virtualRequest,
                VirtualAccountPaymentResponse.class
            );
            
            if (response == null) {
                throw new RuntimeException("결제사 응답이 null입니다.");
            }
            
            // 응답을 DTO로 변환
            PaymentResponseDto result = new PaymentResponseDto(
                response.paymentId(),                    // externalPaymentId
                PaymentStatus.valueOf(response.status()), // status
                request.amount(),                        // amount
                PaymentProviderType.VIRTUAL_ACCOUNT,     // providerType
                "C사",                                   // providerName
                VIRTUAL_API_BASE_URL,                   // apiEndpoint
                "{\"apiKey\":\"virtual_key_789\"}",     // authInfo
                LocalDateTime.now(),                     // processedAt
                response.failureReason()                 // failureReason
            );
            
            log.info("C사 가상계좌 결제 완료 - 예약ID: {}, 상태: {}", 
                    reservation.getId(), result.status());
            
            return result;
            
        } catch (Exception e) {
            log.error("C사 가상계좌 결제 실패 - 예약ID: {}, 오류: {}", 
                    reservation.getId(), e.getMessage());
            
            return new PaymentResponseDto(
                generateExternalPaymentId(),
                PaymentStatus.FAILED,
                request.amount(),
                PaymentProviderType.VIRTUAL_ACCOUNT,
                "C사",
                VIRTUAL_API_BASE_URL,
                "{\"apiKey\":\"virtual_key_789\"}",
                LocalDateTime.now(),
                e.getMessage()
            );
        }
    }
    
    @Override
    public PaymentStatus checkPaymentStatus(String paymentId) {
        log.info("C사 결제 상태 조회 - 외부결제ID: {}", paymentId);
        
        try {
            VirtualAccountPaymentStatusResponse response = restTemplate.getForObject(
                VIRTUAL_API_BASE_URL + "/status/" + paymentId,
                VirtualAccountPaymentStatusResponse.class
            );
            
            if (response == null) {
                return PaymentStatus.FAILED;
            }
            
            return PaymentStatus.valueOf(response.status());
            
        } catch (Exception e) {
            log.error("C사 결제 상태 조회 실패 - 외부결제ID: {}, 오류: {}", 
                    paymentId, e.getMessage());
            return PaymentStatus.FAILED;
        }
    }
    
    @Override
    public PaymentResponseDto cancelPayment(String paymentId) {
        log.info("C사 결제 취소 시작 - 외부결제ID: {}", paymentId);
        
        try {
            VirtualAccountPaymentCancelResponse response = restTemplate.postForObject(
                VIRTUAL_API_BASE_URL + "/cancel",
                new VirtualAccountPaymentCancelRequest(paymentId),
                VirtualAccountPaymentCancelResponse.class
            );
            
            if (response == null) {
                throw new RuntimeException("취소 응답이 null입니다.");
            }
            
            return new PaymentResponseDto(
                paymentId,
                PaymentStatus.CANCELLED,
                null, // amount is not available for cancellation
                PaymentProviderType.VIRTUAL_ACCOUNT,
                "C사",
                VIRTUAL_API_BASE_URL,
                "{\"apiKey\":\"virtual_key_789\"}",
                LocalDateTime.now(),
                null
            );
            
        } catch (Exception e) {
            log.error("C사 결제 취소 실패 - 외부결제ID: {}, 오류: {}", 
                    paymentId, e.getMessage());
            
            return new PaymentResponseDto(
                paymentId,
                PaymentStatus.FAILED,
                null, // amount is not available for failed cancellation
                PaymentProviderType.VIRTUAL_ACCOUNT,
                "C사",
                VIRTUAL_API_BASE_URL,
                "{\"apiKey\":\"virtual_key_789\"}",
                LocalDateTime.now(),
                e.getMessage()
            );
        }
    }
    
    @Override
    public String getPaymentType() {
        return "VIRTUAL_ACCOUNT";
    }
    
    private String generateExternalPaymentId() {
        return "VIRTUAL_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // 실제 결제사 API 요청/응답 클래스들
    record VirtualAccountPaymentRequest(
        String accountNumber,
        Integer amount,
        String merchantId,
        String reservationId
    ) {}
    
    record VirtualAccountPaymentResponse(
        String paymentId,
        String status,
        String message,
        String transactionId,
        String failureReason
    ) {}
    
    record VirtualAccountPaymentStatusResponse(
        String status
    ) {}
    
    record VirtualAccountPaymentCancelRequest(
        String paymentId
    ) {}
    
    record VirtualAccountPaymentCancelResponse(
        String message,
        String transactionId
    ) {}
} 