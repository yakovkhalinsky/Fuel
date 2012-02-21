package com.therocketsurgeon.fuel;

public interface FuelProperty {
	
	public static final String BUNDLE = "com_therocketsurgeon_fuel";
	
	public static final String PROPERTY_SMTP = BUNDLE + ".smtp";
	
	public static final int TEST_SOME_INVALID = 1;
	public static final int TEST_ALL_INVALID = 2;
	
	public static final String ALGORITHM_MD5_DES = "PBEWithMD5AndDES";
	public static final String ALGORITHM_MD5_SHA1_RC2_40 = "PBEWithSHA1AndRC2_40";
	public static final String ALGORITHM_MD5_TRIPLEDES = "PBEWithMD5AndTripleDES";
	
}
