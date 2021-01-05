package com.hien.billing.api.component.transaction.service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hien.base.JsonSerializer;
import com.hien.base.log.LogHelpers;
import com.hien.base.log.LogObj;
import com.hien.billing.api.component.account.Account;
import com.hien.billing.api.infra.gateway.SmsGateway;

@Service
public class SendSmsService {

	@Value("${kafka.sendSmsTopic}")
	private String sendSmsTopic;
	@Autowired
	private KafkaProducer<String, String> sendSmsProducer;
	@Autowired
	private KafkaConsumer<String, String> sendSmsConsumer;
	@Autowired
	private SmsGateway smsGateway;

	public void sendOtpAsync(String otp, Account account) {
		String msg = String.format("You are purchasing something. This is your otp: %s", otp);

		Map<String, Object> data = new LinkedHashMap<>();
		data.put("phoneNumber", account.phoneNumber);
		data.put("msg", msg);

		sendSmsProducer.send(new ProducerRecord<String, String>(sendSmsTopic, account.accountNumber, JsonSerializer.object2Json(data)));
	}

	@PostConstruct
	private void consumeSendSmsTopic() {
		new Thread(() -> {
			while (true) {
				try {
					ConsumerRecords<String, String> records = sendSmsConsumer.poll(Duration.ofSeconds(5));
					for (ConsumerRecord<String, String> record : records) {
						LogObj log = new LogObj("consumeSendSmsEvent");
						log.debug("event", record.value());

						Map<String, Object> data = JsonSerializer.json2Map(record.value(), Object.class);
						smsGateway.sendSms((String) data.get("phoneNumber"), (String) data.get("msg"));

						LogHelpers.info(log);
					}
					sendSmsConsumer.commitAsync();
				} catch (Throwable tr) {
					LogHelpers.error(tr);
					// TODO: send failed event to failure topic to resend or do something else
				}
			}
		}).start();
	}
}
