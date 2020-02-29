package com.yy.framework.commons.encypt;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;


public class RsaUtils {
	
	/** 默认的安全服务提供者 */
    private static final Provider DEFAULTPROVIDER = new BouncyCastleProvider();
    /** 默认的模 */
    private static final String DEFAULT_MODULUS = "108515725448342660217771118363923730296424098423847034969078109022938901475697118831097989759318452732393052579929705744189600493630065211152847021878710513721576086203801222625127634699732009258161718577442033681170556414504415796120818621764323256045115056302406986490370882680859535840925699683478370999877";
    /** 默认的公钥指数 */
    private static final String DEFAULT_PUBLIC_EXPONENT = "65537";
    /** 默认的私钥指数 */
    private static final String DEFAULT_PRIVATE_EXPONENT = "85293224427346798192438253950630013504270841784170902930057090681441455861820192947915218097899082001785722942724552577852124763536199691119603518485987074405511624271890927772587211731181688077279565695132700474576821789695077043650741408500093205755122466045931537364441758993731368248512249955139821788833";
	
	private static KeyPair defaultKeyPair = null;
	private static KeyFactory keyFactory = null;
	
	static {
		KeyPairGenerator keyPairGen;
		try {
			keyPairGen = KeyPairGenerator.getInstance("RSA");
			keyPairGen.initialize(1024);
			defaultKeyPair = keyPairGen.generateKeyPair();
			keyFactory = KeyFactory.getInstance("RSA", DEFAULTPROVIDER);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成默认的公钥和私钥
	 * @return KeyPair
	 */
    public static KeyPair getDefaultKeyPair(){
        return defaultKeyPair;
    }
    
    /**
     * 返回已初始化的默认的公钥
     * @return RSAPublicKey
     */
    public static RSAPublicKey getDefaultPublicKey() {
        return getRSAPublidKey(DEFAULT_MODULUS, DEFAULT_PUBLIC_EXPONENT);
    }
    
    /**
     * 使用模和指数生成公钥
     * @param modulus
     * @param exponent
     * @return
     */
	public static RSAPublicKey getRSAPublidKey(String modulus, String exponent) {
		BigInteger b1 = new BigInteger(modulus);
		BigInteger b2 = new BigInteger(exponent);
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
		try {
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return null;
	}
    
    /**
     * 返回已初始化的默认的私钥
     * @return RSAPrivateKey 私钥
     */
    public static RSAPrivateKey getDefaultPrivateKey() {
        return getRSAPrivateKey(DEFAULT_MODULUS, DEFAULT_PRIVATE_EXPONENT);
    }
    
    /**
     * 使用模和指数生成私钥
     * @param modulus
     * @param exponent
     * @return
     */
    public static RSAPrivateKey getRSAPrivateKey(String modulus, String exponent) {
        BigInteger b1 = new BigInteger(modulus);  
        BigInteger b2 = new BigInteger(exponent);  
        if(modulus != null && exponent != null) {
        	RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(b1, b2);
        	 try {
				return (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}  
        }
        return null;
    }
    
    /**
     * 使用指定的公钥加密数据。
     * @param publicKey 公约
     * @param str 要加密的字符串
     * @return 加密后的字符串
     * @throws Exception 异常
     */
    public static String encrypt(PublicKey publicKey, String str) throws Exception {
        Cipher ci = Cipher.getInstance("RSA", DEFAULTPROVIDER);
        ci.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] enData = ci.doFinal(str.getBytes());
        return new String(Hex.encodeHex(enData));
    }

    /**
     * 使用指定的私钥解密数据。
     * @param privateKey 私钥
     * @param str	要解密的字符串
     * @return	解密后的字符串
     * @throws Exception 异常
     */
    public static String decrypt(PrivateKey privateKey, String str) throws Exception {
    	byte[] data = Hex.decodeHex(str.toCharArray());
        Cipher ci = Cipher.getInstance("RSA", DEFAULTPROVIDER);
        ci.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(ci.doFinal(data));
    }

    public static void main(String[] args) throws Exception {
//    	KeyPair keyPair = RsaUtils.getDefaultKeyPair();
//    	RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//    	RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//    	
//    	String pbm = publicKey.getModulus().toString();
//    	String pbe = publicKey.getPublicExponent().toString();
//    	String pvm = privateKey.getModulus().toString();
//    	String pve = privateKey.getPrivateExponent().toString();
//    	
//    	System.out.println("pbm:" + pbm);
//    	System.out.println("pbe:" + pbe);
//    	System.out.println("pvm:" + pvm);
//    	System.out.println("pve:" + pve);
    	
    	
    	RSAPublicKey publicKey = RsaUtils.getDefaultPublicKey();
    	RSAPrivateKey privateKey = RsaUtils.getDefaultPrivateKey();
    	String aaa = "123456";
    	String bbb = RsaUtils.encrypt(publicKey, aaa);
    	System.out.println(bbb);
    	
    	String ccc = RsaUtils.decrypt(privateKey, bbb);
    	System.out.println(new String(ccc));
    	
//    	String modulus = new String(Hex.encodeHex(publicKey.getModulus().toByteArray()));
//    	String exponent = new String(Hex.encodeHex(publicKey.getPublicExponent().toByteArray()));
//    	publicKey = RsaUtils.getRSAPublidKey(modulus, exponent);
//    	ccc = RsaUtils.encrypt(publicKey, aaa);
//    	System.out.println(new String(ccc));
    }
}

