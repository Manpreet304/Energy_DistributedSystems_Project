package com.example.energyproducer.weather_api;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
                "&hourly=shortwave_radiation";

        // JSON-Antwort als String
        String json = restTemplate.getForObject(url, String.class);

        // Parsen mit org.json (kein neues Mapping)
        JSONObject root = new JSONObject(json);
        JSONObject hourly = root.getJSONObject("hourly");
        JSONArray radArray = hourly.getJSONArray("shortwave_radiation");

        return radArray.getDouble(radArray.length() - 1);
    }
}
