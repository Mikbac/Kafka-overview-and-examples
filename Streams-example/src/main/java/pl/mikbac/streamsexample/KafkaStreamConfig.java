package pl.mikbac.streamsexample;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Created by MikBac on 17.05.2025
 */

@Configuration
@EnableKafkaStreams
public class KafkaStreamConfig {

    @Bean
    public NewTopic sampleInputTopic() {
        return TopicBuilder.name("sample-input-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic sampleOutputTopic() {
        return TopicBuilder.name("sample-output-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public KStream<String, String> streamProcessing(StreamsBuilder builder) {
        KStream<String, String> stream = builder.stream("sample-input-topic", Consumed.with(Serdes.String(), Serdes.String()));

        // mapValues <- update value
        // map <- update key and value
        stream.map((key, value) -> KeyValue.pair(key.toUpperCase(), value.toUpperCase()))
                .to("sample-output-topic", Produced.with(Serdes.String(), Serdes.String()));
        return stream;
    }

}
