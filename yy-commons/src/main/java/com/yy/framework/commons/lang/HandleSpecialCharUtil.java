package com.yy.framework.commons.lang;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * <p>类名: HandleMysqlSpecialCharUtil</p>
 * <p>描述: mysql特殊字符处理</p>
 * <p>公司： www.eversec.com.cn</p>
 * <p>修改时间: 2017年7月31日 </p> 
 * @author guolili
 */
public class HandleSpecialCharUtil {

	public static String escapeString(String x) {
		if (StringUtils.isBlank(x)) {
			return "";
		}
		StringBuilder buf = new StringBuilder((int) (x.length() * 1.1));
		int stringLength = x.length();
		for (int i = 0; i < stringLength; ++i) {
			char c = x.charAt(i);
			switch (c) {
			case 0:
				buf.append('\\');
				buf.append('0');
				break;
			case '\n':
				buf.append('\\');
				buf.append('n');
				break;
			case '\r':
				buf.append('\\');
				buf.append('r');
				break;
			case '\\':
				buf.append('\\');
				buf.append('\\');
				break;
			case '\'':
				buf.append('\\');
				buf.append('\'');
				break;
			case '"':
				buf.append('\\');
				buf.append('"');
				break;
			case '%':
				buf.append('\\');
				buf.append('%');
				break;
			case '_':
				buf.append('\\');
				buf.append('_');
				break;
			default:
				buf.append(c);
			}
		}
		return buf.toString();
	}

	public static void main(String[] args) {
		System.out.println(escapeString(null));
	}
}