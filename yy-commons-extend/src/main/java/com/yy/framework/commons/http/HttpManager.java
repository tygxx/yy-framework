package com.yy.framework.commons.http;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类名称: HttpManager<br>
 * 类描述: http连接池及常用get、post的工具类<br>
 * <p>可以配合spring使用</p>
 * <p>在没有spring的情况下，启动时通过单利包装该类的初始化后使用</p>
 * 修改时间: 2016年12月1日上午11:09:07<br>
 * @author mateng@eversec.cn
 */
public class HttpManager {
	
    protected HttpClientBuilder httpClientBuilder;
    
    public static String DEFAULT_ENCODING = "UTF-8";
    
    private static Logger logger = LoggerFactory.getLogger(HttpManager.class);
    
    /**
     * 根据参数构造默认的httpmanager
     * @param properties
     */
	public HttpManager(HttpClientProperties properties) {
    	httpClientBuilder = HttpClientBuilder.create();
    	
    	SSLContext sslContext = null;
		try {
			sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
			    public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			        return true;
			    }
			}).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
		}
        httpClientBuilder.setSSLContext(sslContext);
        
        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();
    	
    	PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
    	// 最大连接数
		connectionManager.setMaxTotal(properties.getMaxTotal());
		// 设置每个主机地址的并发数
		connectionManager.setDefaultMaxPerRoute(properties.getDefaultMaxPerRoute());
		httpClientBuilder.setConnectionManager(connectionManager);
		
		Builder builder = RequestConfig.custom();
		// 创建连接的最长时间
		builder.setConnectTimeout(properties.getConnectTimeout());
		// 从连接池中获取到连接的最长时间 
		builder.setConnectionRequestTimeout(properties.getConnectionRequestTimeout());
		// 数据传输的最长时间 
		builder.setSocketTimeout(properties.getSocketTimeout());
		// 是否允许重定向
		builder.setRedirectsEnabled(properties.getRedirectEnable());
		RequestConfig config = builder.build();
		httpClientBuilder.setDefaultRequestConfig(config);
    }
	
	/**
	 * 自定义构造httpmanager <br>
	 * 使用方法参见该类底部的main方法
	 * @param httpConfig
	 */
	public HttpManager(HttpConfig httpConfig) {
		httpClientBuilder = HttpClientBuilder.create();
		Builder builder = RequestConfig.custom();
		RequestConfig config = httpConfig.config(httpClientBuilder, builder);
		httpClientBuilder.setDefaultRequestConfig(config);
    }
  
    /**
     * 获取httpclient
     * @return
     */
	public CloseableHttpClient getHttpClient() {
		return httpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy()).build();
	}
	
	/**
	 * 发送post请求
	 * @param url 请求地址
	 * @return String
	 */
	public String postRequest(String url) {
		return this.postRequest(url, null, "");
	}
	
	/**
	 * 发送post请求
	 * @param url		请求地址
	 * @param head		请求头
	 * @param params	请求的form表单参数
	 * @return	返回String
	 */
	public String postRequest(String url, Map<String, String> head, Map<String, String> params) {
		CloseableHttpResponse response = post(url, head, params);
//		if (response.getStatusLine().getStatusCode() != 200) {
//			logger.error("发送请求失败，异常码:{}", response.getStatusLine().getStatusCode());
//			throw new HttpException(response.getStatusLine().getStatusCode(), "请求异常");
//		}
		HttpEntity entity = response.getEntity();
		try {
			return EntityUtils.toString(entity, Charset.forName(DEFAULT_ENCODING));
		} catch (ParseException | IOException e) {
			throw new HttpException(501, "解析数据失败", e);
		} finally {
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 发送post请求
	 * @param url		请求地址
	 * @param head		请求头
	 * @param params	请求的http body参数
	 * @return	返回String
	 */
	public String postRequest(String url, Map<String, String> head, String params) {
		CloseableHttpResponse response = post(url, head, params);
		if (response.getStatusLine().getStatusCode() != 200) {
			logger.error("发送请求失败，异常码:{}", response.getStatusLine().getStatusCode());
			throw new HttpException(response.getStatusLine().getStatusCode(), "请求异常");
		}
		HttpEntity entity = response.getEntity();
		try {
			return EntityUtils.toString(entity, Charset.forName(DEFAULT_ENCODING));
		} catch (ParseException | IOException e) {
			throw new HttpException(501, "解析数据失败", e);
		} finally {
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 发送post请求 (需要关闭response)
	 * @param url 		请求地址
	 * @param head		请求头
	 * @param params	请求的form表单参数
	 * @return 返回HttpResponse
	 */
	public CloseableHttpResponse post(String url, Map<String, String> head, Map<String, String> params) {
		CloseableHttpClient httpClient = getHttpClient();
		HttpPost httpPost = new HttpPost(url);
		if(!MapUtils.isEmpty(head)){
			for (String key : head.keySet()) {
				String value = head.get(key);
				httpPost.addHeader(new BasicHeader(key, value));
			}
		}
		
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		
		if(!MapUtils.isEmpty(params)){
			for (String key : params.keySet()) {
				list.add(new BasicNameValuePair(key, params.get(key)));
			}
		}
		
		CloseableHttpResponse response = null;
		try{
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(list, Charset.forName(DEFAULT_ENCODING));
			httpPost.setEntity(formEntity);
			response = httpClient.execute(httpPost);
		}catch(Exception e){
			throw new HttpException(500, "请求失败", e);
		}
		return response;
	}
	
	/**
	 * 发送post请求 (需要关闭response)
	 * @param url 		请求地址
	 * @param head		请求头
	 * @param params	请求的http body参数
	 * @return 返回HttpResponse
	 */
	public CloseableHttpResponse post(String url, Map<String, String> head, String params) {
		CloseableHttpClient httpClient = getHttpClient();
		HttpPost httpPost = new HttpPost(url);
		if(!MapUtils.isEmpty(head)){
			for (String key : head.keySet()) {
				String value = head.get(key);
				httpPost.addHeader(new BasicHeader(key, value));
			}
		}

		CloseableHttpResponse response = null;
		try{
			if(StringUtils.isNotBlank(params)){
				StringEntity stringEntity = new StringEntity(params, Charset.forName(DEFAULT_ENCODING));
				httpPost.setEntity(stringEntity);
			}
			response = httpClient.execute(httpPost);
		}catch(Exception e){
			throw new HttpException(500, "请求失败", e);
		}
		return response;
	}
	
	/**
	 * 发送get请求
	 * @param url 请求地址
	 * @return
	 */
	public String getRequest(String url){
		return this.getRequest(url, null, null);
	}
	
	/**
	 * 发送get请求
	 * @param url	请求地址
	 * @param head	请求头
	 * @param params	请求参数
	 * @return	返回String
	 */
	public String getRequest(String url, Map<String, String> head, Map<String, String> params) {
		CloseableHttpResponse response = get(url, head, params);
		// 判断返回状态是否为200
		if (response.getStatusLine().getStatusCode() != 200) {
			logger.error("发送请求失败，异常码:{}", response.getStatusLine().getStatusCode());
			throw new HttpException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
		}
		HttpEntity entity = response.getEntity();
		try {
			return EntityUtils.toString(entity, Charset.forName(DEFAULT_ENCODING));
		} catch (ParseException | IOException e) {
			throw new HttpException(501, "解析数据失败", e);
		} finally {
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 发送get请求(需要关闭response)
	 * @param url 		请求地址
	 * @param head		请求头
	 * @param params	请求的url参数
	 * @return 返回HttpResponse
	 */
	public CloseableHttpResponse get(String url, Map<String, String> head, Map<String, String> params){
		CloseableHttpClient httpClient = getHttpClient();
		HttpGet httpGet = null;
		
		if(!MapUtils.isEmpty(params)) {
			try {
				URIBuilder uriBuilder = new URIBuilder(url);
				for (String key : params.keySet()) {
					uriBuilder.addParameter(key, params.get(key));
				}
				httpGet = new HttpGet(uriBuilder.build().toString());
			} catch (URISyntaxException e) {
				e.printStackTrace();
				throw new HttpException(501, "创建get请求连接失败", e);
			}
		}else {
			httpGet = new HttpGet(url);
		}
		
		if(!MapUtils.isEmpty(head)){
			for (String key : head.keySet()) {
				String value = head.get(key);
				httpGet.addHeader(new BasicHeader(key, value));
			}
		}
		
		CloseableHttpResponse response = null;
		try{
			response = httpClient.execute(httpGet);
		}catch(Exception e){
			throw new HttpException(500, "请求失败", e);
		}
		return response;
	}
	
	public CloseableHttpResponse head(String url, Map<String, String> head, Map<String, String> params){
		CloseableHttpClient httpClient = getHttpClient();
		HttpHead httpHead = null;
		
		if(!MapUtils.isEmpty(params)) {
			try {
				URIBuilder uriBuilder = new URIBuilder(url);
				for (String key : params.keySet()) {
					uriBuilder.addParameter(key, params.get(key));
				}
				httpHead = new HttpHead(uriBuilder.build().toString());
			} catch (URISyntaxException e) {
				e.printStackTrace();
				throw new HttpException(501, "创建get请求连接失败", e);
			}
		}else {
			httpHead = new HttpHead(url);
		}
		
		if(!MapUtils.isEmpty(head)){
			for (String key : head.keySet()) {
				String value = head.get(key);
				httpHead.addHeader(new BasicHeader(key, value));
			}
		}
		
		CloseableHttpResponse response = null;
		try{
			response = httpClient.execute(httpHead);
		}catch(Exception e){
			throw new HttpException(500, "请求失败", e);
		}
		return response;
	}
	
	public static void main(String[] args) {
		HttpClientProperties properties = new HttpClientProperties();
		HttpManager httpManager = new HttpManager(properties);
		String result = null;
		CloseableHttpResponse response = null;
//		result = httpManager.getRequest("http://blog.csdn.net/u010648555/article/details/52162840");
//		System.out.println(result);
//		result = httpManager.getRequest("https://192.168.102.155:20009/", null, null);
//		System.out.println(result);
//		result = httpManager.getRequest("https://www.hao123.com/", null, null);
//		System.out.println(result);
//		result = httpManager.getRequest("https://www.hao123.com/", null, null);
//		System.out.println(result);
		result = httpManager.getRequest("http://www.baidu.cn/");
		System.out.println(result);
//		// 判断返回状态是否为200
//		if (response.getStatusLine().getStatusCode() != 200) {
//			throw new HttpException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
//		}
//		Header[] headers = response.getAllHeaders();
//		for (Header header : headers) {
//			System.out.println(header.getName() + "=" + header.getValue());
//		}
	}
	
	public static void main1(String[] args) {
		final HttpClientProperties properties = new HttpClientProperties();
		HttpManager httpManager = new HttpManager(new HttpConfig() {
			@Override
			public RequestConfig config(HttpClientBuilder httpClientBuilder, Builder builder) {
				builder.setConnectionRequestTimeout(properties.getConnectionRequestTimeout());
				builder.setConnectTimeout(properties.getConnectTimeout());
				//builder.setRedirectsEnabled(false);//可以在此处配置是否开启重定向
				//builder.setProxy(proxy);//可以在此处配置代理
				
				PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
				connectionManager.setMaxTotal(properties.getMaxTotal());// 最大连接数
				connectionManager.setDefaultMaxPerRoute(properties.getDefaultMaxPerRoute());// 设置每个主机地址的并发数
				httpClientBuilder.setConnectionManager(connectionManager);
				
				return builder.build();
			}
		});
		String result = null;
		result = httpManager.getRequest("http://blog.csdn.net/u010648555/article/details/52162840");
		System.out.println(result);
	}
}
