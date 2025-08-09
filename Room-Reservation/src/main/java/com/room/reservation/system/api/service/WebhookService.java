package com.room.reservation.system.api.service;

import com.room.reservation.system.api.dto.payment.WebhookPaymentDto;
import com.room.reservation.system.api.persistence.entity.Payment;
import com.room.reservation.system.api.persistence.entity.PaymentProvider;
import com.room.reservation.system.api.persistence.entity.PaymentProviderType;
import com.room.reservation.system.api.persistence.entity.PaymentStatus;
import com.room.reservation.system.api.persistence.repository.PaymentProviderRepository;
import com.room.reservation.system.api.persistence.repository.PaymentRepository;
import com.room.reservation.system.global.error.exception.NotFoundException;
import com.room.reservation.system.global.error.model.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 외부 결제사로부터 웹훅을 수신하여 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final PaymentRepository paymentRepository;
    private final PaymentProviderRepository paymentProviderRepository;

    /**
     * 결제 웹훅 처리
     * 새로운 트랜잭션에서 실행하여 메인 결제 트랜잭션과 분리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processPaymentWebhook(String providerType, WebhookPaymentDto webhookData) {
        log.info("=== 웹훅 처리 시작 ===");
        try {
            final Long paymentId = Long.parseLong(webhookData.paymentId());
            final Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_PAYMENT));

            log.info("결제 정보 조회 완료 - 예약ID: {}, 현재상태: {}", 
                    payment.getReservation().getId(), payment.getStatus());

            final PaymentProvider paymentProvider = createOrGetPaymentProvider(
                    webhookData.providerName(),
                    webhookData.apiEndpoint(),
                    webhookData.authInfo(),
                    PaymentProviderType.valueOf(webhookData.providerType())
            );

            log.info("PaymentProvider 처리 완료 - ID: {}, 이름: {}", 
                    paymentProvider.getId(), paymentProvider.getName());

            payment.updateStatus(convertToPaymentStatus(webhookData.status()));
            payment.updatePaymentProvider(paymentProvider);

            log.info("=== 웹훅 처리 완료 ===");

        } catch (Exception e) {
            log.error("웹훅 처리 중 오류 발생 - 결제사: {}, 외부결제ID: {}, 오류: {}", 
                    providerType, webhookData.externalPaymentId(), e.getMessage(), e);
            throw e;
        }
    }

    private PaymentProvider createOrGetPaymentProvider(
            String name,
            String apiEndpoint,
            String authInfo,
            PaymentProviderType type
    ) {
        return paymentProviderRepository.findByName(name)
                .orElseGet(() -> {
                    log.info("새로운 PaymentProvider 생성 - 이름: {}, 타입: {}", name, type);
                    PaymentProvider newProvider = PaymentProvider.create(name, type, apiEndpoint, authInfo);
                    return paymentProviderRepository.save(newProvider);
                });
    }

    private PaymentStatus convertToPaymentStatus(String webhookStatus) {
        return switch (webhookStatus) {
            case "SUCCESS" -> PaymentStatus.SUCCESS;
            case "FAILED" -> PaymentStatus.FAILED;
            case "CANCELLED" -> PaymentStatus.CANCELLED;
            default -> {
                log.warn("알 수 없는 웹훅 상태: {}, FAILED로 처리", webhookStatus);
                yield PaymentStatus.FAILED;
            }
        };
    }
}