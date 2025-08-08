package com.room.reservation.system.api.service.payment;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 결제 전략 팩토리
 * F-Lab 방식: Factory Pattern을 사용한 전략 객체 생성
 */
@Component
public class PaymentStrategyFactory {
    
    private final Map<String, PaymentStrategy> strategies;

    public PaymentStrategyFactory(List<PaymentStrategy> allStrategies) {
        this.strategies = allStrategies.stream()
            .collect(Collectors.toMap(
                PaymentStrategy::getPaymentType,  // 키: 결제사 타입
                Function.identity()                // 값: 전략 객체
            ));
    }
    
    public PaymentStrategy getPaymentStrategy(String paymentType) {
        PaymentStrategy strategy = strategies.get(paymentType);
        validateExsitType(paymentType, strategy);

        return strategy;
    }

    private static void validateExsitType(String paymentType, PaymentStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException(
                String.format("지원하지 않는 결제사 타입입니다: %s", paymentType)
            );
        }
    }

    public List<String> getSupportedPaymentTypes() {
        return strategies.keySet().stream().toList();
    }
    
    public boolean isSupported(String paymentType) {
        return strategies.containsKey(paymentType);
    }
} 