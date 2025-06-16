package com.example.currentpercentageservice.service;

import com.example.currentpercentageservice.repository.CurrentPercentageEntity;
import com.example.currentpercentageservice.repository.DatabaseRepository;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CurrentPercentageService {
    private final DatabaseRepository repository;

    public CurrentPercentageService(DatabaseRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "current_percentage_mq")
    public void enterCurrentPercentageInDB(String json){
        System.out.println("Listening to current_percentage_mq");

        try {
            JSONObject obj = new JSONObject(json);

            Date hour = Date.from(
                    java.time.Instant.parse(obj.getString("hour")));

            double communityProduced = obj.getDouble("communityProduced");
            double communityUsed = obj.getDouble("communityUsed");
            double gridUsed = obj.getDouble("gridUsed");

            double grid_portion = (communityUsed / (communityUsed + gridUsed)) * 100;
            double grid_depleted = (communityUsed / communityProduced) * 100;

            CurrentPercentageEntity percentageEntity = new CurrentPercentageEntity();
            percentageEntity.setHour(hour);
            percentageEntity.setCommunityDepleted(grid_depleted);
            percentageEntity.setGridPortion(grid_portion);
            repository.save(percentageEntity);
            System.out.println(percentageEntity.toString() + " is saved in DB");

        } catch (Exception e) {
            System.out.println("Error while parsing incoming JSON in enterCurrentPercentageInDB");
        }

    }
}
