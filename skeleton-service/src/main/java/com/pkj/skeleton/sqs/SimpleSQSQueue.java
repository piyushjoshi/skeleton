package com.pkj.skeleton.sqs;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

/**
 * @author piyushkumar
 *
 */
public class SimpleSQSQueue implements SQSQueue {

	private AmazonSQS sqs;
	private String queueUrl;
	private String accessKey;
	private String secretKey;

	private static final Logger logger = Logger.getLogger(SimpleSQSQueue.class);

	public SimpleSQSQueue(String queueUrl, String accessKey, String secretKey) {
		this.queueUrl = queueUrl;
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		logger.debug("Creating AwsSqsClient.");
		logger.debug(String.format("accessKey: %s, secretKey: %s, queueUrl: %s", accessKey, secretKey, queueUrl));
		if (StringUtils.isEmpty(this.accessKey) || StringUtils.isEmpty(this.secretKey)) {
			logger.debug("Accesskey and secret keys are empty. Creating SQS client without them.");
			this.sqs = new AmazonSQSClient();
		} else {
			logger.debug("Creating sqs client with accesskey and secretkey.");
			AWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
			this.sqs = new AmazonSQSClient(awsCredentials);
		}
	}

	@Override
	public void sendMessage(String message) {
		logger.debug("Creating SendMessageRequest with queueUrl: " + queueUrl);
		SendMessageRequest sendMessageRequest = new SendMessageRequest();
		sendMessageRequest.setQueueUrl(queueUrl);
		sendMessageRequest.setMessageBody(message);
		logger.debug("Sending Message: " + message);
		SendMessageResult sendMessageResult = this.sqs.sendMessage(sendMessageRequest);
		logger.debug("SendMessageResult: " + sendMessageResult);
	}

	@Override
	public Message receiveMessage() {
		logger.info("Creating ReceiveMessageRequest with queueURL: " + queueUrl);
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest();
		receiveMessageRequest.setQueueUrl(queueUrl);
		logger.info("Sending receiveMessageRequest to SQS queue.");
		ReceiveMessageResult receiveMessageResult = this.sqs.receiveMessage(receiveMessageRequest);
		logger.info("ReceiveMessageResult: " + receiveMessageResult);
		List<Message> receivedMessages = receiveMessageResult.getMessages();
		logger.info("Number of Messages: " + receivedMessages.size());
		if (receivedMessages.size() > 0) {
			Message m = receiveMessageResult.getMessages().get(0);
			String body = m.getBody();
			logger.info("Message body: " + body);
			return m;
		} else {
			return null;
		}
	}

	@Override
	public void deleteMessage(Message message) {
		logger.debug(String.format("Deleting message from Queue with queueURL: %s, ReceiptHandle: %s", queueUrl,
				message.getReceiptHandle()));
		DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest();
		deleteMessageRequest.setQueueUrl(queueUrl);
		deleteMessageRequest.setReceiptHandle(message.getReceiptHandle());
		this.sqs.deleteMessage(deleteMessageRequest);
	}

	public void setSqs(AmazonSQS sqs) {
		this.sqs = sqs;
	}

	public void setQueueUrl(String queueUrl) {
		this.queueUrl = queueUrl;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@Override
	public String toString() {
		return "SimpleSQSQueue [queueUrl=" + queueUrl + "]";
	}
	
}
