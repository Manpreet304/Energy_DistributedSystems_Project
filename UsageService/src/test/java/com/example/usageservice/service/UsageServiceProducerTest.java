package com.example.usageservice.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;

class UsageServiceProducerTest {

    String sampleJson =
            "{\"type\":\"PRODUCER\",\"association\":\"COMMUNITY\",\"kwh\":0.007,\"datetime\":\"2025-06-17T12:00:00\"}";


    @InjectMocks
    private UsageServiceProducer service;

    @Test
    void kwhValueIsParsedCorrectly() {
        JSONObject obj = null;
        try {
            obj = new JSONObject(sampleJson);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        double kwh = 0;
        try {
            kwh = obj.getDouble("kwh");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        assertEquals(0.007, kwh);   // 1e-6 = Toleranz
    }

}