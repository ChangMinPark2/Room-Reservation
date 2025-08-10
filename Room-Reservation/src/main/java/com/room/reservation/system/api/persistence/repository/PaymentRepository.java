package com.room.reservation.system.api.persistence.repository;

import com.room.reservation.system.api.persistence.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    @Query("SELECT p FROM Payment p WHERE p.reservation.id = :reservationId")
    List<Payment> findByReservationId(@Param("reservationId") Long reservationId);
}
