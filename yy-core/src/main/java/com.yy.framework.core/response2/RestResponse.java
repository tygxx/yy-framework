package com.yy.framework.core.response2;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 类名称: Rsp<br>
 * 类描述:  controller返回的数据类型<br>
 * 修改时间: 2019年3月19日<br>
 * @author mateng@eversec.cn
 */
@ApiModel
public class RestResponse<T> implements Serializable {

	private static final long serialVersionUID = 1146652338844128518L;

	public static final int SUCCESS = 200;
	
	@ApiModelProperty(value="响应报文的响应码，code==200即为正常响应")
	private int code;
	
	@ApiModelProperty(value="响应报文的消息提示")
	private String message;
	
	@ApiModelProperty(value="响应报文的消息体")
	private T body;
	
	@ApiModelProperty(value="响应报文的时间戳")
	private Long timestamp;
	
	public RestResponse(){}
	
	public RestResponse(T body) {
		this(SUCCESS, "成功", body);
	}
	
	public RestResponse(int code , String message, T body){
		this.code = code;
		this.message = message;
		this.body = body;
		timestamp = System.currentTimeMillis();
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getBody() {
		return body;
	}
	public void setBody(T data) {
		this.body = data;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	@JsonIgnore
	public String getJsonBody() {
		return JSONObject.toJSONString(body);
	}
	
//	@JsonIgnore
//	public <T> T getObjectBody(Class<T> clazz) {
//		String json = getJsonBody();
//		return JSONObject.parseObject(json, clazz);
//	}
//	
//	@JsonIgnore
//	public <E> List<E> getListBody(Class<E> clazz) {
//		String json = getJsonBody();
//		return JSONObject.parseArray(json, clazz);
//	}
//	
//	public static Response2 ok(T body) {
//		return new Response2(body);
//	}
//	
//	public static Response2 error(int code , String message) {
//		return new Response2(code, message, null);
//	}
	
	@Override
	public String toString() {
		return "Response [code=" + code + ", message=" + message + ", body=" + body + "]";
	}
}
