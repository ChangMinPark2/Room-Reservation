package com.room.reservation.system.api.service;

import com.room.reservation.system.api.dto.payment.PaymentRequestDto;
import com.room.reservation.system.api.dto.payment.PaymentResponseDto;
import com.room.reservation.system.api.persistence.entity.PaymentStatus;
import com.room.reservation.system.api.persistence.entity.Reservation;
import com.room.reservation.system.api.persistence.repository.UserRepository;
import com.room.reservation.system.api.persistence.repository.ReservationRepository;
import com.room.reservation.system.api.service.payment.PaymentStrategy;
import com.room.reservation.system.api.service.payment.PaymentStrategyFactory;
import com.room.reservation.system.global.error.exception.BadRequestException;
import com.room.reservation.system.global.error.exception.NotFoundException;
import com.room.reservation.system.global.error.model.ErrorCode;
import com.room.reservation.system.api.persistence.entity.ReservationStatus;
import com.room.reservation.system.api.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    
    private final PaymentStrategyFactory strategyFactory;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final WebhookService webhookService;

    public PaymentResponseDto processPayment(Long reservationId, PaymentRequestDto request) {
        log.info("결제 요청 - 예약ID: {}, 결제사: {}", reservationId, request.providerType());

        final Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_RESERVATION));
        final User user = userRepository.findByNameAndPhoneNumber(request.userName(), request.phoneNumber())
            .orElseThrow(() -> new NotFoundException(ErrorCode.FAIL_NOT_USER));

        validateUserAndReservation(reservation, user);
        validateReservationStatus(reservation);

        final PaymentStrategy strategy = strategyFactory.getPaymentStrategy(request.providerType().name());
        final PaymentResponseDto result = strategy.pay(reservation, request);

        updateReservationStatus(reservation, result, reservationId);
        
        log.info("결제 처리 완료 - 예약ID: {}, 상태: {}", reservationId, result.status());
        return result;
    }

    private void validateUserAndReservation(Reservation reservation, User user) {
        if (!reservation.getUser().getId().equals(user.getId())) {
            log.warn("예약자 정보 불일치 - 예약ID: {}, 예약자: {}, 요청자: {}", 
                    reservation.getId(), reservation.getUser().getName(), user.getName());
            throw new BadRequestException(ErrorCode.FAIL_INVALID_USER);
        }
        
        log.info("예약자 검증 완료 - 예약ID: {}, 사용자: {}", reservation.getId(), user.getName());
    }

    private void validateReservationStatus(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            log.warn("이미 결제 완료된 예약 - 예약ID: {}", reservation.getId());
            throw new BadRequestException(ErrorCode.FAIL_ALREADY_PAID);
        }
        
        log.info("예약 상태 검증 완료 - 예약ID: {}, 상태: {}", reservation.getId(), reservation.getStatus());
    }

    /**
     * 결제 결과에 따른 예약 상태 업데이트
     */
    private void updateReservationStatus(Reservation reservation, PaymentResponseDto result, Long reservationId) {
        if (result.status() == PaymentStatus.SUCCESS) {
            // 예약 상태 확정
            reservation.confirm();
            reservationRepository.save(reservation);
            log.info("예약 상태 확정 - 예약ID: {}", reservationId);
        } else {
            log.warn("결제 실패로 예약 상태 변경하지 않음 - 예약ID: {}, 상태: {}", 
                    reservationId, result.status());
        }
    }
    
    /**
     * 결제 상태 조회
     * @param paymentId 결제 ID
     * @return 결제 상태
     */
    public PaymentStatus getPaymentStatus(String paymentId) {
        log.info("결제 상태 조회 - 결제ID: {}", paymentId);
        

            String paymentType = "CARD_PAYMENT";
            
            PaymentStrategy strategy = strategyFactory.getPaymentStrategy(paymentType);
            PaymentStatus status = strategy.checkPaymentStatus(paymentId);
            
            log.info("결제 상태 조회 완료 - 결제ID: {}, 상태: {}", paymentId, status);
            return status;
            

    }
} 