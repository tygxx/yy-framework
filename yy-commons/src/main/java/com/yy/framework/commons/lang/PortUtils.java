package com.yy.framework.commons.lang;

/**
 * <p>类描述：端口操作工具类</p>
 * @author hanlishan
 * 创建时间：2017年8月14日下午5:40:05
 */
public class PortUtils {
	
	/**
	 * <p>方法描述：对端口正确性进行验证</p>
	 * <p>支持具体端口或以“-”分隔的端口范围，单个端口元素长度最大为13</p>
	 * <p>修改时间：2017年8月14日 下午5:40:21</p>
	 * @author hanlishan
	 * @param portStr 端口字符串
	 * @return true:正确  false:错误
	 */
	public static boolean portJudge(String portStr){
		String[] arr = portStr.split(",");
		for(String s : arr){
			if(s.length() > 13){
				return false;
			}
			if(s.contains("-")){
				String[] subArr = s.split("-");
				Integer startNum = null;
				Integer endNum = null;
				try{
					startNum = Integer.parseInt(subArr[0].trim());
					endNum = Integer.parseInt(subArr[1].trim());
				}catch(Exception ex){
					return false;
				}
				if(startNum < 1 || startNum > 65535){
					return false;
				}
				if(endNum < 1 || endNum > 65535){
					return false;
				}
				if(startNum > endNum){
					return false;
				}
			}else{
				Integer portNum = null;
				try{
					portNum = Integer.parseInt(s);
				}catch(Exception ex){
					return false;
				}
				if(portNum < 1 || portNum > 65535){
					return false;
				}
			}
		}
		
		return true;
	}
	public static void main(String[] args) {
		String portStr = "65524 - 65535";
		System.out.println(PortUtils.portJudge(portStr));
	}
}
