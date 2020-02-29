package com.yy.framework.commons.collections;

import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 类名称: ECollectionsUtils<br>
 * 类描述:  集合工具类（apache CollectionUtils的补充）<br>
 * 创建人: codemonkey<br>
 * 创建时间: 2015年4月29日 下午5:53:35<br>
 * 修改人: mateng@eversec.cn<br>
 * 修改时间: 2016年11月24日下午1:29:01<br>
 */
public class ECollectionsUtils {

	/**
	 * 转换Collection所有元素(通过toString())为String, 每个元素的前面加入prefix，后面加入postfix，如<div>mymessage</div>。
	 * @param collection 需要遍历的集合
	 * @param prefix	每个元素的前面增加的前缀
	 * @param postfix	每个元素的后面增加的后缀
	 * @return	如<div>mymessage</div>
	 */
	public static String convertToString(final Collection collection, final String prefix, final String postfix) {
		StringBuilder builder = new StringBuilder();
		for (Object o : collection) {
			builder.append(prefix).append(o).append(postfix);
		}
		return builder.toString();
	}


	/**
	 * 取得Collection的第一个元素，如果collection为空返回null
	 * @param collection 集合
	 * @param <T> 类
	 * @return 返回集合中的第一个元素
	 */
	public static <T> T getFirst(Collection<T> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return null;
		}
		return collection.iterator().next();
	}

	/**
	 * 获取Collection的最后一个元素
	 * @param collection 集合
	 * @param <T> 类
	 * @return 返回集合中的最后一个元素，如果collection为空返回null.
	 */
	public static <T> T getLast(Collection<T> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return null;
		}
		
		// 当类型为List时，直接取得最后一个元素 。
		if (collection instanceof List) {
			List<T> list = (List<T>) collection;
			return list.get(list.size() - 1);
		}

		// 其他类型通过iterator滚动到最后一个元素.
		Iterator<T> iterator = collection.iterator();
		while (true) {
			T current = iterator.next();
			if (!iterator.hasNext()) {
				return current;
			}
		}
	}
}
