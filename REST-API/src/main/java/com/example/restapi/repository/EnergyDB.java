package com.example.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface EnergyDB extends JpaRepository<EnergyDataEntity, LocalDateTime> {
    List<EnergyDataEntity> findByHourBetween(Date start, Date end);
}
