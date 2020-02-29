package com.yy.framework.web.swagger.vo;

import io.swagger.models.Operation;
import io.swagger.models.parameters.Parameter;

import java.util.List;

/**
 * 类名称: ApiOperation<br>
 * 类描述: API接口信息<br>
 * 修改时间: 2017年5月23日下午8:12:41<br>
 * @author mateng@eversec.cn
 */
public class ApiOperation {
	
	private String method;
	
	private String summary;
	
	private String description;
	
	private String url;
	
	private List<Parameter> parameters;
	
	public ApiOperation(String url, String method, Operation operation) {
		this.url = url;
		this.method = method;
		this.summary = operation.getSummary();
		this.description = operation.getDescription();
		this.parameters = operation.getParameters();
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
