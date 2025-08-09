package com.room.reservation.system.api.service;

import com.room.reservation.system.api.dto.payment.request.PaymentRequestDto;
import com.room.reservation.system.api.dto.payment.response.PaymentPendingResponseDto;
import com.room.reservation.system.api.dto.payment.PaymentStatusRequestDto;
import com.room.reservation.system.api.dto.payment.PaymentStatusResponseDto;
import com.room.reservation.system.api.persistence.entity.*;
import com.room.reservation.system.api.persistence.repository.PaymentRepository;
import com.room.reservation.system.api.persistence.repository.UserRepository;
import com.room.reservation.system.api.persistence.repository.ReservationRepository;
import com.room.reservation.system.api.persistence.entity.Payment;
import com.room.reservation.system.api.service.payment.PaymentStrategy;
import com.room.reservation.system.api.service.payment.PaymentStrategyFactory;
import com.room.reservation.system.global.error.exception.BadRequestException;
import com.room.reservation.system.global.error.exception.NotFoundException;
import com.room.reservation.system.global.error.model.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentStrategyFactory strategyFactory;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public PaymentPendingResponseDto processPayment(Long reservationId, PaymentRequestDto request) {
        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_RESERVATION));
        final User user = userRepository.findByNameAndPhoneNumber(request.userName(), request.phoneNumber())
                .orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER));

        validateUserAndReservation(reservation, user);
        validateReservationStatus(reservation);

        // Payment 객체 생성 및 DB 저장 (PENDING 상태)
        final Payment payment = Payment.create(request.amount(), reservation);
        paymentRepository.save(payment); // PENDING으로 저장

        // 결제 전략 호출 (내부 Payment ID 전달)
        final PaymentStrategy strategy = strategyFactory.getPaymentStrategy(request.providerType().name());
        final PaymentPendingResponseDto strategyResult = strategy.pay(payment.getId(), reservation, request);

        // Payment 엔티티에 외부 결제 ID 업데이트
        payment.updateExternalPaymentId(strategyResult.externalPaymentId()); //외부 ID 업데이트

        log.info("결제 요청 완료 - 예약ID: {}, 내부결제ID: {}, 외부결제ID: {}",
                reservationId, payment.getId(), strategyResult.externalPaymentId());

        return strategyResult;
    }

    /**
     * 결제 상태 조회
     * @param paymentId 결제 ID
     * @param request 사용자 검증 정보 (이름, 전화번호)
     * @return 결제 상태 정보
     */
    @Transactional(readOnly = true)
    public PaymentStatusResponseDto getPaymentStatus(Long paymentId, PaymentStatusRequestDto request) {
        final Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_PAYMENT));
        final User user = userRepository.findByNameAndPhoneNumber(request.userName(), request.phoneNumber())
                .orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER));

        validateUserAndReservation(payment.getReservation(), user);

        final String message = generateStatusMessage(payment.getStatus(), payment.getPaymentProvider());

        return payment.toResponseDto(message);
    }

    /**
     * 결제 상태에 따른 메시지 생성
     */
    private String generateStatusMessage(PaymentStatus status, PaymentProvider provider) {
        if (provider == null) {
            return provider.getName() + "에서 결제가 성공적으로 완료되었습니다.";
        }
        
        return switch (status) {
            case PENDING -> "결제가 진행 중입니다. 잠시만 기다려 주세요.";
            case SUCCESS -> provider.getName() + "에서 결제가 성공적으로 완료되었습니다.";
            case FAILED -> "결제 처리 중 오류가 발생했습니다. 다시 시도해 주세요.";
            case CANCELLED -> "결제가 취소되었습니다.";
        };
    }

    private void validateUserAndReservation(Reservation reservation, User user) {
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new BadRequestException(ErrorCode.FAIL_INVALID_USER);
        }
    }

    private void validateReservationStatus(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            throw new BadRequestException(ErrorCode.FAIL_ALREADY_PAID);
        }
    }
} 