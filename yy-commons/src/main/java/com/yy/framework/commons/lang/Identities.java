package com.yy.framework.commons.lang;

import java.security.SecureRandom;
import java.util.UUID;

/**    
 * 类名称：Identities    <br>
 * 类描述： 封装各种生成唯一性ID算法的工具类.   <br>
 * 创建人：codemonkey    <br>
 * 创建时间：2015年4月29日 下午6:00:05    <br>
 * 修改人：mateng@eversec.cn<br>
 * @version 1.0.0      
 */
public class Identities {

	private static SecureRandom random = new SecureRandom();

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间有-分割.
	 * @return 带有分隔符的UUID
	 */
	public static String uuid() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
	 * @return 不带有分隔符的UUID
	 */
	public static String uuid2() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 使用SecureRandom随机生成Long.
	 * @return 随机数
	 */
	public static long randomLong() {
		return Math.abs(random.nextLong());
	}

//	/**
//	 * 基于Base62编码的SecureRandom随机生成bytes.
//	 */
//	public static String randomBase62(int length) {
//		byte[] randomBytes = new byte[length];
//		random.nextBytes(randomBytes);
//		return Encodes.encodeBase62(randomBytes);
//	}
}
