package com.pkj.skeleton.sqs;

import org.apache.log4j.Logger;

import com.amazonaws.services.sqs.model.Message;
import com.pkj.skeleton.utils.serialization.JsonSerializer;

public class SampleSQSListener implements SQSListener {

	private static final Logger logger = Logger.getLogger(SampleSQSListener.class);

	@Override
	public void notifyForMsg(Message m) {
		logger.info("received sqs message: " + JsonSerializer.serialize(m));
	}

}
