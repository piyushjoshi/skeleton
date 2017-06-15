package com.pkj.skeleton.sqs;

import com.amazonaws.services.sqs.model.Message;

public interface SQSQueue {

	public void sendMessage(String message);

	public Message receiveMessage();

	public void deleteMessage(Message message);

}
