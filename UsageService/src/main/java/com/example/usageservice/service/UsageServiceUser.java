package com.example.usageservice.service;

import com.example.usageservice.repository.DatabaseRepository;
import com.example.usageservice.repository.EnergyDataEntity;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class UsageServiceUser {
    private final DatabaseRepository repository;
    private final RabbitTemplate rabbit;

    public UsageServiceUser(DatabaseRepository repository, RabbitTemplate rabbit) {

        this.repository = repository;
        this.rabbit = rabbit;
    }

    @RabbitListener(queues = "user_mq")
    public void enterUserDataInDB(String json){
        try {
            JSONObject obj = new JSONObject(json);
            double kwh = obj.getDouble("kwh");
            LocalDateTime dateTime = LocalDateTime.parse(obj.getString("datetime"));
            // 2) Auf volle Stunde runden
            LocalDateTime truncated = dateTime.truncatedTo(ChronoUnit.HOURS);
            Date hour = Date.from(truncated.atZone(ZoneId.systemDefault()).toInstant());

            // 3) DB-Eintrag holen oder neu erstellen
            Optional<EnergyDataEntity> optEntry = repository.findByHour(hour);
            EnergyDataEntity entry;
            if (optEntry.isPresent()) {
                entry = optEntry.get();
            } else {
                entry = new EnergyDataEntity();
                entry.setHour(hour);
                entry.setCommunityProduced(0);
                entry.setCommunityUsed(0);
                entry.setGridUsed(kwh);
                repository.save(entry);
                return;
            }

            double community_produced = entry.getCommunityProduced();
            double community_used = entry.getCommunityUsed();
            double grid_used = entry.getGridUsed();

            if(community_used + kwh >= community_produced){
                grid_used = kwh - (community_produced - community_used);
                community_used = community_produced;

            }

            entry.setCommunityUsed(community_used);
            entry.setGridUsed(grid_used);

            // 5) Speichern (Insert oder Update)
            repository.save(entry);
            System.out.println(entry.toString() + "saved in DB");

            JSONObject msg = new JSONObject()
                    .put("hour", entry.getHour().toInstant())
                    .put("communityProduced", entry.getCommunityProduced())
                    .put("communityUsed", entry.getCommunityUsed())
                    .put("gridUsed", entry.getGridUsed());

            rabbit.convertAndSend("current_percentage_mq", msg.toString());
            System.out.println(msg.toString() + " from User sent to current_percentage_mq");





        }catch (Exception e){
            System.err.println("could not extract Data from json: " + json);
            e.printStackTrace();
        }



    }
}
