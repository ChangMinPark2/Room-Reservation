package com.room.reservation.system.api.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tbl_metingroom")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingRoom {
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @Column(name = "capacity", nullable = false)
    private Integer capacity;
    
    @Column(name = "half_hourly_rate", nullable = false)
    private Integer halfHourlyRate;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    public MeetingRoom(String name, Integer capacity, Integer halfHourlyRate) {
        this.name = name;
        this.capacity = capacity;
        this.halfHourlyRate = halfHourlyRate;
    }
} 