package pl.mikbac.streamsexample;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by MikBac on 17.05.2025
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class SampleService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage() {
        kafkaTemplate.send("sample-input-topic", UUID.randomUUID().toString(), "Hello World");
    }

    @KafkaListener(
            topics = "sample-output-topic",
            autoStartup = "true",
            groupId = "sample-listener-group")
    public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        LOGGER.info("ConsumerRecord -> {} ", consumerRecord);
    }
}
