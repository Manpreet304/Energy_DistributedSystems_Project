package com.example.energyproducer;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.energyproducer.EnergyProducer;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class EnergyProducerApplication {


    @Bean
    public Queue producerQueue() {
        return new Queue("producer_mq", true);
    }



    public static void main(String[] args) {
        SpringApplication.run(EnergyProducerApplication.class, args);
    }

}
