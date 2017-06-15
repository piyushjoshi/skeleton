package com.pkj.skeleton.sqs;

import com.amazonaws.services.sqs.model.Message;

public interface SQSListener {

	public void notifyForMsg(Message m);

}
