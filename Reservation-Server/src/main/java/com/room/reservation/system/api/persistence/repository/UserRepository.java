package com.room.reservation.system.api.persistence.repository;

import com.room.reservation.system.api.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNameAndPhoneNumber(String name, String phoneNumber);
} 
