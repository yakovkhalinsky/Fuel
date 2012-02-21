package com.therocketsurgeon.fuel.crypto;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;
import sun.security.provider.MD5;

import com.therocketsurgeon.fuel.FuelProperty;
import com.therocketsurgeon.fuel.Property;
import com.therocketsurgeon.fuel.text.TextUtil;

public class Cryptomatic implements FuelProperty {

	public static Logger log = Logger.getLogger(Cryptomatic.class.getName());
	
	private String SALT = null;
	private String PASSPHRASE = null;
	
	private String ALGORITHM = ALGORITHM_MD5_SHA1_RC2_40;
	
	private Cipher ENCRYPTER = null;
	private Cipher DECRYPTER = null;
	
	private int iterations = 20;
	
	public static final String PROPERTY_SALT = Cryptomatic.class.getName() + ".salt";
	public static final String PROPERTY_SECRET = Cryptomatic.class.getName() + ".secret";
	
	private static SecureRandom RANDOM = new SecureRandom();
	
	public Cryptomatic() {
		this(Property.getProperty(BUNDLE, PROPERTY_SALT), Property.getProperty(BUNDLE, PROPERTY_SECRET));
	}
	
	public Cryptomatic(String salt, String passphrase) {
		if (!TextUtil.isValid(salt) || !TextUtil.isValid(passphrase)) {
			throw new IllegalArgumentException("Invalid arguments");
		}
		this.SALT = salt;
		this.PASSPHRASE = passphrase;
		initialise();
	}
	
	public void setAlgorithm(String algorithm) {
		if (!TextUtil.isValid(algorithm)) {
			throw new IllegalArgumentException("Invalid argument");
		}
		this.ALGORITHM = algorithm;
	}
	
	private void initialise() {
		try {
            // Create the key
            KeySpec keySpec = new PBEKeySpec(this.PASSPHRASE.toCharArray(), this.SALT.getBytes(), this.iterations);
            SecretKey key = SecretKeyFactory.getInstance(this.ALGORITHM).generateSecret(keySpec);
            
            this.ENCRYPTER = Cipher.getInstance(key.getAlgorithm());
            this.DECRYPTER = Cipher.getInstance(key.getAlgorithm());

            // Prepare the parameter to the ciphers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(this.SALT.getBytes(), this.iterations);

            // Create the ciphers
            this.ENCRYPTER.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            this.DECRYPTER.init(Cipher.DECRYPT_MODE, key, paramSpec);
        } catch (java.security.InvalidAlgorithmParameterException e) {
        	log.error(e.getMessage(), e);
        } catch (java.security.spec.InvalidKeySpecException e) {
        	log.error(e.getMessage(), e);
        } catch (javax.crypto.NoSuchPaddingException e) {
        	log.error(e.getMessage(), e);
        } catch (java.security.NoSuchAlgorithmException e) {
        	log.error(e.getMessage(), e);
        } catch (java.security.InvalidKeyException e) {
        	log.error(e.getMessage(), e);
        }
	}
	
	public String encrypt(String string) {
		if (!TextUtil.isValid(string)) {
			throw new IllegalArgumentException("Invalid argument");
		}
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = string.getBytes("UTF8");

            // Encrypt
            byte[] enc = this.ENCRYPTER.doFinal(utf8);

            // Encode bytes to base64 to get a string
            return new sun.misc.BASE64Encoder().encode(enc);
        } catch (javax.crypto.BadPaddingException e) {
        	log.error(e.getMessage(), e);
        } catch (IllegalBlockSizeException e) {
        	log.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
        	log.error(e.getMessage(), e);
        }
        return null;
    }

    public String decrypt(String string) {
    	if (!TextUtil.isValid(string)) {
			throw new IllegalArgumentException("Invalid argument");
		}
        try {
            // Decode base64 to get bytes
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(string);

            // Decrypt
            byte[] utf8 = this.DECRYPTER.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, "UTF8");
        } catch (javax.crypto.BadPaddingException e) {
        	log.error(e.getMessage(), e);
        } catch (IllegalBlockSizeException e) {
        	log.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
        	log.error(e.getMessage(), e);
        } catch (java.io.IOException e) {
        	log.error(e.getMessage(), e);
        }
        return null;
    }

    public static String getRandomString() {
    	return new BigInteger(130, RANDOM).toString(32);
    }
    
    public static String getHmacSHA1(String key, String data) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException { 
    	Mac mac = null;
    	BASE64Encoder encoder = null;
    	try {
    		mac = Mac.getInstance("HmacSHA1");
    		mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA1"));
    		encoder = new BASE64Encoder();
    		return encoder.encode(mac.doFinal(data.getBytes("UTF-8")));
    	} finally {
    		mac = null; 
    		encoder = null; 
    	}
    }
    
}

