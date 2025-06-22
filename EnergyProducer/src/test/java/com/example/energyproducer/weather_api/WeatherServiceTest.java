package com.example.energyproducer.weather_api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private RestTemplate mockRestTemplate;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void fetchCurrentRadiation_returnsExpectedRadiationValue() {
        // Arrange
        String fixedTime = java.time.LocalDateTime.now()
                .withMinute(0).withSecond(0).withNano(0)
                .toString();

        String mockedJson = """
            {
              "hourly": {
                "time": ["%s", "2025-01-01T15:00"],
                "shortwave_radiation": [123.4, 140.0]
              }
            }
        """.formatted(fixedTime);

        when(mockRestTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockedJson);

        // Act
        double radiation = weatherService.fetchCurrentRadiation();

        // Assert
        assertThat(radiation).isEqualTo(123.4);
    }
}
