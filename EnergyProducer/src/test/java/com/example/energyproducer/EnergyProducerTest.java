package com.example.energyproducer;

import com.example.energyproducer.weather_api.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnergyProducerTest {

    @Mock
    RabbitTemplate mockRabbit;

    @Mock
    WeatherService mockWeather;

    @InjectMocks
    EnergyProducer producer;

    @Test
    void sendMessage_shouldCallRabbitTemplate() {
        // Arrange
        String msg = "Hello Rabbit_MQ";

        // Act
        producer.sendMessage(msg);

        // Assert
        verify(mockRabbit).convertAndSend("producer_mq", msg);
    }

    @Test
    void startProducer_shouldSendCorrectJson() {
        // Arrange
        when(mockWeather.fetchCurrentRadiation()).thenReturn(500.0); // 50% Sonnenstrahlung

        // Act
        producer.startProducer(); // nutzt random Werte

        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockRabbit).convertAndSend(eq("producer_mq"), captor.capture());

        String json = captor.getValue();
        System.out.println("Captured JSON: " + json);

        // Grobe Strukturprüfung
        assert json.contains("\"type\":\"PRODUCER\"");
        assert json.contains("\"association\":\"COMMUNITY\"");
        assert json.contains("\"kwh\":");
        assert json.contains("\"datetime\":\"" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE).substring(0, 10));
    }
}
