package com.example.usageservice.service;

import com.example.usageservice.repository.DatabaseRepository;
import com.example.usageservice.repository.EnergyDataEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsageServiceUserTest {

    @Mock
    DatabaseRepository repository;

    @InjectMocks
    UsageServiceUser service;

    JSONObject obj;

    {
        try {
            obj = new JSONObject()
                    .put("type",        "USER")
                    .put("association","COMMUNITY")
                    .put("kwh",         0.005)
                    .put("datetime",    "2025-06-24T14:00:00");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    String sampleJson = obj.toString();

    @Test
    void enterUserDataInDB_updatesExistingEntry() {
        // Arrange: Stunde aus JSON
        LocalDateTime dt = LocalDateTime.parse("2025-06-24T14:00:00");
        Date hour = Date.from(dt
                .truncatedTo(java.time.temporal.ChronoUnit.HOURS)
                .atZone(ZoneId.systemDefault())
                .toInstant()
        );
        EnergyDataEntity entry = new EnergyDataEntity();
        entry.setHour(hour);
        entry.setCommunityUsed(1.0);

        // Neuer Stub: direkt Entity zurückliefern (kein Optional mehr)
        when(repository.findByHour(hour)).thenReturn(entry);

        // Act
        service.enterUserDataInDB(sampleJson);

        // Assert: Verbrauch um 0.005 erhöht
        assertEquals(1.005, entry.getCommunityUsed(), 1e-6);

        // Verify: gespeichert wurde
        verify(repository).save(entry);
    }
}
