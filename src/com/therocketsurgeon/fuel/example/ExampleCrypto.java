package com.therocketsurgeon.fuel.example;

import com.therocketsurgeon.fuel.crypto.Cryptomatic;

public class ExampleCrypto {

	public static void main(String[] args) {

		try {
//			Cryptomatic crypto = new Cryptomatic();
//			String str = crypto.encrypt("password");
//			System.out.println(str);
			
//			String randomString = Cryptomatic.getRandomString(); 
//			System.out.print(randomString);
			
			// String hashValue = Cryptomatic.hashMD5("hash this String");
			String hashValue = Cryptomatic.hashSHA1("hash this String");
			System.out.println(hashValue);
		} catch (Exception ex) {
			 ex.printStackTrace();
		} finally {
			//
		}
		
	}

}
