package com.yy.framework.core.response2;

/**
 * 类名称: ResponseExecutable<br>
 * 类描述: 处理响应类的接口<br>
 * 修改时间: 2019年3月19日<br>
 * @author mateng@eversec.cn
 * @param <T>
 */
public interface ResponseExecutable<T> {
	
	public T execute();
	
}
