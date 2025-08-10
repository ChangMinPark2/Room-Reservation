package com.room.reservation.system.api.service.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentStrategyFactoryTest {

    @Mock
    private CardPaymentStrategy cardPaymentStrategy;

    @Mock
    private SimplePaymentStrategy simplePaymentStrategy;

    @Mock
    private VirtualAccountPaymentStrategy virtualAccountPaymentStrategy;

    private PaymentStrategyFactory paymentStrategyFactory;

    @BeforeEach
    void setUp() {
        // 각 전략의 getPaymentType() 메서드 설정
        when(cardPaymentStrategy.getPaymentType()).thenReturn("CARD_PAYMENT");
        when(simplePaymentStrategy.getPaymentType()).thenReturn("SIMPLE_PAYMENT");
        when(virtualAccountPaymentStrategy.getPaymentType()).thenReturn("VIRTUAL_ACCOUNT");

        // 팩토리 생성
        List<PaymentStrategy> strategies = Arrays.asList(
                cardPaymentStrategy,
                simplePaymentStrategy,
                virtualAccountPaymentStrategy
        );
        paymentStrategyFactory = new PaymentStrategyFactory(strategies);
    }

    @Test
    @DisplayName("카드 결제 전략 조회 성공")
    void getPaymentStrategy_CardPayment_Success() {
        // when
        PaymentStrategy result = paymentStrategyFactory.getPaymentStrategy("CARD_PAYMENT");

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(cardPaymentStrategy);
        assertThat(result.getPaymentType()).isEqualTo("CARD_PAYMENT");
    }

    @Test
    @DisplayName("간편 결제 전략 조회 성공")
    void getPaymentStrategy_SimplePayment_Success() {
        // when
        PaymentStrategy result = paymentStrategyFactory.getPaymentStrategy("SIMPLE_PAYMENT");

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(simplePaymentStrategy);
        assertThat(result.getPaymentType()).isEqualTo("SIMPLE_PAYMENT");
    }

    @Test
    @DisplayName("가상 계좌 결제 전략 조회 성공")
    void getPaymentStrategy_VirtualAccount_Success() {
        // when
        PaymentStrategy result = paymentStrategyFactory.getPaymentStrategy("VIRTUAL_ACCOUNT");

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(virtualAccountPaymentStrategy);
        assertThat(result.getPaymentType()).isEqualTo("VIRTUAL_ACCOUNT");
    }

    @Test
    @DisplayName("지원하지 않는 결제 타입으로 전략 조회 시 예외 발생")
    void getPaymentStrategy_UnsupportedType_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> paymentStrategyFactory.getPaymentStrategy("UNSUPPORTED_TYPE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 결제사 타입입니다: UNSUPPORTED_TYPE");
    }

    @Test
    @DisplayName("지원하는 결제 타입 목록 조회")
    void getSupportedPaymentTypes_Success() {
        // when
        List<String> supportedTypes = paymentStrategyFactory.getSupportedPaymentTypes();

        // then
        assertThat(supportedTypes).isNotNull();
        assertThat(supportedTypes).hasSize(3);
        assertThat(supportedTypes).contains("CARD_PAYMENT");
        assertThat(supportedTypes).contains("SIMPLE_PAYMENT");
        assertThat(supportedTypes).contains("VIRTUAL_ACCOUNT");
    }

    @Test
    @DisplayName("결제 타입 지원 여부 확인 - 지원하는 타입")
    void isSupported_SupportedType_ReturnsTrue() {
        // when & then
        assertThat(paymentStrategyFactory.isSupported("CARD_PAYMENT")).isTrue();
        assertThat(paymentStrategyFactory.isSupported("SIMPLE_PAYMENT")).isTrue();
        assertThat(paymentStrategyFactory.isSupported("VIRTUAL_ACCOUNT")).isTrue();
    }

    @Test
    @DisplayName("결제 타입 지원 여부 확인 - 지원하지 않는 타입")
    void isSupported_UnsupportedType_ReturnsFalse() {
        // when & then
        assertThat(paymentStrategyFactory.isSupported("UNSUPPORTED_TYPE")).isFalse();
        assertThat(paymentStrategyFactory.isSupported("INVALID_TYPE")).isFalse();
    }

    @Test
    @DisplayName("대소문자 구분 확인")
    void getPaymentStrategy_CaseSensitive_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> paymentStrategyFactory.getPaymentStrategy("card_payment"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 결제사 타입입니다: card_payment");
    }

    @Test
    @DisplayName("null 값으로 전략 조회 시 예외 발생")
    void getPaymentStrategy_NullType_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> paymentStrategyFactory.getPaymentStrategy(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 결제사 타입입니다: null");
    }

    @Test
    @DisplayName("빈 문자열로 전략 조회 시 예외 발생")
    void getPaymentStrategy_EmptyType_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> paymentStrategyFactory.getPaymentStrategy(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 결제사 타입입니다: ");
    }
}
