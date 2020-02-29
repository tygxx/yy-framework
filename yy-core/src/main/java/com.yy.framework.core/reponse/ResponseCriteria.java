package com.yy.framework.core.reponse;

import java.util.HashMap;
import java.util.Map;

public class ResponseCriteria {

	private Map<String, Object> resultMap;
	
	private Object result;
	
	public ResponseCriteria addMapResult(String key,Object value) {
		if(getResultMap() == null) {
			setResultMap(new HashMap<String, Object>());
		}
		getResultMap().put(key, value);
		return this;
	}
	
	public ResponseCriteria addSingleResult(Object object) {
		this.setResult(object);
		return this;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Map<String, Object> getResultMap() {
		return resultMap;
	}

	public void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;
	}

}
