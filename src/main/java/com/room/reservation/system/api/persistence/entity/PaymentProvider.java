package com.room.reservation.system.api.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Getter
@Table(name = "tbl_payment_provider")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentProvider {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "api_endpoint", nullable = false)
    private String apiEndpoint;

    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Column(name = "secret_key", nullable = false)
    private String secretKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PaymentProviderType type;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public PaymentProvider(String name, String apiEndpoint, String apiKey,
                           String secretKey, PaymentProviderType type) {
        this.name = name;
        this.apiEndpoint = apiEndpoint;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.type = type;
    }
} 