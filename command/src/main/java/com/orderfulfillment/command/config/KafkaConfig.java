package com.orderfulfillment.command.config;

import com.orderfulfillment.command.utils.Constants;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
public class KafkaConfig {
  @Bean
  public NewTopic orderEventsTopic() {
    return TopicBuilder.name(Constants.ORDER_EVENTS_TOPIC).partitions(3).replicas(1).build();
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate(
      ProducerFactory<String, Object> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

  @Bean
  public NewTopic inventoryEventsTopic() {
    return TopicBuilder.name(Constants.INVENTORY_EVENTS_TOPIC).partitions(3).replicas(1).build();
  }

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    JsonSerializer<Object> jsonSerializer = new JsonSerializer<>();
    jsonSerializer.setAddTypeInfo(true);

    return new DefaultKafkaProducerFactory<>(configProps, new StringSerializer(), jsonSerializer);
  }
}
