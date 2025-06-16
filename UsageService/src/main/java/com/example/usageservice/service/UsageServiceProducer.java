package com.example.usageservice.service;

import com.example.usageservice.repository.DatabaseRepository;
import com.example.usageservice.repository.EnergyDataEntity;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class UsageServiceProducer {


    private final DatabaseRepository repository;

    public UsageServiceProducer(DatabaseRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "producer_mq")
    public void enterProducerDataInDB(String json){
        try {
            JSONObject obj = new JSONObject(json);
            double kwh = obj.getDouble("kwh");
            LocalDateTime dateTime = LocalDateTime.parse(obj.getString("datetime"));

            // 2) Auf volle Stunde runden
            LocalDateTime truncated = dateTime.truncatedTo(ChronoUnit.HOURS); //.toInstant kannn man hier schon einzesetzen, evtl überarbeiten
            Date hour = Date.from(truncated.atZone(ZoneId.systemDefault()).toInstant());

            // 3) DB-Eintrag holen oder neu erstellen
            Optional<EnergyDataEntity> optEntry = repository.findByHour(hour);
            EnergyDataEntity entry;
            if (optEntry.isPresent()) {
                entry = optEntry.get();
            } else {
                entry = new EnergyDataEntity();
                entry.setHour(hour);
                entry.setCommunityProduced(kwh);
                entry.setCommunityUsed(0);
                entry.setGridUsed(0);
                repository.save(entry);
                return;
            }
            // 4) Produktionswert erhöhen
            entry.setCommunityProduced(entry.getCommunityProduced() + kwh);

            // 5) Speichern (Insert oder Update)
            repository.save(entry);
            System.out.println(entry.toString() + " from Producer saved in DB");




        }catch (Exception e){
            System.err.println("could not extract Data from json: " + json);
            e.printStackTrace();
        }



    }
}
