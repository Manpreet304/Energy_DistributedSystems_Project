package com.example.usageservice.service;

import com.example.usageservice.repository.DatabaseRepository;
import com.example.usageservice.repository.EnergyDataEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsageServiceProducerTest {

    @Mock
    DatabaseRepository repository;

    @Mock
    RabbitTemplate rabbit;

    @InjectMocks
    UsageServiceProducer service;

    String sampleJson =
            "{\"type\":\"PRODUCER\",\"association\":\"COMMUNITY\"," +
                    "\"kwh\":0.007,\"datetime\":\"2025-06-17T12:00:00\"}";

    @Test
    void enterProducerDataInDB_updatesExistingEntry() {
        // 1) Arrange: gleiche Stunde wie in Deinem JSON
        LocalDateTime dt      = LocalDateTime.parse("2025-06-17T12:00:00");
        Date hour             = Date.from(dt.truncatedTo(ChronoUnit.HOURS)
                .atZone(ZoneId.systemDefault())
                .toInstant());
        EnergyDataEntity entry = new EnergyDataEntity();
        entry.setHour(hour);
        entry.setCommunityProduced(1.0);

        // 2) Stub nur noch direkt das Entity, kein Optional mehr!
        when(repository.findByHour(hour)).thenReturn(entry);

        // 3) Act
        service.enterProducerDataInDB(sampleJson);

        // 4) Assert: der Wert wurde um 0.007 erhöht
        assertEquals(1.007, entry.getCommunityProduced(), 1e-6);

        // 5) verify, dass gespeichert wurde
        verify(repository).save(entry);
    }
}
