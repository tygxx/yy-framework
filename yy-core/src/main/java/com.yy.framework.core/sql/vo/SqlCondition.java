package com.yy.framework.core.sql.vo;

/**
 * 类名称: SqlCondition<br>
 * 类描述: 拼组sql语句的条件<br>
 * 修改时间: 2017年1月3日下午6:37:57<br>
 * @author mateng@eversec.cn
 */
public class SqlCondition {
	
	/**
	 * 拼接SQL条件的判断类型，该字段是SqlType.value。例如：isNotEmpty
	 */
	private String type;

	/**
	 * 用于判断条件是否成立的属性
	 */
	private String field;
	
	/**
	 * 属性类型：String、Int、Date。主要用于如何判断filed是否为空
	 */
	private String fieldType;
	
	/**
	 * 该条件xml节点中的text文本内容
	 */
	private String text;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

}
