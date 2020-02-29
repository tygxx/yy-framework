package com.yy.framework.commons.collections;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名称: ExtendMap<br>
 * 类描述: map的扩展类，可以从map中直接获取int、long等类型的数据<br>
 * 创建人: mateng@eversec.cn<br>
 * 创建时间: 2016年11月25日上午9:38:23<br>
 * 修改人: <br>
 * 修改时间: <br>
 */
public class ExtendMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = -5968565033956565603L;
	
	public ExtendMap() {
		super();
	}
	
	public ExtendMap(Map<? extends K, ? extends V> map) {
		super(map);
	}
	
	public ExtendMap(K k , V v) {
		this();
		this.put(k, v);
	}
	
	public ExtendMap<K, V> add(K k , V v) {
		this.put(k, v);
		return this;
	}
	
	public String getString(Object key) {
		if(!this.containsKey(key)) {
			return null;
		}
		Object answer = this.get(key);
		if (answer == null) {
			return null;
		}
		return answer.toString();
	}
	
	public Integer getInteger(Object key) {
		String s = getString(key);
		if(isEmpty(s)) {
			return null;
		}
		return MapUtils.getInteger(this, key);
	}
	
	public int toInt(Object key) {
		Integer i =  getInteger(key);
		if(i == null) {
			return 0;
		}
		return i.intValue();
	}

	public Long getLong(Object key) {
		String s = getString(key);
		if(StringUtils.isEmpty(s)) {
			return null;
		}
		return MapUtils.getLong(this, key);
	}
	
	public long toLong(Object key) {
		Long i =  getLong(key);
		if(i == null) {
			return 0l;
		}
		return i.longValue();
	}
	
	public Double getDouble(Object key) {
		String s = getString(key);
		if(isEmpty(s)) {
			return null;
		}
		return MapUtils.getDouble(this, key);
	}
	
	public double toDouble(Object key) {
		Double i =  getDouble(key);
		if(i == null) {
			return 0d;
		}
		return i.doubleValue();
	}

	public Boolean getBoolean(Object key) {
		String s = getString(key);
		if(isEmpty(s)) {
			return null;
		}
		return MapUtils.getBoolean(this, key);
	}
	
	public boolean toBoolean(Object key) {
		Boolean i =  getBoolean(key);
		if(i == null) {
			return Boolean.FALSE;
		}
		return i.booleanValue();
	}
	
	public BigDecimal getBigDecimal(Object key) {
		String s = getString(key);
		if(isEmpty(s)) {
			return null;
		}
		return BigDecimal.valueOf(getDouble(key));
	}

	public ExtendMap<String, Object> getMap(String key) {
		Object obj = get(key);
		if(obj instanceof Map) {
			return new ExtendMap<String, Object>((Map) obj);
		}
		return null;
	}
	
	public <E> List<E> getList(String key) {
		Object obj = get(key);
		if(obj instanceof List) {
			List<E> list = new ArrayList<>();
			for (Object o : (List)obj) {
				list.add((E) o);
			}
			return list;
		}
		return null;
	} 
	
	private Boolean isEmpty(String str) {
		return StringUtils.isBlank(str);
	}
	
	public String toUrlParam() {
		StringBuffer sb = new StringBuffer();
		for (K k : this.keySet()) {
			V v = this.get(k);
			if(v != null) {
				if(sb.length() != 0) {
					sb.append("&");
				}
				sb.append(k.toString() + "=" + v.toString());
			}
		}
		return sb.toString();
	}
}
