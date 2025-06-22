package com.example.usageservice.service;

import com.example.usageservice.repository.DatabaseRepository;
import com.example.usageservice.repository.EnergyDataEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsageServiceUserTest {

    @Mock
    DatabaseRepository repository;

    @Mock
    RabbitTemplate rabbit;

    @InjectMocks
    UsageServiceUser service;

    @Test
    void enterUserDataInDB_shouldSaveNewEntry() throws JSONException {
        // Arrange
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        Date hour = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

        String json = new JSONObject()
                .put("kwh", 0.2)
                .put("datetime", now.toString())
                .toString();

        when(repository.findByHour(hour)).thenReturn(Optional.empty());

        // Act
        service.enterUserDataInDB(json);

        // Assert
        verify(repository).save(any()); // Nur Datenbank-Speicherung prüfen
        // kein convertAndSend() hier, weil return im Code den Versand verhindert
    }


    @Test
    void enterUserDataInDB_shouldUpdateExistingEntry() throws JSONException {
        // Arrange
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        Date hour = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

        EnergyDataEntity entity = new EnergyDataEntity();
        entity.setHour(hour);
        entity.setCommunityProduced(0.3);
        entity.setCommunityUsed(0.1);
        entity.setGridUsed(0.0);

        String json = new JSONObject()
                .put("kwh", 0.1)
                .put("datetime", now.toString())
                .toString();

        when(repository.findByHour(hour)).thenReturn(Optional.of(entity));

        // Act
        service.enterUserDataInDB(json);

        // Assert
        verify(repository).save(entity);
        verify(rabbit).convertAndSend(eq("current_percentage_mq"), anyString());
    }
}
