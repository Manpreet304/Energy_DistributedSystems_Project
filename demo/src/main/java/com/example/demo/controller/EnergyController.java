package com.example.demo.controller;

import com.example.demo.dto.Community;
import com.example.demo.dto.Type;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    // Platzhalter für aktuelle Daten
    @GetMapping("/current")
    public Map<String, Object> getCurrentData() {
        Map<String, Object> current = new HashMap<>();
        current.put("hour", "2025-04-09T15:00:00");
        current.put("community_depleted", 100.0);
        current.put("grid_portion", 8.45);
        return current;
    }

    // Platzhalter für historische Daten
    @GetMapping("/historical")
    public List<Map<String, Object>> getHistoricalData(
            @RequestParam String start,
            @RequestParam String end
    ) {
        List<Map<String, Object>> historical = new ArrayList<>();

        historical.add(Map.of(
                "hour", "2025-04-09T12:00:00",
                "community_produced", 18.5,
                "community_used", 17.9,
                "grid_used", 1.1
        ));

        historical.add(Map.of(
                "hour", "2025-04-09T13:00:00",
                "community_produced", 20.0,
                "community_used", 21.2,
                "grid_used", 2.0
        ));

        historical.add(Map.of(
                "hour", "2025-04-09T14:00:00",
                "community_produced", 22.4,
                "community_used", 22.4,
                "grid_used", 0.0
        ));

        return historical;
    }
}
