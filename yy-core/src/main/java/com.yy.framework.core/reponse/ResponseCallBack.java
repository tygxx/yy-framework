package com.yy.framework.core.reponse;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eversec.framework.core.exception.BusinessException;

public abstract class ResponseCallBack {
	
	private static Logger logger = LoggerFactory.getLogger(ResponseCallBack.class.getName());

    public Response sendRequest(Object... obj) {
        String message = "成功";
        int code = 200;
        Object infoList = null;
        ResponseCriteria criteria = new ResponseCriteria();
        try {
            execute(criteria, obj);
            Object result = criteria.getResult();
            Map<String, Object> resultMap = criteria.getResultMap();

            if (result != null && resultMap != null) {
                throw new BusinessException(501, "返回数据异常");
            }
            infoList = (result != null ? result : resultMap);
        } catch (BusinessException e) {
            code = e.getCode();
            message = e.getMessage();
            logger.warn("BusinessException:[code:{}, message:{}, stacktrace:{}]", code, message, e.getStackTrace()[0]);
        } catch (Exception e) {
            code = 500;
            message = "服务内部异常";
            logger.error(e.getMessage(), e);
        } 
        return new Response(code, message, infoList);
    }

    public abstract void execute(ResponseCriteria criteria, Object... obj);

}