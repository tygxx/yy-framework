package com.yy.framework.commons.xls;

/**
 * 类名称: ExcelTitle<br>
 * 类描述: 导出excel时的标题，用于映射标题和实体的属性<br>
 * 修改时间: 2016年12月1日上午11:09:46<br>
 * @author mateng@eversec.cn
 */
public class ExcelTitle {
	
	/**
	 * excel标题
	 */
	private String title;
	
	/**
	 * excel标题对应的bean的属性
	 */
	private String filed;
	
	public ExcelTitle() {
		super();
	}

	public ExcelTitle(String title, String filed) {
		super();
		this.title = title;
		this.filed = filed;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFiled() {
		return filed;
	}

	public void setFiled(String filed) {
		this.filed = filed;
	}
	
	

}
