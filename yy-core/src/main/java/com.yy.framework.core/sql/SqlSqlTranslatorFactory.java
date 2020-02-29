package com.yy.framework.core.sql;

import com.eversec.framework.core.sql.vo.SqlDialect;
import com.eversec.framework.core.sql.xml.Xml2Sql;

/**
 * 类名称: SqlReplaceFactory<br>
 * 类描述: 该类已经废弃，将不再维护，请使用SqlTranslatorFactory<br>
 * 修改时间: 2017年1月3日下午6:39:05<br>
 * @author mateng@eversec.cn
 */
@Deprecated()
public class SqlSqlTranslatorFactory {
	
	/**
	 * 创建sql翻译器
	 * @param dialect 数据库的方言
	 * @param pck	扫描mapper文件的包路径
	 * @return
	 */
	public static SqlTranslator create(SqlDialect dialect, String pck) {
		Xml2Sql xml2Sql = new Xml2Sql();
		xml2Sql.scanXml(pck);
		if(SqlDialect.ES.equals(dialect)) {
			return new SqlESTranslator(xml2Sql);
		}
		return null;
	}

}
