package com.yy.framework.core.sql.vo;

import java.util.List;

public class SqlScript {
	
	/**
	 * SQL脚本文件对应xml的路径
	 */
	private String location;
	
	/**
	 * SQL脚本文件的命名空间
	 */
	private String namespace;

	/**
	 * SQL脚本文件中每个小xml节点的id，每一个节点代表一种sql语句
	 */
	private String id;
	
	/**
	 * SQL脚本中每个xml节点的文本内容
	 */
	private String data;
	
	/**
	 * SQL脚本中每个xml节点中的判断条件
	 */
	private List<SqlCondition> conditions;
	
	public String key() {
		return this.namespace + "." + this.id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<SqlCondition> getConditions() {
		return conditions;
	}

	public void setConditions(List<SqlCondition> conditions) {
		this.conditions = conditions;
	}
	
}
