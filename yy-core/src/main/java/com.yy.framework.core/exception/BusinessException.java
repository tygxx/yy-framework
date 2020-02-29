package com.yy.framework.core.exception;

/**
 * 类名称: BusinessException<br>
 * 类描述: 业务处理异常通用类<br>
 * 修改时间: 2016年11月29日上午10:57:48<br>
 * @author mateng@eversec.cn
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 9084633664515645279L;
	
	/**业务层通用异常*/
	public static final Integer BUSSINESS_EXCEPTION = 10000;
	/**数据不存在*/
	public static final Integer BUSSINESS_DATE_NOT_EXISTS = 10001;
	
	/**数据库操作通用异常*/
	public static final Integer DB_CONNETION_EXCEPTION = 20000;
	
	/**违反唯一约束*/
	public static final Integer DB_DUPLICATE_EXCEPTION = 20001;
	
	/**反射异常*/
	public static final Integer REFLECTION_EXCEPTION = 30000;
	
	/**获取注解异常*/
	public static final Integer ANNOTATION_EXCEPTION = 40000;
	
	private Integer code;

    private String message;

    public BusinessException(Integer code) {
        super();
        this.code = code;
    }

    public BusinessException(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public BusinessException(Integer code, String message, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

	@Override
	public String toString() {
		return "BusinessException [code=" + code + ", message=" + message + "]";
	}
}
