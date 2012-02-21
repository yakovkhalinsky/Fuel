package com.therocketsurgeon.fuel.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.therocketsurgeon.fuel.Property;

public class EmailUtil {

	public static void sendEmail(String from, String to, String subject, String body, boolean isHtml) throws MessagingException {
		Properties props = null;
		Session session = null;
		Message message = null;
		InternetAddress addressFrom = null;
		InternetAddress[] addressTo = null;
		try {
			props = new Properties();
			props.put("mail.smtp.host", Property.getProperty(Property.BUNDLE, Property.PROPERTY_SMTP));
			
			session = Session.getDefaultInstance(props, null);
			message = new MimeMessage(session);
			
			// set the from and to address
			addressFrom = new InternetAddress(from);
			message.setFrom(addressFrom);
			
			addressTo = new InternetAddress[1]; 
			addressTo[0] = new InternetAddress(to);
			message.setRecipients(Message.RecipientType.TO, addressTo);
			
			message.setSubject(subject);
			if (!isHtml) {
				message.setContent(body, "text/plain");
			} else {
				message.setContent(body, "text/html");
			}
			Transport.send(message);
		} finally {
			props = null;
			session = null;
			message = null;
			addressFrom = null;
			addressTo = null;
		}
	}
	
}
