package com.yy.framework.commons.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.client.HttpClientBuilder;

public interface HttpConfig {
	
	/**
	 * 定义配置httpclient的接口
	 * 该接口可以自定义配置httpclient
	 * @param httpClientBuilder
	 * @param builder
	 * @return
	 */
	RequestConfig config(HttpClientBuilder httpClientBuilder, Builder builder);

}
