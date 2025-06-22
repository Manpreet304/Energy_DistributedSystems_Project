package com.example.energyuser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnergyUserTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private EnergyUser energyUser;


    @Test
    void testRandomKwh() {
        for (int i = 0; i < 1000; i++) {
            double result = EnergyUser.randomKwh();
            assertTrue(result >= 0.001 && result <= 0.060, "Result out of range: " + result);
        }
    }

    @Test
    void testSendMessage() {
        String msg = "hallo-welt";
        energyUser.sendMessage(msg);
        verify(rabbitTemplate).convertAndSend("user_mq", msg);
    }

    @Test
    void testSendFixedJsonMessage() {
        String fixedJson = "{\"type\":\"USER\",\"association\":\"COMMUNITY\",\"kwh\":0.027,\"datetime\":\"2025-06-19T13:45:12\"}";

        energyUser.sendMessage(fixedJson);

        verify(rabbitTemplate)
                .convertAndSend("user_mq", fixedJson);
}
}