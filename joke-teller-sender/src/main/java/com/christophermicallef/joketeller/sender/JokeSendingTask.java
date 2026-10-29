package com.christophermicallef.joketeller.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JokeSendingTask {

	private static final Logger logger = LoggerFactory.getLogger(JokeSendingTask.class);

	@Scheduled(cron = "${sender.schedule.cron:0 0 0 * * *}")
	public void sendJokes() {
		logger.info("sending out jokes ....");
	}
}
