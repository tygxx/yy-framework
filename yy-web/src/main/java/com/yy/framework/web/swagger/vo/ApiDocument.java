package com.yy.framework.web.swagger.vo;

import io.swagger.models.Info;

import java.util.List;

/**
 * 类名称: ApiDocument<br>
 * 类描述: api文档实体类<br>
 * 修改时间: 2017年5月23日下午8:11:49<br>
 * @author mateng@eversec.cn
 */
public class ApiDocument {
	
	private Info info;
	
	private String host;
    
	private String basePath;

	private List<ApiTag> apiTags;

	public Info getInfo() {
		return info;
	}

	public void setInfo(Info info) {
		this.info = info;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public List<ApiTag> getApiTags() {
		return apiTags;
	}

	public void setApiTags(List<ApiTag> apiTags) {
		this.apiTags = apiTags;
	}
}
