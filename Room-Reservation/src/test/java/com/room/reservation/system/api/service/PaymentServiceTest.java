package com.room.reservation.system.api.service;

import com.room.reservation.system.api.dto.payment.PaymentStatusRequestDto;
import com.room.reservation.system.api.dto.payment.PaymentStatusResponseDto;
import com.room.reservation.system.api.dto.payment.request.PaymentRequestDto;
import com.room.reservation.system.api.dto.payment.response.PaymentPendingResponseDto;
import com.room.reservation.system.api.persistence.entity.*;
import com.room.reservation.system.api.persistence.repository.PaymentRepository;
import com.room.reservation.system.api.persistence.repository.ReservationRepository;
import com.room.reservation.system.api.persistence.repository.UserRepository;
import com.room.reservation.system.api.service.payment.PaymentStrategy;
import com.room.reservation.system.api.service.payment.PaymentStrategyFactory;
import com.room.reservation.system.global.error.exception.BadRequestException;
import com.room.reservation.system.global.error.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentStrategyFactory strategyFactory;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentStrategy paymentStrategy;

    @InjectMocks
    private PaymentService paymentService;

    private User testUser;
    private MeetingRoom testMeetingRoom;
    private Reservation testReservation;
    private Payment testPayment;
    private PaymentProvider testPaymentProvider;

    @BeforeEach
    void setUp() {
        testUser = new User("홍길동", "010-1234-5678");
        // Reflection을 사용하여 id 설정
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testUser, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        testMeetingRoom = new MeetingRoom("A회의실", 10, 25000);
        // Reflection을 사용하여 id 설정
        try {
            java.lang.reflect.Field idField = MeetingRoom.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testMeetingRoom, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        testReservation = Reservation.create(
                testUser,
                testMeetingRoom,
                LocalDateTime.now().with(LocalTime.of(14, 0)),
                LocalDateTime.now().with(LocalTime.of(15, 0)),
                50000
        );
        // Reflection을 사용하여 id 설정
        try {
            java.lang.reflect.Field idField = Reservation.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testReservation, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        testPayment = Payment.create(50000, testReservation);
        // Reflection을 사용하여 id 설정
        try {
            java.lang.reflect.Field idField = Payment.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testPayment, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        testPaymentProvider = PaymentProvider.create("테스트결제사", PaymentProviderType.CARD_PAYMENT, "http://test.com", "auth");
        // Reflection을 사용하여 id 설정
        try {
            java.lang.reflect.Field idField = PaymentProvider.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testPaymentProvider, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("결제 처리 성공")
    void processPayment_Success() {
        // given
        PaymentRequestDto request = new PaymentRequestDto(PaymentProviderType.CARD_PAYMENT, "CARD", 50000, 
                "홍길동", "010-1234-5678", null, null, null);
        PaymentPendingResponseDto expectedResponse = new PaymentPendingResponseDto("1", "ext_123", "결제가 진행중입니다", "PENDING");

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(userRepository.findByNameAndPhoneNumber("홍길동", "010-1234-5678")).thenReturn(Optional.of(testUser));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(strategyFactory.getPaymentStrategy("CARD_PAYMENT")).thenReturn(paymentStrategy);
        when(paymentStrategy.pay(any(), any(), any())).thenReturn(expectedResponse);

        // when
        PaymentPendingResponseDto result = paymentService.processPayment(1L, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.externalPaymentId()).isEqualTo("ext_123");
        assertThat(result.status()).isEqualTo("PENDING");

        verify(reservationRepository).findById(1L);
        verify(userRepository).findByNameAndPhoneNumber("홍길동", "010-1234-5678");
        verify(paymentRepository).save(any(Payment.class));
        verify(strategyFactory).getPaymentStrategy("CARD_PAYMENT");
        verify(paymentStrategy).pay(any(), any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 예약으로 결제 시도 시 예외 발생")
    void processPayment_ReservationNotFound_ThrowsException() {
        // given
        PaymentRequestDto request = new PaymentRequestDto(PaymentProviderType.CARD_PAYMENT, "CARD", 50000, 
                "홍길동", "010-1234-5678", null, null, null);

        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.processPayment(999L, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 결제 시도 시 예외 발생")
    void processPayment_UserNotFound_ThrowsException() {
        // given
        PaymentRequestDto request = new PaymentRequestDto(PaymentProviderType.CARD_PAYMENT, "CARD", 50000, 
                "존재하지않는사용자", "010-9999-9999", null, null, null);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(userRepository.findByNameAndPhoneNumber("존재하지않는사용자", "010-9999-9999"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.processPayment(1L, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("다른 사용자의 예약으로 결제 시도 시 예외 발생")
    void processPayment_InvalidUser_ThrowsException() {
        // given
        User otherUser = new User("다른사용자", "010-9999-9999");
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(otherUser, 2L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        PaymentRequestDto request = new PaymentRequestDto(PaymentProviderType.CARD_PAYMENT, "CARD", 50000, 
                "다른사용자", "010-9999-9999", null, null, null);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(userRepository.findByNameAndPhoneNumber("다른사용자", "010-9999-9999"))
                .thenReturn(Optional.of(otherUser));

        // when & then
        assertThatThrownBy(() -> paymentService.processPayment(1L, request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("이미 결제된 예약으로 결제 시도 시 예외 발생")
    void processPayment_AlreadyPaid_ThrowsException() {
        // given
        // 예약 상태를 CONFIRMED로 설정
        Reservation confirmedReservation = Reservation.create(
                testUser,
                testMeetingRoom,
                LocalDateTime.now().with(LocalTime.of(14, 0)),
                LocalDateTime.now().with(LocalTime.of(15, 0)),
                50000
        );
        // Reflection을 사용하여 id 설정
        try {
            java.lang.reflect.Field idField = Reservation.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(confirmedReservation, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 상태를 CONFIRMED로 변경
        confirmedReservation.confirm();

        PaymentRequestDto request = new PaymentRequestDto(PaymentProviderType.CARD_PAYMENT, "CARD", 50000, 
                "홍길동", "010-1234-5678", null, null, null);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(confirmedReservation));
        when(userRepository.findByNameAndPhoneNumber("홍길동", "010-1234-5678")).thenReturn(Optional.of(testUser));

        // when & then
        assertThatThrownBy(() -> paymentService.processPayment(1L, request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("결제 상태 조회 성공")
    void getPaymentStatus_Success() {
        // given
        PaymentStatusRequestDto request = new PaymentStatusRequestDto("홍길동", "010-1234-5678");
        testPayment.updateStatus(PaymentStatus.SUCCESS);
        testPayment.updatePaymentProvider(testPaymentProvider);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(userRepository.findByNameAndPhoneNumber("홍길동", "010-1234-5678")).thenReturn(Optional.of(testUser));

        // when
        PaymentStatusResponseDto result = paymentService.getPaymentStatus(1L, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.paymentStatus()).isEqualTo("SUCCESS");
        assertThat(result.providerName()).isEqualTo("테스트결제사");

        verify(paymentRepository).findById(1L);
        verify(userRepository).findByNameAndPhoneNumber("홍길동", "010-1234-5678");
    }

    @Test
    @DisplayName("존재하지 않는 결제로 상태 조회 시도 시 예외 발생")
    void getPaymentStatus_PaymentNotFound_ThrowsException() {
        // given
        PaymentStatusRequestDto request = new PaymentStatusRequestDto("홍길동", "010-1234-5678");

        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.getPaymentStatus(999L, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("다른 사용자의 결제 상태 조회 시도 시 예외 발생")
    void getPaymentStatus_InvalidUser_ThrowsException() {
        // given
        User otherUser = new User("다른사용자", "010-9999-9999");
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(otherUser, 2L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        PaymentStatusRequestDto request = new PaymentStatusRequestDto("다른사용자", "010-9999-9999");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(userRepository.findByNameAndPhoneNumber("다른사용자", "010-9999-9999"))
                .thenReturn(Optional.of(otherUser));

        // when & then
        assertThatThrownBy(() -> paymentService.getPaymentStatus(1L, request))
                .isInstanceOf(BadRequestException.class);
    }
}
