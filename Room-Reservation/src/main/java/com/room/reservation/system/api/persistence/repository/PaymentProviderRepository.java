package com.room.reservation.system.api.persistence.repository;

import com.room.reservation.system.api.persistence.entity.PaymentProvider;
import com.room.reservation.system.api.persistence.entity.PaymentProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentProviderRepository extends JpaRepository<PaymentProvider, Long> {
    Optional<PaymentProvider> findByType(PaymentProviderType type);
    Optional<PaymentProvider> findByName(String name);
} 