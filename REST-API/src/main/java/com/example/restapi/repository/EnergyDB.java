package com.example.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Date;

public interface EnergyDB extends JpaRepository<EnergyDataEntity, Long> {

    @Query(value =
            "SELECT COALESCE(SUM(community_produced),0) " +
                    "  FROM energy_data " +
                    " WHERE hour BETWEEN ?1 AND ?2",
            nativeQuery = true
    )
    Double selectCommunityProducedTotals(Date date1, Date date2);

    @Query(value =
            "SELECT COALESCE(SUM(community_used),0) " +
                    "  FROM energy_data " +
                    " WHERE hour BETWEEN ?1 AND ?2",
            nativeQuery = true
    )
    Double selectCommunityUsedTotals(Date date1, Date date2);

    @Query(value =
            "SELECT COALESCE(SUM(grid_used),0) " +
                    "  FROM energy_data " +
                    " WHERE hour BETWEEN ?1 AND ?2",
            nativeQuery = true
    )
    Double selectGridUsedTotals(Date date1, Date date2);

}

