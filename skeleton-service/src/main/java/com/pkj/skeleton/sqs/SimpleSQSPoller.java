package com.pkj.skeleton.sqs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.pkj.skeleton.utils.serialization.JsonSerializer;

public class SimpleSQSPoller {

	private static final Logger logger = Logger.getLogger(SimpleSQSPoller.class);

	private SQSQueue sqsQueue;
	private long pollingInterval = 250;
	private long retryInterval = 4000;
	private int pollerThreads = 1;
	private List<SQSListener> listeners = new ArrayList<>();

	private ExecutorService executor;

	public SimpleSQSPoller(SQSQueue sqsQueue) {
		super();
		this.sqsQueue = sqsQueue;
	}

	public SimpleSQSPoller(SQSQueue sqsQueue, long pollingInterval, long retryInterval, int pollerThreads,
			List<SQSListener> listeners) {
		super();
		this.sqsQueue = sqsQueue;
		this.pollingInterval = pollingInterval;
		this.retryInterval = retryInterval;
		this.pollerThreads = pollerThreads;
		this.listeners = listeners;
	}

	@PostConstruct
	private void postInit() {
		logger.info("starting sqs poller for the queue: " + sqsQueue);
		notifyInit();
	}

	public void notifyInit() {
		logger.info("Bean init event fired. Submitting poller threads to background executor.");
		executor = Executors.newFixedThreadPool(pollerThreads,
				new ThreadFactoryBuilder().setNameFormat("sample-sqs-poller-thread-%d").build());
		ListenerTask task = new ListenerTask();
		for (int i = 0; i < pollerThreads; i++) {
			executor.execute(task);
		}
	}

	public void subscribe(SQSListener listener) {
		logger.info("Adding SQS listener: " + listener.getClass());
		listeners.add(listener);
	}

	private void startListening() {
		logger.debug("Starting listening to alert messages.");
		while (true) {
			try {
				logger.info(String.format("Sleeping for %d ms before polling.", pollingInterval));
				if (pollingInterval > 0) {
					Thread.sleep(pollingInterval);
				}
				logger.info("Polling sqs queue for messages.");
				Message message = receiveMessage();
				if (logger.isDebugEnabled()) {
					logger.debug("Notifying listeners with message: " + JsonSerializer.serialize(message));
				}
				notifyListeners(message);
			} catch (InterruptedException e) {
				logger.error("Recharge Success publisher was inturrepted. Exiting.", e);
				return;
			} catch (Exception e) {
				logger.error("Some error occurred in simple sqs poller. Restarting it.", e);
			}
		}
	}

	private Message receiveMessage() throws InterruptedException {
		Message message = null;
		while (message == null) {
			logger.info("Receving messages from SQS.");
			try {
				message = sqsQueue.receiveMessage();
			} finally {
				if (message != null) {
					logger.info("Deleting message from the sqs queue.");
					sqsQueue.deleteMessage(message);
				}
			}
			if (message == null) {
				logger.debug(String.format("Did not get any message from sqs. Retrying after %d ms.", retryInterval));
				Thread.sleep(retryInterval);
			} else {
				logger.info("Received message from sqs queue.");
				if (logger.isDebugEnabled()) {
					logger.debug(JsonSerializer.serialize(message));
				}
			}
		}
		return message;
	}

	private void notifyListeners(Message message) throws JsonParseException, JsonMappingException, IOException {
		logger.info("Received one message from sqs queue.");
		if (message != null) {
			logger.info("Notifying listeners");
			for (SQSListener listener : listeners) {
				try {
					logger.info("Notifying: " + listener.getClass());
					listener.notifyForMsg(message);
					logger.info("Notified: " + listener.getClass());
				} catch (Exception e) {
					logger.error("Error while notifying listener: " + listener.getClass(), e);
				}
			}
		}
	}

	private class ListenerTask implements Runnable {
		@Override
		public void run() {
			logger.info("Started listening for recharge success events, in a poller thread.");
			startListening();
		}

	}

}
