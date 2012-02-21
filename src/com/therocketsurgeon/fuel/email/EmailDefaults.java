package com.therocketsurgeon.fuel.email;

import java.util.HashMap;

public abstract class EmailDefaults {

    public static HashMap<String, Object> DEFAULTS_GMAIL;
    public static HashMap<String, Object> DEFAULTS_IMAP;
    
    public static final String PROPERTY_NAME_HOST = "mail.imap.host";
    public static final String PROPERTY_NAME_PORT = "mail.imap.port";
    public static final String PROPERTY_NAME_CONNECTION_TIMEOUT = "mail.imap.connectiontimeout";
    public static final String PROPERTY_NAME_TIMEOUT = "mail.imap.timeout";
    public static final String PROPERTY_NAME_PROTOCOL = "mail.store.protocol";
    
    static {
	DEFAULTS_GMAIL = new HashMap<String, Object>();
	DEFAULTS_GMAIL.put(PROPERTY_NAME_HOST, "imap.gmail.com");
	DEFAULTS_GMAIL.put(PROPERTY_NAME_PORT, 993);
	DEFAULTS_GMAIL.put(PROPERTY_NAME_PROTOCOL, "imaps");
    }
    
}
