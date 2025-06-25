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

    // wenn man ohne @Value -> getter und setter definieren und Konstruktor mit this.

    @Value("${weather.api.base-url}")
    private String baseUrl;

    @Value("${weather.api.latitude}")
    private double latitude;

    @Value("${weather.api.longitude}")
    private double longitude;

    // Objekt, mit dem wir eine Anfrage ins Internet schicken können
    private final RestTemplate restTemplate = new RestTemplate();

    // Ruft das JSON komplett als String ab, parst es mit JSONObject und liefert den letzten (also aktuellen) shortwave_radiation–Wert.
    public double fetchCurrentRadiation() {
        String url = baseUrl +
                "?latitude="  + latitude +
                "&longitude=" + longitude +
                "&hourly=shortwave_radiation" +
                "&timezone=auto"; // auto: lokale Zeit!

        String jsonText = restTemplate.getForObject(url, String.class);

        JSONObject root = new JSONObject(jsonText); // komplettes  JSON
        JSONObject hourly = root.getJSONObject("hourly"); // Abschnitt mit stündlichen Werten
        JSONArray timeArray = hourly.getJSONArray("time"); // Liste mit Zeitpunkten
        JSONArray radiationArray = hourly.getJSONArray("shortwave_radiation"); // Liste mit Strahlungswerten

        String currentHour = LocalDateTime.now() //Radiationwert nur von dieser Stunde
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toString(); // Format yyyy-MM-ddTHH:mm

        // Alle Zeiten durchgehen, passenden Zeitpunkt suchen
        for (int i = 0; i < timeArray.length(); i++) {
            String time = timeArray.getString(i);
            if (time.startsWith(currentHour)) {
                // Wenn Zeit passt → passenden Strahlungswert zurückgeben
                return radiationArray.getDouble(i);
            }
        }
        return 0;
    }

}
