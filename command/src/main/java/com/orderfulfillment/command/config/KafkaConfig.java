package com.orderfulfillment.command.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {
  @Bean
  public NewTopic orderEventsTopic() {
    return TopicBuilder.name("order-events").partitions(3).replicas(1).build();
  }
}
