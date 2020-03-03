package com.yy.framework.web;

import com.yy.framework.core.exception.BusinessException;
import com.yy.framework.core.reponse.Response;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.List;


@ControllerAdvice
class ErrorHandler {
	
	private static Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
	
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public Response defaultErrorHandler(Exception e) {
		String message = null;
		int code = 200;
		if (e instanceof BusinessException) {//业务异常
			message = e.getMessage();
			code = ((BusinessException) e).getCode();
			logger.warn("BusinessException:[code:{}, message:{}, stacktrace:{}]", code, message, e.getStackTrace()[0]);
		}else if(e instanceof HttpRequestMethodNotSupportedException){//method不支持
			message = "Method Not Allowed, " + e.getMessage();
			code = 405;
			logger.warn("HttpRequestMethodNotSupportedException:[code:{}, message:{}, stacktrace:{}]", code, message, e.getStackTrace()[0]);
		}else if(e instanceof NestedRuntimeException) {//MethodArgumentTypeMismatchException，pathvaiable的参数类型不匹配
			message = "Bad Request, " + e.getMessage();
			code = 400;
			logger.warn("NestedRuntimeException:[code:{}, message:{}, stacktrace:{}]", code, message, e.getStackTrace()[0]);
		}else if(e instanceof BindException) {//MethodArgumentTypeMismatchException，requestparma的参数类型不匹配
			BindingResult bindingResult = ((BindException) e).getBindingResult();
			List<String> errors = new ArrayList<>();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				errors.add(objectError.getDefaultMessage());
			}
			message = "Bad Request, " + StringUtils.join(errors, ",");
			code = 400;
			logger.warn("BindException:[code:{}, message:{}, stacktrace:{}]", code, message, e.getStackTrace()[0]);
		}else if(e instanceof NestedServletException) {//MissingServletRequestParameterException，requestparam的参数没有传递
			message = "Bad Request, " + e.getMessage();
			code = 400;
			logger.warn("NestedServletException:[code:{}, message:{}, stacktrace:{}]", code, message, e.getStackTrace()[0]);
		}else {
			message = "服务器内部异常";
			code = 500;
			logger.error(message, e);
		}
		Response response = new Response(code, message, null);
		return response;
	}

}