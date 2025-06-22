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
    void testGetCurrentData() {
        // 1) Ein einzelnes Entity erzeugen und mit Werten befüllen
        CurrentPercentageEntity entity = new CurrentPercentageEntity();
        entity.setHour(new Date());
        entity.setCommunityDepleted(100.0);
        entity.setGridPortion(20.0);

        // 2) Verhalten des Mocks festlegen
        when(currentPercentageDbRepository.findTopByOrderByHourDesc())
                .thenReturn(entity);

        // 3) Controller-Methode aufrufen
        List<CurrentPercentageEntity> result = controller.getCurrentData();

        // 4) Rückgabe prüfen
        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getCommunityDepleted());
        assertEquals(20.0,  result.get(0).getGridPortion());

        // 5) Sicherstellen, dass das Repository aufgerufen wurde
        verify(currentPercentageDbRepository).findTopByOrderByHourDesc();
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