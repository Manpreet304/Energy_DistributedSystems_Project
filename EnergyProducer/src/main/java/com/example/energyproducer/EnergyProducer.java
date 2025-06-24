package com.example.energyproducer;

import com.example.energyproducer.weather_api.WeatherService;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EnergyProducer {

    private final RabbitTemplate rabbit;
    private static final double MAX_RADIATION = 1000.0;


    private final WeatherService weatherService;


    private static final Random RANDOM = new Random();

    public EnergyProducer(RabbitTemplate rabbit, WeatherService weatherService) {
        this.rabbit = rabbit;
        this.weatherService = weatherService; //Konstruktor erweitert, damit der Test funktioniert mit InjectMockito
    }

    public static double randomKwh() {
        double min = 0.005;
        double max = 0.100;
        double value = min + (max - min) * RANDOM.nextDouble();
        return Math.round(value * 1000.0) / 1000.0; // round to 3 decimals
    }

    public void sendMessage(String message) {
        rabbit.convertAndSend("producer_mq", message);
    }



    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void startProducer() {

            // 1. Aktuelle Solarstrahlung holen
        double radiation = weatherService.fetchCurrentRadiation();

        // 2. Basis-kWh (wie bisher) und Skalierungsfaktor berechnen
        double baseKwh      = randomKwh();
        double productionFactor = radiation / MAX_RADIATION;
        double adjustedKwh  = baseKwh * productionFactor;


        System.out.println("baseKwh: " + baseKwh);
        System.out.println("radiation: " + radiation);
        System.out.println("productionFactor: " + productionFactor);
        System.out.println("adjustedKwH: " + adjustedKwh);

        JSONObject json = new JSONObject()
                .put("type", "PRODUCER")
                .put("association", "COMMUNITY")
                .put("kwh", adjustedKwh)
                .put("datetime", LocalDateTime.now());
        System.out.println(json);
        sendMessage(json.toString());


    }
}
