package com.yy.framework.core.sql.vo;

public class Placeholder {
	
	private String name;
	
	private String filed;
	
	private DataType dataType;
	
	public Placeholder(String name) {
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		if(!name.contains(",")) {
			this.filed = this.name.replace("#{", "").replace("}", "").trim();
			this.dataType = DataType.INT;
		}else {
			String[] array = name.replace("#{", "").replace("}", "").trim().split(",");
			this.filed = array[0].trim();
			this.dataType = DataType.valueOf(array[1].trim());
		}
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	
	public String getFiled() {
		return filed;
	}
}
