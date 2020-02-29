package com.yy.framework.commons.http;

/**
 * 类名称: HttpException<br>
 * 类描述: httpclient异常类<br>
 * 修改时间: 2016年11月29日上午10:57:48<br>
 * @author mateng@eversec.cn
 */
public class HttpException extends RuntimeException {

	private static final long serialVersionUID = 9084633664515645279L;
	
	private Integer code;

    private String message;

    public HttpException(Integer code) {
        super();
        this.code = code;
    }

    public HttpException(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public HttpException(Integer code, String message, Throwable cause) {
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
		return "HttpException [code=" + code + ", message=" + message + "]";
	}
}
