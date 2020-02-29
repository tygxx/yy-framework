package com.yy.framework.commons.lang;

import org.apache.commons.lang.StringUtils;

/**
 * 类名称: StringUtils<br>
 * 类描述: 字符串工具类（apache StringUtils的补充）<br>
 * 修改时间: 2017年6月14日下午5:42:45<br>
 * @author mateng@eversec.cn
 */
public class StringUtil {
	
	/**
	 * 小写字母正则
	 */
	public static final String REGULAR_LOWERCASE = ".*?[a-z]+.*?";
	
	/**
	 * 大写字母正则
	 */
	public static final String REGULAR_UPPERCASE = ".*?[A-Z]+.*?";
	
	/**
	 * 数字正则
	 */
	public static final String REGULAR_DIGITALCASE = ".*?[\\d]+.*?";
	
	/**
	 * 特殊字符正则
	 */
	public static final String REGULAR_SPECIALCASE = ".*?[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]+.*?";
	
	/** 
     * 邮箱 正则
     */  
	public static final String EMAIL_REGEX="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"; 
	
	
	/**
	 * 手机号码正则
	 */
	public static final String PHONE_REGEX = "^1[3|4|5|7|8|9]\\d{9}$";
  
	/**
	 * 是否包含小写字母
	 * @param string  要检查的字符串
	 * @return 是否包含
	 */
	public static boolean containsLower(String string) {
		if(StringUtils.isBlank(string)) {
			return false;
		}
		return string.matches(REGULAR_LOWERCASE);
	}
	
	/**
	 * 是否包含大写字母
	 * @param string  要检查的字符串
	 * @return 是否包含
	 */
	public static boolean containsUpper(String string) {
		if(StringUtils.isBlank(string)) {
			return false;
		}
		return string.matches(REGULAR_UPPERCASE);
	}
	
	/**
	 * 是否包含数字
	 * @param string  要检查的字符串
	 * @return 是否包含
	 */
	public static boolean containsDigital(String string) {
		if(StringUtils.isBlank(string)) {
			return false;
		}
		return string.matches(REGULAR_DIGITALCASE);
	}
	
	/**
	 * 是否包含特殊字符
	 * @param string  要检查的字符串
	 * @return 是否包含
	 */
	public static boolean containsSpecial(String string) {
		if(StringUtils.isBlank(string)) {
			return false;
		}
		return string.matches(REGULAR_SPECIALCASE);
	}

	/**
	 * 是否为邮箱地址
	 * @param email  要检查的字符串
	 * @return 是否包含
	 */
	public static boolean isEmail(String email) {
		if(StringUtils.isBlank(email)) {
			return false;
		}
		return email.matches(EMAIL_REGEX);
	}
	
	/**
	 * 是否是手机号码
	 * @param phone  要检查的字符串
	 * @return 是否包含
	 */
	public static boolean isPhone(String phone) {
		if(StringUtils.isBlank(phone)) {
			return false;
		}
		return phone.matches(PHONE_REGEX);
	}
	
	/**
	 * 统计字符串中包含子串的个数
	 * @param str 要匹配的字符串
	 * @param c 要统计的子串
	 * @return 个数
	 */
	public static int contains(String str, String c) {
		int fromIndex = 0;
        int count = 0;
        while(true){
            int index = str.indexOf(c, fromIndex);
            if(-1 != index){
                fromIndex = index + 1;
                count++;
            }else{
                break;
            }
        }
        return count;
	}
	
	public static void main(String[] args) {
		System.out.println(isEmail("aaa@aaa.com"));
		System.out.println(isEmail("@aaa.com"));
		System.out.println(isEmail("aaa@aaa.asdf"));
		System.out.println(isEmail("a1aa123@aaa.com"));
		System.out.println(isEmail("123@aaa.com"));
		System.out.println(isEmail("aaa.com"));
		
		System.out.println(isPhone("13112345678"));
		System.out.println(isPhone("12312345678"));
		System.out.println(isPhone("19012345678"));
		System.out.println(isPhone("1311234567"));
		
		System.out.println(contains("abcdabc", "1"));
	}
}
