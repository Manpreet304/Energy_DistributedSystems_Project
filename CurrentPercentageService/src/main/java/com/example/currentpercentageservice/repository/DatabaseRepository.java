package com.example.currentpercentageservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface DatabaseRepository extends JpaRepository<CurrentPercentageEntity, LocalDateTime> {

}
