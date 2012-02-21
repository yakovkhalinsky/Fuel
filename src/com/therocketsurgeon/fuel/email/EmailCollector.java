package com.therocketsurgeon.fuel.email;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.log4j.Logger;

public class EmailCollector {

	public static Logger log = Logger.getLogger(EmailCollector.class.getName());

	private Session session;
	private Store store;
	private Folder folder;

	private String protocol;
	private String host;
	private int port;
	private int connectionTimeout;
	private int timeout;

	public EmailCollector() {
		this(false);
	}
	
	public EmailCollector(boolean gmailDefaults) {
		this.initialise();
		if (gmailDefaults) {
			this.host = (String) EmailDefaults.DEFAULTS_GMAIL.get(EmailDefaults.PROPERTY_NAME_HOST);
			this.port = (Integer) EmailDefaults.DEFAULTS_GMAIL.get(EmailDefaults.PROPERTY_NAME_PORT);
			this.protocol = (String) EmailDefaults.DEFAULTS_GMAIL.get(EmailDefaults.PROPERTY_NAME_PROTOCOL);
		}
	}

	public void connect(String username, String password) throws MessagingException {
		this.session = Session.getDefaultInstance(this.getProperties(), null);
		this.store = session.getStore(this.protocol);
		this.store.connect(this.host, username, password);
	}

	private Properties getProperties() {
		Properties properties = System.getProperties();
		properties.setProperty("mail.imap.host", this.host);
		properties.setProperty("mail.imap.port", String.valueOf(this.port));
		if (this.connectionTimeout > 0) {
			properties.setProperty("mail.imap.connectiontimeout",
					String.valueOf(this.connectionTimeout));
		}
		if (this.timeout > 0) {
			properties.setProperty("mail.imap.timeout",
					String.valueOf(this.timeout));
		}
		properties.setProperty("mail.store.protocol", this.protocol);
		return properties;
	}

	public void closeAll(boolean expunge) throws MessagingException {
		if (null != this.folder && this.folder.isOpen()) {
			this.folder.close(expunge);
		}
		if (null != this.store && this.store.isConnected()) {
			this.store.close();
		}
		this.session = null;
	}

	public Folder getFolder(String folderName, boolean open, int mode) throws MessagingException {
		this.folder = this.store.getFolder(folderName);
		if (this.folder.exists()) {
			this.folder.open(mode);
		}
		return this.folder;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	private void initialise() {
		this.session = null;
		this.store = null;
		this.folder = null;
		this.protocol = null;
		this.host = null;
		this.port = 143;
		this.connectionTimeout = 0;
		this.timeout = 0;
	}

}
