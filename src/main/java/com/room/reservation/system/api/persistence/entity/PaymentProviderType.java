package com.room.reservation.system.api.persistence.entity;

public enum PaymentProviderType {
    CARD_PAYMENT,      // A사 결제사: 신용카드 결제
    SIMPLE_PAYMENT,    // B사 결제사: 간편결제
    VIRTUAL_ACCOUNT    // C사 결제사: 가상계좌 결제
} 