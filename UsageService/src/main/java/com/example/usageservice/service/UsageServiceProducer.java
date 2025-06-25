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

@Service
public class UsageServiceProducer {


    private final DatabaseRepository repository;
    private final RabbitTemplate rabbit;

    public UsageServiceProducer(DatabaseRepository repository, RabbitTemplate rabbit) {
        this.repository = repository;
        this.rabbit = rabbit;
    }

    @RabbitListener(queues = "producer_mq")
    public void enterProducerDataInDB(String json){
        try {
            JSONObject obj = new JSONObject(json);
            double kwh = obj.getDouble("kwh");
            System.out.println("Producer kwh: " + kwh);
            LocalDateTime dateTime = LocalDateTime.parse(obj.getString("datetime"));

            // 2) Auf volle Stunde runden
            LocalDateTime truncated = dateTime.truncatedTo(ChronoUnit.HOURS); //.toInstant kannn man hier schon einzesetzen, evtl überarbeiten
            Date hour = Date.from(truncated.atZone(ZoneId.systemDefault()).toInstant());

            // 3) DB-Eintrag holen oder neu erstellen
            EnergyDataEntity entry = repository.findByHour(hour);
            if (entry != null) {
                System.out.println("Producer entry.getCommunityProduced() = " + entry.getCommunityProduced());
                // 4) Produktionswert erhöhen
                entry.setCommunityProduced(entry.getCommunityProduced() + kwh);
                System.out.println("Producer after inserting new entry in DB entry.getCommunityProduced() = " + entry.getCommunityProduced());

                // 5) Speichern (Insert oder Update)
                repository.save(entry);
                System.out.println(entry.toString() + " from Producer saved in DB");
                JSONObject msg = new JSONObject()
                        .put("hour", entry.getHour().toInstant())
                        .put("communityProduced", entry.getCommunityProduced())
                        .put("communityUsed", entry.getCommunityUsed())
                        .put("gridUsed", entry.getGridUsed());

                rabbit.convertAndSend("current_percentage_mq", msg.toString());
                System.out.println(msg.toString() + "from UsageServiceProducer sent to current_percentage_mq");
            } else {
                entry = new EnergyDataEntity();
                entry.setHour(hour);
                entry.setCommunityProduced(kwh);
                entry.setCommunityUsed(0);
                entry.setGridUsed(0);
                repository.save(entry);
            }





        }catch (Exception e){
            System.err.println("could not extract Data from json: " + json);
            e.printStackTrace();
        }



    }
}
