package com.example.energyproducer;

import com.example.energyproducer.weather_api.WeatherService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnergyProducerTest {

    @Mock
    RabbitTemplate rabbit;

    @Mock
    WeatherService weatherService;

    @InjectMocks
    EnergyProducer producer;

    @Test
    void startProducer_sendsValidJsonPayload() throws JSONException {
        // Arrange
        when(weatherService.fetchCurrentRadiation()).thenReturn(0.8);

        // Act
        producer.startProducer();

        // Capture both arguments: 1) Queue-Name, 2) JSON-String
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(rabbit).convertAndSend(captor.capture(), captor.capture());

        List<String> captured = captor.getAllValues();
        String queueName = captured.get(0);
        String jsonPayload = captured.get(1);

        // Assert: richtige Queue
        assertThat(queueName).isEqualTo("producer_mq");

        // Assert: JSON-Felder
        JSONObject obj = new JSONObject(jsonPayload);
        assertThat(obj.getString("type")).isEqualTo("PRODUCER");
        assertThat(obj.getString("association")).isEqualTo("COMMUNITY");

        double kwh = obj.getDouble("kwh");
        // plausibler Bereich
        assertThat(kwh).isGreaterThanOrEqualTo(0.005);
        assertThat(kwh).isLessThanOrEqualTo(0.100);

        // Timestamp-Feld vorhanden
        assertThat(obj.has("datetime")).isTrue();
    }
}
