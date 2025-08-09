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

    @Column(name = "auth_info", nullable = false)
    private String authInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PaymentProviderType type;

    private PaymentProvider(String name, PaymentProviderType type, String apiEndpoint, String authInfo) {
        this.name = name;
        this.type = type;
        this.apiEndpoint = apiEndpoint;
        this.authInfo = authInfo;
    }

    public static PaymentProvider create(String name, PaymentProviderType type, String apiEndpoint, String authInfo) {
        return new PaymentProvider(name, type, apiEndpoint, authInfo);
    }
} 