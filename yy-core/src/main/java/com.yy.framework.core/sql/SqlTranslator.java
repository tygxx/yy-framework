package com.yy.framework.core.sql;

public interface SqlTranslator {
	
	/**
	 * 翻译并获取sql
	 * @param namespace xml的命名空间
	 * @param id sql语句的id
	 * @param param	翻译sql的参数
	 * @return
	 */
	public String getSql(String namespace, String id, Object param);

}
