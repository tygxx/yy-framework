package com.yy.framework.core.data;

import io.swagger.annotations.ApiModelProperty;

/**
 * 类名称: QueryCondition<br>
 * 类描述: 复合分页条件查询的DTO<br>
 * 修改时间: 2016年11月28日下午6:07:23<br>
 * @author mateng@eversec.cn
 */
public class QueryCondition {
	
	/**
	 * 查询的起始位置，默认值为0，相当于mysql的limit的第一个参数
	 */
	@ApiModelProperty(value="分页查询的起始位置，默认值为0")
	private Integer startPosition = 0;
	
	/**
	 * 查询的条数，默认值为100，相当于mysql的limit的第二个参数
	 */
	@ApiModelProperty(value="分页查询的pageSize，默认值为100")
	private Integer maxResult = 100;

	public Integer getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(Integer startPosition) {
		this.startPosition = startPosition;
	}

	public Integer getMaxResult() {
		return maxResult;
	}

	public void setMaxResult(Integer maxResult) {
		this.maxResult = maxResult;
	}
}
