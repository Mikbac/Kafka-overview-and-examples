package com.example.libraryconsumer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

/**
 * Created by MikBac on 26.12.2022
 */

@Configuration
@EnableKafka
@Profile("local")
public class KafkaConsumerConfiguration {

//    @Bean
//    public StringJsonMessageConverter jsonConverter() {
//        return new StringJsonMessageConverter();
//    }

}
