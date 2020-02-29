package com.yy.framework.core.jpa;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 类名称: BatchJpaTemplateSetter<br>
 * 类描述: jap批量插入的调用接口<br>
 * 修改时间: 2017年4月24日下午6:23:56<br>
 * @author mateng@eversec.cn
 */
public interface BatchJpaStatementSetter {
	
	/**
	 * @param ps  PreparedStatement
	 * @param row  批量数据中的单元
	 * @param i	游标 index of the statement
	 * @throws SQLException 执行sql异常
	 */
	void setValues(PreparedStatement ps, Object row, int i) throws SQLException;

}
