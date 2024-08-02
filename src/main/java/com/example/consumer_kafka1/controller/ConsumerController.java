package com.example.consumer_kafka1.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class ConsumerController {
    private final Counter enrollNowCounter;
    private final Counter pricingInfoCounter;
    private final Counter contactFormCounter;
    private final JsonParser jsonParser;

    public ConsumerController(MeterRegistry meterRegistry) {
        this.enrollNowCounter = meterRegistry.counter("kafka_enroll_now_clicks_counter");
        this.pricingInfoCounter = meterRegistry.counter("kafka_pricing_info_requests_counter");
        this.contactFormCounter = meterRegistry.counter("kafka_contact_form_submissions_counter");
        this.jsonParser = new JsonParser();

    }

    @KafkaListener(topics = "tasty", groupId = "metrics-consumer-group")
    public void listen(String eventData) {
        System.out.println("Recived data : " + eventData);
        try {
            JsonObject json = jsonParser.parse(eventData).getAsJsonObject();
            String event = json.get("event").getAsString();
            switch (event) {
                case "enrollNow":
                    enrollNowCounter.increment();
                    break;
                case "pricingInfoRequested":
                    pricingInfoCounter.increment();
                    break;
                case "contactInquirySent":
                    contactFormCounter.increment();
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

    }

}
