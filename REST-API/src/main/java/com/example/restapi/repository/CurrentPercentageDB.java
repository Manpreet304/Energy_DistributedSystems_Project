package com.example.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

public interface CurrentPercentageDB extends JpaRepository<CurrentPercentageEntity, LocalDateTime> {
    CurrentPercentageEntity findByHour(Date hour);
}
