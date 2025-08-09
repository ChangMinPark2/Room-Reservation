package com.room.reservation.system.api.persistence.entity;

import com.room.reservation.system.api.dto.payment.PaymentStatusResponseDto;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "tbl_payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Payment {
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_provider_id", nullable = true)
    private PaymentProvider paymentProvider;
    
    @Column(name = "amount", nullable = false)
    private Integer amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;
    
    @Column(name = "external_payment_id", nullable = true)
    private String externalPaymentId;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private Payment(Integer amount, Reservation reservation) {
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.reservation = reservation;
    }

    public static Payment create(Integer amount, Reservation reservation) {
        return new Payment(amount, reservation);
    }

    public PaymentStatusResponseDto toResponseDto(String message) {
        return new PaymentStatusResponseDto(
            this.reservation.getId(),
            this.id,
            this.externalPaymentId,
            this.status.name(),
            this.paymentProvider.getName(),
            amount,
            message
        );
    }

    public void confirm() {
        this.status = PaymentStatus.SUCCESS;
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

    public void updateExternalPaymentId(String externalPaymentId) {
        this.externalPaymentId = externalPaymentId;
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

    public void updatePaymentProvider(PaymentProvider paymentProvider) {
        this.paymentProvider = paymentProvider;
    }
} 