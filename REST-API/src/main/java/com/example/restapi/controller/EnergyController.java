package com.example.restapi.controller;


import com.example.restapi.dto.TotalEnergyBetweenDates;
import com.example.restapi.repository.CurrentPercentageDB;
import com.example.restapi.repository.CurrentPercentageEntity;
import com.example.restapi.repository.EnergyDB;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;


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
    public CurrentPercentageEntity getCurrentData() {

        LocalDateTime dateTime = LocalDateTime.now();
         dateTime = dateTime.truncatedTo(ChronoUnit.HOURS);

        Date hour = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());



        CurrentPercentageEntity current = currentPercentageDbRepository.findByHour(hour);

       if (current == null) {
           current = new CurrentPercentageEntity();      // no-arg-Ctor muss existieren
           current.setHour(hour);
           current.setCommunityDepleted(0.0);
           current.setGridPortion(0.0);
       }

       return current;
    }


    @GetMapping("/historical")
    public TotalEnergyBetweenDates getHistoricalData(
        @RequestParam("dateStart")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date dateStart,

        @RequestParam("dateEnd")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date dateEnd) {

        double produced = energyDbRepository.selectCommunityProducedTotals(dateStart, dateEnd);
        double used = energyDbRepository.selectCommunityUsedTotals(dateStart, dateEnd);
        double grid = energyDbRepository.selectGridUsedTotals(dateStart, dateEnd);

        TotalEnergyBetweenDates energyData = new TotalEnergyBetweenDates(produced, used, grid);
        System.out.println(energyData.toString());

        return energyData;
    }



}
