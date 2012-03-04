package com.therocketsurgeon.fuel.crypto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;

import com.therocketsurgeon.fuel.FuelProperty;
import com.therocketsurgeon.fuel.Property;
import com.therocketsurgeon.fuel.text.TextUtil;

/**
 * Class for doing some simple crypto operations.<br/>
 * This class depends on two lines to be present in a property file in the classpath:<br/>
 * - com.therocketsurgeon.fuel.crypto.Cryptomatic.salt<br/>
 * - com.therocketsurgeon.fuel.crypto.Cryptomatic.secret
 * @author Yakov Khalinsky
 */
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

	/**
	 * Default constructor. This looks for a salt and secret strings in a properties file to be used for subsequent hashing, one and two way encryption methods.
	 * @throws IllegalArgumentException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 * @throws InvalidKeyException 
	 */
	public Cryptomatic() throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalArgumentException {
		this(Property.getProperty(BUNDLE, PROPERTY_SALT), Property.getProperty(BUNDLE, PROPERTY_SECRET));
	}
	
	/**
	 * Constructor. Allows for salt and passphrase to be set in the arguments.
	 * @param salt
	 * @param passphrase
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 * @throws InvalidKeyException 
	 */
	public Cryptomatic(String salt, String passphrase) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		if (!TextUtil.isValid(salt) || !TextUtil.isValid(passphrase)) {
			throw new IllegalArgumentException("Invalid arguments");
		}
		this.SALT = salt;
		this.PASSPHRASE = passphrase;
		initialise();
	}
	
	/**
	 * Set the encryption algorithm to use in subsequent encryption or hashing methods.
	 * @param algorithm
	 */
	public void setAlgorithm(String algorithm) {
		if (!TextUtil.isValid(algorithm)) {
			throw new IllegalArgumentException("Invalid argument");
		}
		this.ALGORITHM = algorithm;
	}
	
	private void initialise() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		KeySpec keySpec = null;
		SecretKey key = null;
		AlgorithmParameterSpec paramSpec = null;
		try {
            // Create the key
            keySpec = new PBEKeySpec(this.PASSPHRASE.toCharArray(), this.SALT.getBytes(), this.iterations);
            key = SecretKeyFactory.getInstance(this.ALGORITHM).generateSecret(keySpec);
            
            this.ENCRYPTER = Cipher.getInstance(key.getAlgorithm());
            this.DECRYPTER = Cipher.getInstance(key.getAlgorithm());

            // Prepare the parameter to the ciphers
            paramSpec = new PBEParameterSpec(this.SALT.getBytes(), this.iterations);

            // Create the ciphers
            this.ENCRYPTER.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            this.DECRYPTER.init(Cipher.DECRYPT_MODE, key, paramSpec);
        } finally {
        	keySpec = null;
    		key = null;
    		paramSpec = null;
        }
	}
	
	/**
	 * Encrypts the string. Reversable by calling decrypt().
	 * @param string
	 * @return encrypted string
	 * @throws UnsupportedEncodingException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public String encrypt(String string) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
		if (!TextUtil.isValid(string)) {
			throw new IllegalArgumentException("Invalid argument");
		}
		byte[] utf8 = null;
		byte[] enc = null;
		try {
			utf8 = string.getBytes("UTF8");
			
			// Encrypt
			enc = this.ENCRYPTER.doFinal(utf8);
			
			// Encode bytes to base64 to get a string
			return new sun.misc.BASE64Encoder().encode(enc);
		} finally {
			utf8 = null;
			enc = null;
		}
    }

	/**
	 * Decrypts the string into its "clear" form as provided in the argument of the encrypt() method.
	 * @param string
	 * @return encrypted string
	 * @throws IOException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
    public String decrypt(String string) throws IOException, IllegalBlockSizeException, BadPaddingException {
    	if (!TextUtil.isValid(string)) {
			throw new IllegalArgumentException("Invalid argument");
		}
    	byte[] dec = null;
    	byte[] utf8 = null;
    	try {
    		// Decode base64 to get bytes
    		dec = new sun.misc.BASE64Decoder().decodeBuffer(string);
    		
    		// Decrypt
    		utf8 = this.DECRYPTER.doFinal(dec);
    		
    		// Decode using utf-8
    		return new String(utf8, "UTF8");
    	}
    	finally {
    		dec = null;
        	utf8 = null;
    	}
    }

    /**
     * Generate a random string of length 32 characters.
     * @return String
     */
    public static String getRandomString() {
    	return new BigInteger(130, RANDOM).toString(32);
    }
    
    /**
     * Returns an encoded String from the arguments using the HmacSHA1 algorithm.
     * @param key
     * @param data
     * @return encoded String
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     */
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
    
    /**
     * Get MD5 hashed value of String data
     * @param data
     * @return hashValue
     * @throws NoSuchAlgorithmException
     */
    public static String hashMD5(String data) throws NoSuchAlgorithmException {
    	return hash(data, ALGORITHM_MD5);
    }

    /**
     * Get SHA1 hashed value of String data
     * @param data
     * @return hashValue
     * @throws NoSuchAlgorithmException
     */
    public static String hashSHA1(String data) throws NoSuchAlgorithmException {
    	return hash(data, ALGORITHM_SHA1);
    }
    
    private static String hash(String data, String algorithm) throws NoSuchAlgorithmException {
    	if (!TextUtil.isValid(data)) {
			throw new IllegalArgumentException("Invalid argument");
		}
    	StringBuffer hashValue = new StringBuffer(4);
    	MessageDigest md = null;
    	String hex = null;
    	try {
    		md = MessageDigest.getInstance(algorithm);
    		byte[] out = md.digest(data.getBytes());
    		for (int i=0; i < out.length; i++) {
    			hex = Integer.toHexString(0xff & out[i]);
    		    if(hex.length() == 1) {
    		    	hashValue.append('0');
    		    }
    		    hashValue.append(hex);
    		}
    	} finally {
    		md = null;
    		hex = null;
    	}
    	return hashValue.toString();
    }
    
}

