package com.example.restapi.repository;

import com.example.restapi.dto.TotalEnergyBetweenDates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface EnergyDB extends JpaRepository<EnergyDataEntity, LocalDateTime> {


    List<EnergyDataEntity> findByHourBetween(Date start, Date end);
    @Query(value = "SELECT " +
            "SUM(community_produced) AS total_community_produced, " +
            "SUM(community_used) AS total_community_used, " +
            "SUM(grid_used) AS total_grid_used " +
            "FROM energy_data " +
            "WHERE hour BETWEEN :start AND :end", nativeQuery = true)
    Object[] findTotalValuesBetweenDates(@Param("start") Date start, @Param("end") Date end);


    @Query(value = "SELECT SUM(community_produced) FROM energy_data", nativeQuery = true)
    Double selectCommunityProducedTotals();

    @Query(value = "SELECT SUM(community_used) FROM energy_data", nativeQuery = true)
    Double selectCommunityUsedTotals();
    @Query(value = "SELECT SUM(grid_used) FROM energy_data", nativeQuery = true)
    Double selectGridUsedTotals();



}
