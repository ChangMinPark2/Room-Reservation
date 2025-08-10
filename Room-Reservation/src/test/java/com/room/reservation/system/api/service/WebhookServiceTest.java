package com.room.reservation.system.api.service;

import com.room.reservation.system.api.dto.payment.WebhookPaymentDto;
import com.room.reservation.system.api.persistence.entity.*;
import com.room.reservation.system.api.persistence.repository.PaymentProviderRepository;
import com.room.reservation.system.api.persistence.repository.PaymentRepository;
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
class WebhookServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentProviderRepository paymentProviderRepository;

    @InjectMocks
    private WebhookService webhookService;

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
    @DisplayName("결제 성공 웹훅 처리 - 예약 상태가 CONFIRMED로 변경됨")
    void processPaymentWebhook_Success_ReservationStatusChanged() {
        // given
        String providerType = "CARD_PAYMENT";
        WebhookPaymentDto webhookData = new WebhookPaymentDto(
                "ext_123", "SUCCESS", 50000, "txn_123", "결제 성공", 
                "테스트결제사", "http://test.com", "auth", "CARD_PAYMENT", "1");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentProviderRepository.findByName("테스트결제사")).thenReturn(Optional.of(testPaymentProvider));

        // when
        webhookService.processPaymentWebhook(providerType, webhookData);

        // then
        verify(paymentRepository).findById(1L);
        verify(paymentProviderRepository).findByName("테스트결제사");
        
        // 결제 상태가 SUCCESS로 변경되었는지 확인
        assertThat(testPayment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        
        // 예약 상태가 CONFIRMED로 변경되었는지 확인
        assertThat(testReservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    @DisplayName("결제 실패 웹훅 처리 - 예약 상태 변경되지 않음")
    void processPaymentWebhook_Failed_ReservationStatusNotChanged() {
        // given
        String providerType = "CARD_PAYMENT";
        WebhookPaymentDto webhookData = new WebhookPaymentDto(
                "ext_123", "FAILED", 50000, "txn_123", "결제 실패", 
                "테스트결제사", "http://test.com", "auth", "CARD_PAYMENT", "1");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentProviderRepository.findByName("테스트결제사")).thenReturn(Optional.of(testPaymentProvider));

        // when
        webhookService.processPaymentWebhook(providerType, webhookData);

        // then
        verify(paymentRepository).findById(1L);
        verify(paymentProviderRepository).findByName("테스트결제사");
        
        // 결제 상태가 FAILED로 변경되었는지 확인
        assertThat(testPayment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        
        // 예약 상태가 변경되지 않았는지 확인 (PENDING 유지)
        assertThat(testReservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    @DisplayName("결제 취소 웹훅 처리 - 예약 상태 변경되지 않음")
    void processPaymentWebhook_Cancelled_ReservationStatusNotChanged() {
        // given
        String providerType = "CARD_PAYMENT";
        WebhookPaymentDto webhookData = new WebhookPaymentDto(
                "ext_123", "CANCELLED", 50000, "txn_123", "결제 취소", 
                "테스트결제사", "http://test.com", "auth", "CARD_PAYMENT", "1");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentProviderRepository.findByName("테스트결제사")).thenReturn(Optional.of(testPaymentProvider));

        // when
        webhookService.processPaymentWebhook(providerType, webhookData);

        // then
        verify(paymentRepository).findById(1L);
        verify(paymentProviderRepository).findByName("테스트결제사");
        
        // 결제 상태가 CANCELLED로 변경되었는지 확인
        assertThat(testPayment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        
        // 예약 상태가 변경되지 않았는지 확인 (PENDING 유지)
        assertThat(testReservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    @DisplayName("존재하지 않는 결제로 웹훅 처리 시 예외 발생")
    void processPaymentWebhook_PaymentNotFound_ThrowsException() {
        // given
        String providerType = "CARD_PAYMENT";
        WebhookPaymentDto webhookData = new WebhookPaymentDto(
                "ext_123", "SUCCESS", 50000, "txn_123", "결제 성공", 
                "테스트결제사", "http://test.com", "auth", "CARD_PAYMENT", "999");

        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> webhookService.processPaymentWebhook(providerType, webhookData))
                .isInstanceOf(NotFoundException.class);

        verify(paymentRepository).findById(999L);
    }

    @Test
    @DisplayName("새로운 결제사로 웹훅 처리 - 새로운 PaymentProvider 생성")
    void processPaymentWebhook_NewPaymentProvider_CreatesNewProvider() {
        // given
        String providerType = "CARD_PAYMENT";
        WebhookPaymentDto webhookData = new WebhookPaymentDto(
                "ext_123", "SUCCESS", 50000, "txn_123", "결제 성공", 
                "새로운결제사", "http://new.com", "new_auth", "CARD_PAYMENT", "1");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentProviderRepository.findByName("새로운결제사")).thenReturn(Optional.empty());
        when(paymentProviderRepository.save(any(PaymentProvider.class))).thenReturn(testPaymentProvider);

        // when
        webhookService.processPaymentWebhook(providerType, webhookData);

        // then
        verify(paymentRepository).findById(1L);
        verify(paymentProviderRepository).findByName("새로운결제사");
        verify(paymentProviderRepository).save(any(PaymentProvider.class));
    }

    @Test
    @DisplayName("알 수 없는 웹훅 상태 처리 - FAILED로 처리")
    void processPaymentWebhook_UnknownStatus_TreatedAsFailed() {
        // given
        String providerType = "CARD_PAYMENT";
        WebhookPaymentDto webhookData = new WebhookPaymentDto(
                "ext_123", "UNKNOWN_STATUS", 50000, "txn_123", "알 수 없는 상태", 
                "테스트결제사", "http://test.com", "auth", "CARD_PAYMENT", "1");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentProviderRepository.findByName("테스트결제사")).thenReturn(Optional.of(testPaymentProvider));

        // when
        webhookService.processPaymentWebhook(providerType, webhookData);

        // then
        verify(paymentRepository).findById(1L);
        verify(paymentProviderRepository).findByName("테스트결제사");
        
        // 알 수 없는 상태가 FAILED로 처리되었는지 확인
        assertThat(testPayment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        
        // 예약 상태가 변경되지 않았는지 확인
        assertThat(testReservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    @DisplayName("웹훅 처리 중 예외 발생 시 예외 전파")
    void processPaymentWebhook_ExceptionOccurs_ThrowsException() {
        // given
        String providerType = "CARD_PAYMENT";
        WebhookPaymentDto webhookData = new WebhookPaymentDto(
                "ext_123", "SUCCESS", 50000, "txn_123", "결제 성공", 
                "테스트결제사", "http://test.com", "auth", "CARD_PAYMENT", "1");

        when(paymentRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        // when & then
        assertThatThrownBy(() -> webhookService.processPaymentWebhook(providerType, webhookData))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(paymentRepository).findById(1L);
    }
}
