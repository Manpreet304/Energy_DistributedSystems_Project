package com.example.restapi.controller;


import com.example.restapi.dto.TotalEnergyBetweenDates;
import com.example.restapi.repository.CurrentPercentageDB;
import com.example.restapi.repository.CurrentPercentageEntity;
import com.example.restapi.repository.EnergyDB;
import com.example.restapi.repository.EnergyDataEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    CurrentPercentageDB currentPercentageDbRepository;
    EnergyDB energyDbRepository;

    public EnergyController(CurrentPercentageDB currentPercentageDbRepository, EnergyDB energyDbRepository) {
        this.currentPercentageDbRepository = currentPercentageDbRepository;
        this.energyDbRepository = energyDbRepository;
    }

    @GetMapping("/current")
    public List<CurrentPercentageEntity> getCurrentData() {

        LocalDateTime dateTime = LocalDateTime.now();
        dateTime = dateTime.truncatedTo(ChronoUnit.HOURS);

        Date hour = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());



        CurrentPercentageEntity last = currentPercentageDbRepository.findByHour(hour);

        List<CurrentPercentageEntity> result = new ArrayList<>();
        if (last != null) {
            result.add(last);
        }
        for (CurrentPercentageEntity r:result
             ) {
            System.out.println(r.toString());
        }
        System.out.println(result.toString());
        return result;
    }


    @GetMapping("/historical")
    public TotalEnergyBetweenDates getHistoricalData(
        @RequestParam("dateStart")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date dateStart,

        @RequestParam("dateEnd")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date dateEnd) {

        double produced = energyDbRepository.selectCommunityProducedTotals();
        double used = energyDbRepository.selectCommunityUsedTotals();
        double grid = energyDbRepository.selectGridUsedTotals();

        TotalEnergyBetweenDates energyData = new TotalEnergyBetweenDates(produced, used, grid);
        System.out.println(energyData.toString());

        return energyData;
    }



}
