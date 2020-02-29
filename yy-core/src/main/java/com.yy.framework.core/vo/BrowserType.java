package com.yy.framework.core.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名称: BrowserType<br>
 * 类描述: 浏览器的类型<br>
 * 创建人: mateng@eversec.cn<br>
 * 创建时间: 2016年11月25日上午9:33:55<br>
 * 修改人: <br>
 * 修改时间: <br>
 */
public enum BrowserType {
	
	IE6("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)"),
	IE7("Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 6.0)"),
	IE8("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)"),
	IE9("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)"),
	Maxthon("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.12 (KHTML, like Gecko) Maxthon/3.0 Chrome/22.0.1229.79 Safari/535.12"),
	Firefox("Mozilla/5.0 (Windows NT 5.1; zh-CN; rv:1.9.1.3) Gecko/20100101 Firefox/8.0"),
	Chrome("Mozilla/5.0 (Windows NT 5.1; zh-CN) AppleWebKit/535.12 (KHTML, like Gecko) Chrome/22.0.1229.79 Safari/535.12"),
	Opera("Opera/9.99 (Windows NT 5.1; U; zh-CN) Presto/9.9.9"),
	Safari("Mozilla/5.0 (Windows NT 5.1; zh-CN) AppleWebKit/535.12 (KHTML, like Gecko) Version/5.0.1 Safari/535.12");
	
	private String value;
	
	BrowserType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public static List<BrowserType> list() {
		List<BrowserType> types = new ArrayList<BrowserType>();
		types.add(IE6);
		types.add(IE7);
		types.add(IE8);
		types.add(IE9);
		types.add(Maxthon);
		types.add(Firefox);
		types.add(Chrome);
		types.add(Opera);
		types.add(Safari);
		return types;
	}
}
