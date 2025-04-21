package com.example.demo.controller;

import com.example.demo.dto.CommunityData;
import com.example.demo.dto.EnergyData;
import com.example.demo.dto.Type;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final List<EnergyData> storedData = new ArrayList<>();

    @PostConstruct
    public void init() {
        storedData.addAll(generateData());
    }

    @PostMapping("/message")
    public void receiveMessage(@RequestBody List<CommunityData> communityDataList) {
        for (CommunityData communityData : communityDataList) {
            Calendar messageTime = Calendar.getInstance();
            messageTime.setTime(communityData.getDatetime());
            messageTime.set(Calendar.MINUTE, 0);
            messageTime.set(Calendar.SECOND, 0);
            messageTime.set(Calendar.MILLISECOND, 0);
            Date messageHour = messageTime.getTime();


            EnergyData newEntry = null;

            if (!storedData.isEmpty()) {
                EnergyData lastEntry = storedData.get(storedData.size() - 1);

                Calendar lastCal = Calendar.getInstance();
                lastCal.setTime(lastEntry.getHour());
                lastCal.set(Calendar.MINUTE, 0);
                lastCal.set(Calendar.SECOND, 0);
                lastCal.set(Calendar.MILLISECOND, 0);

                if (lastCal.getTime().equals(messageHour)) {
                    newEntry = lastEntry;
                }
            }


            if (newEntry == null) {
                newEntry = new EnergyData(messageHour, 0, 0, 0);
                storedData.add(newEntry);
            }

            double kwh = communityData.getKwh();

            if (communityData.getType().equals("PRODUCER")) {
                newEntry.setCommunity_produced(newEntry.getCommunity_produced() + kwh);
            } else if (communityData.getType().equals("USER")) {
                double newCommunityUsed = newEntry.getCommunity_used() + kwh;
                double communityAvailable = newEntry.getCommunity_produced();

                if (newCommunityUsed <= communityAvailable) {

                    newEntry.setCommunity_used(newCommunityUsed);
                } else {

                    double usedFromCommunity = communityAvailable - newEntry.getCommunity_used();
                    double usedFromGrid = kwh - usedFromCommunity;

                    newEntry.setCommunity_used(communityAvailable);
                    newEntry.setGrid_used(newEntry.getGrid_used() + usedFromGrid);
                }
            }
        }
    }


    @GetMapping("/current")
    public List<EnergyData> getCurrentData() {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.HOUR, -1);
        Date oneHourAgo = cal.getTime();

        List<EnergyData> currentData = new ArrayList<>();
        for (EnergyData d : storedData) {
            if (d.getHour().after(oneHourAgo)) {
                currentData.add(d);
            }
        }

        return currentData;
    }

    @GetMapping("/historical")
    public List<EnergyData> getHistoricalData(
            @RequestParam("dateStart")
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date dateStart,

            @RequestParam("dateEnd")
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date dateEnd) {

        List<EnergyData> result = new ArrayList<>();

        for (EnergyData d : storedData) {
            boolean afterStart = !d.getHour().before(dateStart);
            boolean beforeEnd = !d.getHour().after(dateEnd);

            if (afterStart && beforeEnd) {
                result.add(d);
            }
        }

        return result;
    }


    private List<EnergyData> generateData() {
        List<EnergyData> dataList = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            dataList.add(new EnergyData(sdf.parse("2025-04-03T15:00:00"), 24.1, 23.7, 1.8));
            dataList.add(new EnergyData(sdf.parse("2025-04-05T14:00:00"), 22.4, 22.4, 0.0));
            dataList.add(new EnergyData(sdf.parse("2025-04-10T13:00:00"), 20.0, 21.2, 2.0));
            dataList.add(new EnergyData(sdf.parse("2025-04-21T17:00:00"), 18.5, 17.9, 1.1));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }
}
