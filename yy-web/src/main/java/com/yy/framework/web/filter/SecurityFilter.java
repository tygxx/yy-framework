package com.yy.framework.web.filter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 服务安全验证过滤器
 * @author mateng@eversec.cn
 *
 */
public class SecurityFilter implements Filter {
	
	private final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);
	
	private Map<String, String> apps = new HashMap<>();
	
	public SecurityFilter(String appId, String appSecret) {
		this.apps.put(appId, appSecret);
	}
	
	public SecurityFilter(Map<String, String> apps) {
		this.apps.putAll(apps);
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String servletPath = request.getServletPath();
		
		logger.debug("request url:" + servletPath);
		
		String appId = request.getHeader("appId");
		String timestamp = request.getHeader("timestamp");
		String signature = request.getHeader("signature");
		String appSecret = this.apps.get(appId);
		
		if(StringUtils.isBlank(appId) || StringUtils.isBlank(signature) || StringUtils.isBlank(timestamp)) {
			response.setStatus(401);
			return;
		}
		
		if(!checkSignature(appId, appSecret, signature, timestamp)) {
			response.setStatus(401);
			return;
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}
	
	private boolean checkSignature(String appId, String appSecret, String signature, String timestamp) {
		List<String> key = new ArrayList<String>();
		key.add(appId);
		key.add(appSecret);
		key.add(timestamp);
		Collections.sort(key);
		String tempStr = key.get(0) + key.get(1) + key.get(2);
		tempStr = DigestUtils.sha1Hex(tempStr);
		return tempStr.equals(signature);
	}

}
