package com.yy.framework.core.response2;

import com.yy.framework.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类名称: ResponseHolder<br>
 * 类描述: 处理响应类<br>
 * 修改时间: 2019年3月19日<br>
 * @author mateng@eversec.cn
 */
public class ResponseHolder {
	
	private static Logger logger = LoggerFactory.getLogger(ResponseHolder.class.getName());
	
	public static <T> RestResponse<T> build(ResponseExecutable<T> executable) {
		String message = "成功";
		int code = 200;

		long start = System.currentTimeMillis();
		try {
			T t = executable.execute();
			return new RestResponse<T>(code, message, t);
		} catch (BusinessException e) {
			code = e.getCode().intValue();
			message = e.getMessage();
			logger.warn("BusinessException:[code:{}, message:{}, stacktrace:{}]", code, message, e.getStackTrace()[0]);
		} catch (Exception e) {
			code = 500;
			message = "服务内部异常";
			logger.error(message, e);
		}
		
		long time = System.currentTimeMillis() - start;
		logger.debug("耗时：{}", time);

		return new RestResponse<T>(code, message, null);
	}
}