package com.example.restapi.controller;

import com.example.restapi.dto.TotalEnergyBetweenDates;
import com.example.restapi.repository.CurrentPercentageDB;
import com.example.restapi.repository.CurrentPercentageEntity;
import com.example.restapi.repository.EnergyDB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnergyControllerTest {

    @Mock
    private CurrentPercentageDB currentPercentageDbRepository;

    @Mock
    private EnergyDB energyDbRepository;

    @InjectMocks
    private EnergyController controller;

    @Test
    void getCurrentData_returnsEntityFromRepository() {
        // Arrange: genau jene volle Stunde, die der Controller auch nimmt
        LocalDateTime nowHour = LocalDateTime.now()
                .truncatedTo(ChronoUnit.HOURS);
        Date hour = Date.from(nowHour.atZone(ZoneId.systemDefault()).toInstant());

        CurrentPercentageEntity entity = new CurrentPercentageEntity();
        entity.setHour(hour);
        entity.setCommunityDepleted(100.0);
        entity.setGridPortion(20.0);

        // Stub exakt mit diesem hour-Datum
        when(currentPercentageDbRepository.findByHour(hour))
                .thenReturn(entity);

        // Act
        CurrentPercentageEntity result = controller.getCurrentData();

        // Assert: Werte aus dem Stub-Entity
        assertEquals(100.0, result.getCommunityDepleted());
        assertEquals(20.0,  result.getGridPortion());

        // Verify: Methode wurde genau mit hour aufgerufen
        verify(currentPercentageDbRepository).findByHour(hour);
    }

    @Test
    void getCurrentData_returnsDefaultWhenNoEntry() {
        // Arrange: gleiche Stunde
        LocalDateTime nowHour = LocalDateTime.now()
                .truncatedTo(ChronoUnit.HOURS);
        Date hour = Date.from(nowHour.atZone(ZoneId.systemDefault()).toInstant());

        // Stub gibt null zurück
        when(currentPercentageDbRepository.findByHour(hour))
                .thenReturn(null);

        // Act
        CurrentPercentageEntity result = controller.getCurrentData();

        // Assert: Default-Entity (0-Werte) mit korrektem hour
        assertEquals(hour,  result.getHour());
        assertEquals(0.0,   result.getCommunityDepleted());
        assertEquals(0.0,   result.getGridPortion());

        // Verify
        verify(currentPercentageDbRepository).findByHour(hour);
    }


    @Test
    void testGetHistoricalData() throws Exception {
        // 1) Dummy-Werte festlegen
        double produced = 12.34;
        double used     =  5.67;
        double grid     =  6.67;


        when(energyDbRepository.selectCommunityProducedTotals()).thenReturn(produced);
        when(energyDbRepository.selectCommunityUsedTotals())   .thenReturn(used);
        when(energyDbRepository.selectGridUsedTotals())        .thenReturn(grid);


        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date start = fmt.parse("2025-06-19T12:00:00");
        Date end   = fmt.parse("2025-06-19T13:00:00");


        TotalEnergyBetweenDates result = controller.getHistoricalData(start, end);


        assertEquals(produced, result.getTotalCommunityProduced(), "Produced stimmt nicht");
        assertEquals(used,     result.getTotalCommunityUsed(),     "Used stimmt nicht");
        assertEquals(grid,     result.getTotalGridUsed(),     "Grid stimmt nicht");


        verify(energyDbRepository).selectCommunityProducedTotals();
        verify(energyDbRepository).selectCommunityUsedTotals();
        verify(energyDbRepository).selectGridUsedTotals();
}
}