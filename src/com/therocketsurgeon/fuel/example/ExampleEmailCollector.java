package com.therocketsurgeon.fuel.example;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.FetchProfile;
import javax.mail.FetchProfile.Item;
import javax.mail.Folder;
import javax.mail.Message;

import org.apache.log4j.Logger;

import com.therocketsurgeon.fuel.Property;
import com.therocketsurgeon.fuel.email.EmailCollector;
import com.therocketsurgeon.fuel.email.EmailMessageListThread;
import com.therocketsurgeon.fuel.text.TextUtil;

public class ExampleEmailCollector {

	public static Logger log = Logger.getLogger(ExampleEmailCollector.class.getName());
	
	public static void main(String[] args) {

		ExampleEmailCollector collector = new ExampleEmailCollector();
		collector.runMe();
		
	}

	public void runMe() {
		int maxThreads = 40;
		
		String username = null;
		String password = null;

		EmailCollector emailCollector = null;
		Folder folder = null;
		FetchProfile fp = null;
		Message[] messages = null;
		Date dt = null;
		try {
			username = Property.getProperty(Property.BUNDLE, "imap.username");
			password = Property.getProperty(Property.BUNDLE, "imap.password");

			emailCollector = new EmailCollector(true);
			emailCollector.connect(username, password);

			folder = emailCollector.getFolder("INBOX", true, Folder.READ_ONLY);
			System.out.println(TextUtil.concat("Folder has ", folder.getMessageCount(), " messages with ", folder.getUnreadMessageCount(), " unread messages"));

			fp = new FetchProfile();
			fp.add(Item.CONTENT_INFO);

//			this.messages = folder.getMessages();
//			folder.fetch(this.messages, fp);

			int messageCount = folder.getMessageCount();
			int messageBatchSize = messageCount / maxThreads;
			
			System.out.println("Message count: " + messageCount);
			System.out.println("Get in batches of: " + messageBatchSize);
			
			ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
			
			dt = new Date();
			long startTime = dt.getTime();
			
			for (int i=0; i < messageCount; i+=messageBatchSize) {
				int start = i+1;
				int end = start + messageBatchSize - 1;
				if (end > messageCount) {
					end = messageCount;
				}
				messages = folder.getMessages(start, end);
				folder.fetch(messages, fp);
				executor.execute(new EmailMessageListThread(messages));
				// executor.submit(new EmailMessageListThread(messages));
				System.out.println("Submitted batch"); 
			}
			executor.shutdown();
			while (!executor.isShutdown()) {
				// wait for executor to finish running submitted threads
				log.info("waiting");
			}
			emailCollector.closeAll(true);
			
			dt = new Date();
			long endTime = dt.getTime();
			
			log.info("Finished in: " + (endTime - startTime));
			
			System.out.println("Finished processing email batches");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			username = null;
			password = null;
			emailCollector = null;
			folder = null;
			fp = null;
			messages = null;
			dt = null; 
		}

	}

}
