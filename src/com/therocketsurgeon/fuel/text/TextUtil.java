package com.therocketsurgeon.fuel.text;

import java.io.StringWriter;

import com.therocketsurgeon.fuel.FuelProperty;

public class TextUtil implements FuelProperty {

	public static boolean isValid(String value) {
		if (null == value || value.trim().length() == 0) {
			return false;
		}
		return true;
	}

	public static String concat(Object... one) {
		return stitchArray(one, "");
	}
	
	public static String stitchArray(Object[] array, String delimiter) {
		StringWriter writer = new StringWriter(4);
		for (int i=0; i < array.length; i++) {
			if (array[i] instanceof String) {
				writer.write((String)array[i]);
			} else {
				writer.write(String.valueOf(array[i]));
			}
			if (i < array.length - 1) {
				writer.write(delimiter);
			}
		}
		return writer.toString();
	}
	
	public static boolean areAllBlankOrNull(String[] strings) {
		return evalStringArray(strings, TEST_ALL_INVALID);
	}

	private static boolean evalStringArray(String[] strings, int type) {
		if (null == strings || strings.length == 0) {
			throw new IllegalArgumentException(
					"Invalid String[] argument: must not be null and must contain rows");
		}
		int stringsLength = strings.length;
		int count = 0;
		for (int i = 0; i < stringsLength; i++) {
			if (null == strings[i] || strings[i].trim().length() == 0) {
				count++;
			}
		}
		switch (type) {
		case TEST_SOME_INVALID: // pickup if any values are invalid
			if (count > 0) {
				return true;
			}
			break;
		case TEST_ALL_INVALID: // pickup if all have invalid values
			if (count == stringsLength) {
				return true;
			}
		}
		return false;
	}

	
}
