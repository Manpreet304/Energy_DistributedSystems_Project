package com.example.currentpercentageservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

public interface DatabaseRepository extends JpaRepository<CurrentPercentageEntity, LocalDateTime> {

}
