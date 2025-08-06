package com.room.reservation.system.api.persistence.repository;

import com.room.reservation.system.api.persistence.entity.PaymentProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentProviderRepository extends JpaRepository<PaymentProvider, Long> {
} 