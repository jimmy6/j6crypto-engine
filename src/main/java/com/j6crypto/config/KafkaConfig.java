//package com.j6crypto.config;
//
//import com.j6crypto.to.TimeData;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
//
///**
// * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
// */
//@EnableKafka
//@Configuration
//public class KafkaConfig {
//  @Bean
//  public ConsumerFactory<String, String> consumerFactory() {
//    Map<String, Object> props = new HashMap<>();
//    props.put(GROUP_ID_CONFIG, "1");
//    props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//    props.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//    return new DefaultKafkaConsumerFactory<>(props, null, new JsonDeserializer(TimeData.class));
//  }
//
//  @Bean
//  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
//    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
//    factory.setConsumerFactory(consumerFactory());
//    return factory;
//  }
//}
