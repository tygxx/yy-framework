package com.yy.framework.web.lang;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class RemoteIpUtils {
	
	private static final String UN_KNOWN = "unKnown";

	/**
	 * 获取请求的真实IP地址，不使用request.getRemoteAddr();
	 * @param request
	 * @return
	 */
	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	/**
	 * 获取请求的真实IP地址，如果地址被代理获取到多个地址时，
	 * @author mateng
	 * @param request
	 * @return
	 */
	public static String getIpAddressOnly(HttpServletRequest request) {
		String ip = getIpAddress(request);
		if(StringUtils.isNotEmpty(ip) && !UN_KNOWN.equalsIgnoreCase(ip)) {
			//多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if(index != -1){
				return ip.substring(0,index);
			}
		}
		return ip;
	}

}
