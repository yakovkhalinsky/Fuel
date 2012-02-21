package com.therocketsurgeon.fuel.example;

import com.therocketsurgeon.fuel.crypto.Cryptomatic;

public class ExampleCrypto {

	public static void main(String[] args) {

		try {
			Cryptomatic crypto = new Cryptomatic();
			String str = crypto.encrypt("password");
			System.out.println(str);
		} catch (Exception ex) {
			 ex.printStackTrace();
		} finally {
			//
		}
		
	}

}
