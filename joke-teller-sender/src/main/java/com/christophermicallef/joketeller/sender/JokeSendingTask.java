package com.christophermicallef.joketeller.sender;

import com.christophermicallef.joketeller.services.entity.JokeHistory;
import com.christophermicallef.joketeller.services.entity.Subscriber;
import com.christophermicallef.joketeller.services.service.JokeTellerService;
import com.christophermicallef.joketeller.services.service.SubscriberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JokeSendingTask {

	private static final Logger logger = LoggerFactory.getLogger(JokeSendingTask.class);
	private final SubscriberService subscriberService;
	private final JokeTellerService jokeTellerService;
	private final KafkaTemplate<String, JokeMessage> kafkaTemplate;
	private final String topic;
	private final int pageSize;

	public JokeSendingTask(SubscriberService subscriberService,
			JokeTellerService jokeTellerService,
			KafkaTemplate<String, JokeMessage> kafkaTemplate,
			@Value("${sender.kafka.topic:jokes}") String topic,
			@Value("${sender.page-size:100}") int pageSize) {
		this.subscriberService = subscriberService;
		this.jokeTellerService = jokeTellerService;
		this.kafkaTemplate = kafkaTemplate;
		this.topic = topic;
		this.pageSize = pageSize;
	}

	@Scheduled(cron = "${sender.schedule.cron:0 0 0 * * *}")
	public void sendJokes() {
		JokeHistory joke = jokeTellerService.tellRandomJoke().orElse(null);
		if (joke == null) {
			logger.warn("No jokes are available; skipping subscriber notifications");
			return;
		}

		int pageNumber = 0;
		Page<Subscriber> page;
		do {
			page = subscriberService.findSubscribers(PageRequest.of(pageNumber, pageSize));
			for (Subscriber subscriber : page.getContent()) {
				JokeMessage message = new JokeMessage(subscriber.getEmail(), joke.getJoke());
				kafkaTemplate.send(topic, subscriber.getEmail(), message);
				logger.info("Queued joke message for {}", subscriber.getEmail());
			}
			pageNumber++;
		} while (!page.isLast());
	}
}
