package com.yy.framework.web.swagger.vo;

import io.swagger.models.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名称: ApiTag<br>
 * 类描述: API文档的标签信息（多个接口分组后称为一个标签）<br>
 * 修改时间: 2017年5月23日下午8:12:06<br>
 * @author mateng@eversec.cn
 */
public class ApiTag {
	
	private String name;
	
	private String description;
	
	private List<ApiOperation> operations;
	
	public ApiTag(Tag tag) {
		this.name = tag.getName();
		this.description = tag.getDescription();
	}
	
	public void addOperations(ApiOperation apiOperation) {
		if(operations == null) {
			operations = new ArrayList<>();
		}
		operations.add(apiOperation);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ApiOperation> getOperations() {
		return operations;
	}

	public void setOperations(List<ApiOperation> operations) {
		this.operations = operations;
	}
	
}
