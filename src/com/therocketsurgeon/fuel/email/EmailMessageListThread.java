package com.therocketsurgeon.fuel.email;

import javax.mail.Message;

import org.apache.log4j.Logger;

import com.therocketsurgeon.fuel.text.TextUtil;

public class EmailMessageListThread implements Runnable {

	public static Logger log = Logger.getLogger(EmailMessageListThread.class.getName());
	
	private Message[] messages;
	
	public EmailMessageListThread(Message[] messages) {
		this.messages = messages;
	}
	
	@Override
	public void run() {
		try {
			log.info("Started thread");
			int count = 0;
			for (Message message : this.messages) {
				message.getSubject();
				message.getSentDate();
				count++;
				// log.info(TextUtil.concat(null != message.getSubject() ? message.getSubject() : "'no subject'", ", sent ", message.getSentDate().toString()));
			}
			log.info("Finished thread: " + count);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		} finally {
			//
		}
	}

}
