package com.yy.framework.commons.bean;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名称: EBeanUtils<br>
 * 类描述: bean操作公共方法(对apacheBeanUtils的扩展)<br>
 * 修改时间: 2016年11月25日上午9:36:40<br>
 * @author mateng@eversec.cn
 */
public class EBeanUtils {
	
	/**
	 * 把src中不为空的项更新到dest中<br>
	 * 创建者: lidongyang@eversec.cn  <br>
	 * 修改时间: Aug 25, 2015 2:43:17 PM  <br>
	 * @param src 源对象
	 * @param dest 目标对象
	 */
	public static void copyBeanNotNullToBean(Object src, Object dest) {
		PropertyDescriptor[] origDescriptors = PropertyUtils.getPropertyDescriptors(src);
		for (int i = 0; i < origDescriptors.length; i++) {
			String name = origDescriptors[i].getName();
			if (!"class".equals(name)) {
				if ((PropertyUtils.isReadable(src, name)) && (PropertyUtils.isWriteable(dest, name))) {
					try {
						Object value = PropertyUtils.getSimpleProperty(src, name);
						if (value != null) {
							BeanUtils.copyProperty(dest, name, value);
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	/**
	 * 
	 * <p>方法名: getObjectToMap</p>
	 * <p>描述: Bean转map 并去掉无用的属性</p>
	 * <p>修改时间: 2017年7月5日 下午3:28:06</p>  
	 * @author guolili
	 * @param obj 要转换的对象
	 * @param wipes	需要去掉的属性
	 * @return map
	 */
	public static Map<String, Object> beanToMap(Object obj, List<String> wipes) {
		Map<String, Object> map = new HashMap<>();
		Class<? extends Object> clazz = obj.getClass();
		while(clazz != null){
			Field[] superFieldList = clazz.getDeclaredFields();
			for (Field field : superFieldList) {
				field.setAccessible(true);
				String fname = field.getName();
				if (!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())
						&& !wipes.contains(fname)) {
						try {
							map.put(field.getName(), PropertyUtils.getSimpleProperty(obj, fname));
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						}
				}
			}
			clazz = clazz.getSuperclass();
		}
		return map;
	}
}