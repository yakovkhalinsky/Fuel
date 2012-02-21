package com.therocketsurgeon.fuel;

import java.util.ResourceBundle;

public class Property implements FuelProperty {

	public static String getProperty(String bundleName, String propertyName)
			throws IllegalArgumentException {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
			if (!bundle.containsKey(propertyName)) {
				throw new IllegalArgumentException("Invalid propertyName");
			}
			return bundle.getString(propertyName);
		} catch (IllegalArgumentException iae) {
			throw iae;
		}
	}

	
}
