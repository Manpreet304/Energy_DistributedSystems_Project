package com.example.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CurrentPercentageDB extends JpaRepository<CurrentPercentageEntity, LocalDateTime> {
    CurrentPercentageEntity findTopByOrderByHourDesc();
}
