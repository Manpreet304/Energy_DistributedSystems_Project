package com.example.energyproducer.weather_api;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class WeatherService {

    @Value("${weather.api.base-url}")
    private String baseUrl;

    @Value("${weather.api.latitude}")
    private double latitude;

    @Value("${weather.api.longitude}")
    private double longitude;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Ruft das JSON komplett als String ab, parst es mit JSONObject
     * und liefert den letzten (also aktuellen) shortwave_radiation–Wert.
     */
    public double fetchCurrentRadiation() {
        String url = baseUrl +
                "?latitude="  + latitude +
                "&longitude=" + longitude +
                "&hourly=shortwave_radiation" +
                "&timezone=auto";                 // wichtig: lokale Zeit!

        String json = restTemplate.getForObject(url, String.class);

        JSONObject hourly = new JSONObject(json).getJSONObject("hourly");
        JSONArray times   = hourly.getJSONArray("time");
        JSONArray rad     = hourly.getJSONArray("shortwave_radiation");

        String now = LocalDateTime.now()
                .withMinute(0).withSecond(0).withNano(0) // auf volle Stunde
                .toString();                              // Format yyyy-MM-ddTHH:mm

        for (int i = 0; i < times.length(); i++) {
            if (times.getString(i).startsWith(now)) {   // Zeitstempel passt
                return rad.getDouble(i);
            }
        }
        return 0; // Fallback
    }

}
