package com.yy.framework.core.sql.vo;

public enum SqlType {
	
	ISNOTEMPTY("isNotEmpty");
	
	private String value;
	
	private SqlType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
