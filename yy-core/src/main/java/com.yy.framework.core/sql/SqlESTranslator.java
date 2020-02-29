package com.yy.framework.core.sql;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.eversec.framework.core.sql.vo.DataType;
import com.eversec.framework.core.sql.vo.Placeholder;
import com.eversec.framework.core.sql.vo.SqlCondition;
import com.eversec.framework.core.sql.vo.SqlScript;
import com.eversec.framework.core.sql.vo.SqlType;
import com.eversec.framework.core.sql.xml.Xml2Sql;

/**
 * 类名称: SqlESReplace<br>
 * 类描述: es数据库的翻译sql实现类<br>
 * 修改时间: 2017年1月3日下午6:38:36<br>
 * @author mateng@eversec.cn
 */
public class SqlESTranslator implements SqlTranslator {
	
	private static Logger logger = Logger.getLogger(SqlESTranslator.class.getName());
	
	public static final String PATTERN = "\\#\\{([A-Za-z0-9_,\\s]*)\\}";
	
	private Xml2Sql xml2Sql;
	
	public SqlESTranslator(Xml2Sql xml2Sql) {
		this.xml2Sql = xml2Sql;
	}

	@Override
	public String getSql(String namespace, String id, Object param) {
		//找出sql脚本
		SqlScript sqlScript = xml2Sql.getSqlScript(namespace, id);
		//找出sql脚本的xml节点中的文本内容（包含子xml节点的文本内容）
		String sql = sqlScript.getData();
		//循环所有的条件，把不符合条件的sql语句从sql中剔除掉
		Map<String, String> filedMap = new HashMap<>();
		for (SqlCondition sqlCondition : sqlScript.getConditions()) {
			if(SqlType.ISNOTEMPTY.getValue().equals(sqlCondition.getType())) {
				filedMap.put(sqlCondition.getField(), sqlCondition.getFieldType());
				String value = getProperty(param, sqlCondition.getField());
				if(StringUtils.isBlank(value)) {
					sql = sql.replace(sqlCondition.getText(), "");
				}
			}
		}
		
		//根据正则找出sql语句中所有的占位符
		Matcher m = Pattern.compile(PATTERN).matcher(sql);
		List<Placeholder> placeholders = new ArrayList<>();
		while (m.find()) {
			String group = m.group();
			logger.info("占位符：" + group);
			Placeholder placeholder = new Placeholder(group);
			String filedType = filedMap.get(placeholder.getFiled());
			if(StringUtils.isNotBlank(filedType)) {
				try {
					placeholder.setDataType(DataType.valueOf(filedType.toUpperCase()));
				} catch (IllegalArgumentException e) {
					placeholder.setDataType(DataType.INT);
				}
			}
			placeholders.add(placeholder);
		}
		
		for (Placeholder placeholder : placeholders) {
			String value = getProperty(param, placeholder.getFiled());
			if(StringUtils.isBlank(value)) {
				sql = sql.replace(placeholder.getName(), "null");
//				throw new BusinessException(-4000, "无法得到参数值：" + placeholder.getName());
			}else {
				if(DataType.STRING.equals(placeholder.getDataType())) {
					sql = sql.replace(placeholder.getName(), "'" + value + "'");
				}else {
					sql = sql.replace(placeholder.getName(), value);
				}
			}
		}
		
		return sql;
	}
	
	private String getProperty(Object param, String filed) {
		if(param == null) {
			return null;
		}
		String value = null;
		try {
			if(param instanceof Map) {
				value = BeanUtils.getProperty(param, filed);
			}else if(filed.contains(".")) {
				value = BeanUtils.getNestedProperty(param, filed);
			}else {
				value = BeanUtils.getSimpleProperty(param, filed);
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			logger.severe("反射值失败，属性名称：" + filed);
		}
		return value;
	}

}
