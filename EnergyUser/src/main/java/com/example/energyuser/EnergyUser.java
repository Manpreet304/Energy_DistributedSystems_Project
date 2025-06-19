package com.example.energyuser;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EnergyUser {
    private final RabbitTemplate rabbit;


    private static final Random RANDOM = new Random();

    public EnergyUser(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    private static double randomKwh() {
        double min = 0.001;
        double max = 0.060;
        double value = min + (max - min) * RANDOM.nextDouble();
        return Math.round(value * 1000.0) / 1000.0; // round to 3 decimals
    }


    public void sendMessage(String message) {
        rabbit.convertAndSend("user_mq", message);
    }


    // das muss noch als jsonobj gemacht werden
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void startUser() {
        String json = String.format(
                Locale.US,
                "{\"type\":\"USER\",\"association\":\"COMMUNITY\",\"kwh\":%.3f,\"datetime\":\"%s\"}",
                randomKwh(),
                LocalDateTime.now());
        System.out.println(json);
        sendMessage(json);
    }
}
