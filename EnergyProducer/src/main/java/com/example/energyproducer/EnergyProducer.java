package com.example.energyproducer;

import com.example.energyproducer.weather_api.WeatherService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EnergyProducer {

    private final RabbitTemplate rabbit;
    private static final double MAX_RADIATION = 1000.0;


    @Autowired
    private WeatherService weatherService;


    private static final Random RANDOM = new Random();

    public EnergyProducer(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    private static double randomKwh() {
        double min = 0.001;
        double max = 0.010;
        double value = min + (max - min) * RANDOM.nextDouble();
        return Math.round(value * 1000.0) / 1000.0; // round to 3 decimals
    }
    private static int randomIntervall(){
        int min = 1000;
        int max = 5000;
        return RANDOM.nextInt(max - min + 1) + min;

    }

    public void sendMessage(String message) {
        rabbit.convertAndSend("producer_mq", message);
    }


    //Prof meinte hier ich soll noch ein jsonObject draus machen
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void startProducer() {

            // 1. Aktuelle Solarstrahlung holen
        double radiation = weatherService.fetchCurrentRadiation();

        // 2. Basis-kWh (wie bisher) und Skalierungsfaktor berechnen
        double baseKwh      = randomKwh();
        double productionFactor = radiation / MAX_RADIATION;
        double adjustedKwh  = baseKwh * productionFactor;

        // 3. JSON mit skalierten kWh
        String json = String.format(
                Locale.US,
                "{\"type\":\"PRODUCER\",\"association\":\"COMMUNITY\",\"kwh\":%.3f,\"datetime\":\"%s\"}",
                adjustedKwh,
                LocalDateTime.now()
        );
        System.out.println(json);
        sendMessage(json);


    }
}
