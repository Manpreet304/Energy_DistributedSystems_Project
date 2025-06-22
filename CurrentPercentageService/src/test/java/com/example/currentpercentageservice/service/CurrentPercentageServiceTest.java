package com.example.currentpercentageservice.service;

import com.example.currentpercentageservice.repository.DatabaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CurrentPercentageServiceTest {

    @Mock
    private DatabaseRepository repository;

    @InjectMocks
    private CurrentPercentageService service;

    @Test
    void testEnterCurrentPercentageInDB() {
        // Beispiel-JSON mit gültigen Werten
        String json = "{"
                + "\"hour\":\"2025-06-19T13:00:00Z\","
                + "\"communityProduced\":20.0,"
                + "\"communityUsed\":18.0,"
                + "\"gridUsed\":2.0"
                + "}";

        // Methode aufrufen
        service.enterCurrentPercentageInDB(json);


        // Erwartete Berechnungen
        double expectedDepleted = (18.0 / 20.0) * 100; // 90.0
        double expectedGridPortion = (18.0 / (18.0 + 2.0)) * 100; // 90.0
        Date expectedDate = Date.from(Instant.parse("2025-06-19T13:00:00Z"));


        // Überprüfen, ob das Repository mit korrekten Werten aufgerufen wurde

        //verify(repository).save(any(CurrentPercentageEntity.class));

        verify(repository).save(ArgumentMatchers.argThat(entity ->
                        expectedDate.equals(entity.getHour()) &&
                                Math.abs(entity.getCommunityDepleted() - expectedDepleted) < 0.001 &&
                                Math.abs(entity.getGridPortion() - expectedGridPortion) < 0.001
                ));


}
}