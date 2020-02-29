package com.yy.framework.commons.http;

/**
 * 类名称: HttpClientProperties<br>
 * 类描述: http连接池的配置属性类<br>
 * 修改时间: 2016年12月1日上午11:08:51<br>
 * @author mateng@eversec.cn
 */
public class HttpClientProperties {
	
	/**
	 * 最大连接数
	 */
	private int maxTotal = 200;

	/**
	 * 设置每个主机地址的并发数
	 */
	private int defaultMaxPerRoute = 50;
	
	/**
	 * 创建连接的最长时间
	 */
	private int connectTimeout = 1000;
	
	/**
	 * 从连接池中获取到连接的最长时间 
	 */
	private int connectionRequestTimeout = 500;
	
	/**
	 * 数据传输的最长时间 
	 */
	private int socketTimeout = 10000;
	
	/**
	 * 是否允许重定向
	 */
	private boolean redirectEnable = true;
	
	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getDefaultMaxPerRoute() {
		return defaultMaxPerRoute;
	}

	public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
		this.defaultMaxPerRoute = defaultMaxPerRoute;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getConnectionRequestTimeout() {
		return connectionRequestTimeout;
	}

	public void setConnectionRequestTimeout(int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public boolean getRedirectEnable() {
		return redirectEnable;
	}

	public void setRedirectEnable(boolean redirectEnable) {
		this.redirectEnable = redirectEnable;
	}
}
