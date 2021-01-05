package com.hien.eshopping.api.infra.kafka;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendSmsKafkaConfig {

	@Value("${kafka.appId}")
	private String applicationId;
	@Value("${kafka.endpoint}")
	private String endpoint;
	@Value("${kafka.sendSmsTopic}")
	private String sendSmsTopic;

	@PostConstruct
	public void createTopic() {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, endpoint);
		properties.put("connections.max.idle.ms", 10000);
		properties.put("request.timeout.ms", 5000);
		try (AdminClient client = AdminClient.create(properties)) {
			try {
				CreateTopicsResult result = client.createTopics(Arrays.asList(
						new NewTopic(sendSmsTopic, 1, (short) 1)));
				result.all().get();
			} catch (TopicExistsException e) {
				// do nothing
			} catch (InterruptedException | ExecutionException e) {
				// do nothing
			}
		}
	}

	@Bean("sendSmsProducer")
	public KafkaProducer<String, String> sendSmsProducer() {
		Properties props = new Properties();

		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, endpoint);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, applicationId);
		props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 500);
		props.put(ProducerConfig.RETRIES_CONFIG, 3);

		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass().getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass().getName());

		return new KafkaProducer<>(props);
	}

	@Bean("sendSmsConsumer")
	public KafkaConsumer<String, String> sendSmsConsumer() {
		Properties props = new Properties();

		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, endpoint);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, applicationId);
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, applicationId);
		props.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, 500);

		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 1000 * 10); // 10s
		props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 1000 * 60 * 5); // 5 min
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);

		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.String().deserializer().getClass().getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Serdes.String().deserializer().getClass().getName());

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList(sendSmsTopic));

		return consumer;
	}
}
