/**    
 * 文件名：QueryResult.java    
 * 版本信息：1.0.0
 * 日期：2015年5月4日    
 * Copyright 恒安嘉新（北京）科技有限公司 Corporation 2015     
 * 版权所有       
 */
package com.yy.framework.core.data;

import java.util.List;

/**    
 * 项目名称：smvc-core    
 * 类名称：QueryResult    
 * 类描述：    查询结果集
 * 创建人：codemonkey    
 * 创建时间：2015年5月4日 下午6:42:10    
 * 修改人：codemonkey    
 * 修改时间：2015年5月4日 下午6:42:10    
 * 修改备注：    
 * @version 1.0.0      
 */
public class QueryResult<T> {
	
	/**返回的结果集*/
	private List<T> resultData;
	
	/**总记录数*/
	private long totalRecord;

	/**    
	 * resultData    
	 * @return  the resultData       
	 */
	
	public List<T> getResultData() {
		return resultData;
	}

	/**    
	 * @param resultData the resultData to set    
	 */
	public void setResultData(List<T> resultData) {
		this.resultData = resultData;
	}

	/**    
	 * totalRecord    
	 * @return  the totalRecord       
	 */
	
	public long getTotalRecord() {
		return totalRecord;
	}

	/**    
	 * @param totalRecord the totalRecord to set    
	 */
	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}
}
