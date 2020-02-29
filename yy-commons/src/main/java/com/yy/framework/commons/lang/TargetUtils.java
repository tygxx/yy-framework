package com.yy.framework.commons.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>类名: TargetUtils</p>
 * <p>描述: 解析扫描目标公共方法</p>
 * <p>公司： www.eversec.com.cn</p>
 * <p>修改时间: 2016年12月1日 上午10:41:19</p> 
 * @author lidongyang@eversec.cn
 */
public class TargetUtils {
	
	/**
	 * <p>方法名: parserTargets</p>
	 * <p>描述: 解析扫描目标</p>
	 * <p>修改时间: 2016年12月1日 上午10:41:34</p>  
	 * @author lidongyang@eversec.cn  
	 * @param targets
	 * @return
	 */
	public static List<String> parserTargets(String targets){
		List<String> targetList = new ArrayList<String>();
		String[] targetArr = targets.split(";");
		// 循环创建子任务
		for (String target : targetArr) {
			if(IPUtils.isIp(target)){//判断是否是ip
				targetList.add(target);
				continue;
			}
			if(URLUtils.isUrl(target)) {
				targetList.add(target);
				continue;
			}
			
			if(target.contains("-")){
				if(target.split("-")[1].contains(".")){
					targetList.addAll(afterExcludeTargetList(target));
				}else{//项目名中包含"-"
					targetList.add(target);
				}
			}else{
				String[] tempArr = target.split("\\.");
				if(tempArr.length==4){
					//判断第1或2位是否是数字 （2）第3位是否是数字且是否包含“*”和“/” 
					if(!isNumeric(tempArr[0]) || !isNumeric(tempArr[1]) //第1或2位不是数字 
							|| (!isNumeric(tempArr[1]) && !"*".equals(tempArr[2]) && !tempArr[2].contains("/")))//第3位不是数字且不包含“*”和“/”
					{	
						targetList.add(target);
						continue;
					} 
					
					if(!"*".equals(tempArr[3])){
						//第4位只可能是 （1）不包含“/”的非数字 （2）？/？
						String [] arr4 = tempArr[3].split("/");
						if(arr4.length != 2){
							targetList.add(target);
							continue;
						}
						
						//判断最后一个元素是否是 “数字/数字”  格式
						if(!isNumeric(arr4[0]) || !isNumeric(arr4[1])){//有一个不是数字，作为普通url处理
							targetList.add(target);
							continue;
						}
					}
						
					
					String baseTarget = tempArr[0] + "." + tempArr[1] + ".";
					targetList.addAll(paserTarget_2(tempArr, baseTarget));
				}else{//作为普通url处理
					targetList.add(target);
				}
			}
		}		
		return targetList;
	}
	
	/**
	 * <p>方法名: afterExcludeTargetList</p>
	 * <p>描述: 排除不需要扫描的IP 
	 * 需要排除的任务格式
	 * (1) 192.168.1.1/24-192.168.1.3/24 排除某个ip段，只扫描1和2
	 * (2) 192.168.1.1/24-192.168.1.23-192.168.1.21 表示扫描1-24，但是不扫描23和21
	 * (3) 211.139.1/95.*-211.139.1/79.*;218.203.1/223.*-218.203.1/159.*;117.56.*.*</p>
	 * <p>修改时间: 2016年12月1日 上午10:42:04</p>  
	 * @author lidongyang@eversec.cn  
	 * @param target
	 * @return
	 */
	public static List<String> afterExcludeTargetList(String target){
		//定义任务详情列表，用来存放排除之前的IP任务列表
		List<String> targetList = new ArrayList<String>();
		//解析任务，放到数组中
		String[] targetArr = target.split("-");
		//定义排除前需要解析的IP段
		String[] scanArr =  targetArr[0].split("\\.");
		
		//定义排除前的ip前三段	
		//StringBuffer baseTarget = new StringBuffer(scanArr[0]+"."+scanArr[1]+"."+scanArr[2]+".");
		//解析排除之前的IP任务列表，放到任务详情列表中
		//targetList.addAll(putTargetToList(scanArr,baseTarget));
		
		//定义排除前的ip前两段	
		String baseTarget = scanArr[0] + "." + scanArr[1] + ".";
		targetList = paserTarget_2(scanArr,baseTarget);
		
		//解析需要排除的ip列表
		List<String> excludeTargetList = new ArrayList<String>();
		for(int i = 1; i < targetArr.length; i++){
			//要排除的IP段
			String[] excludeArr =  targetArr[i].split("\\.");
			//要排除的ip前两段	
			String excludeTarget = excludeArr[0]+"."+excludeArr[1]+".";
			//获取要排除的ip列表
			excludeTargetList.addAll(paserTarget_2(excludeArr,excludeTarget));
		}
		//移除要排除的ip
		if(null != excludeTargetList && excludeTargetList.size() > 0){
			targetList.removeAll(excludeTargetList);
		}
		
		return targetList;
	}
	
	/**
	 * <p>方法名: paserTarget_2</p>
	 * <p>描述: 解析ip的第三段</p>
	 * <p>修改时间: 2016年4月11日 上午10:52:30</p>  
	 * @author lidongyang@eversec.cn  
	 * @param scanArr
	 * @param baseTarget
	 * @return
	 */
	public static List<String> paserTarget_2(String [] scanArr,String baseTarget){
		List<String>  targetList = new ArrayList<String>();
		StringBuffer baseTargetBuf = new StringBuffer();
		if (scanArr[2].equals("*")) {
			for (int i = 0; i <= 255; i++) {
				if (baseTargetBuf.length() != 0) {
					baseTargetBuf = baseTargetBuf.delete(0, baseTargetBuf.length());
				}
				baseTargetBuf.append(baseTarget + i + ".");
				targetList.addAll(putTargetToList(scanArr, baseTargetBuf));
			}
		} else if (scanArr[2].contains("/")) {
			String[] str2Arr = scanArr[2].split("/");
			if (Integer.parseInt(str2Arr[0]) <= Integer.parseInt(str2Arr[1])) {
				for (int i = Integer.parseInt(str2Arr[0]); i <= Integer.parseInt(str2Arr[1]); i++) {
					if (baseTargetBuf.length() != 0) {
						baseTargetBuf = baseTargetBuf.delete(0, baseTargetBuf.length());
					}
					baseTargetBuf.append(baseTarget + i + ".");
					targetList.addAll(putTargetToList(scanArr, baseTargetBuf));
				}
			} else {
				for (int i = Integer.parseInt(str2Arr[1]); i <= Integer.parseInt(str2Arr[0]); i++) {
					if (baseTargetBuf.length() != 0) {
						baseTargetBuf = baseTargetBuf.delete(0, baseTargetBuf.length());
					}
					baseTargetBuf.append(baseTarget + i + ".");
					targetList.addAll(putTargetToList(scanArr, baseTargetBuf));
				}
			}
		} else {
			if (baseTargetBuf.length() != 0) {
				baseTargetBuf = baseTargetBuf.delete(0, baseTargetBuf.length());
			}
			baseTargetBuf.append(baseTarget + scanArr[2] + ".");
			targetList.addAll(putTargetToList(scanArr, baseTargetBuf));
		}
		
		return targetList;
	}
	
	/**
	 * <p>方法名: putTargetToList</p>
	 * <p>描述: 将IP或者域名解析后放在list中</p>
	 * <p>修改时间: 2016年12月1日 上午10:42:36</p>  
	 * @author lidongyang@eversec.cn  
	 * @param tempArr
	 * @param baseTargetBuf
	 * @return
	 */
	public static List<String> putTargetToList(String[] tempArr, StringBuffer baseTargetBuf) {
		List<String> targetList = new ArrayList<String>();
		StringBuffer target = new StringBuffer();
		if (tempArr[3].equals("*")) {
			for (int j = 0; j <= 255; j++) {
				if (target.length() != 0) {
					target = target.delete(0, target.length());
				}
				target.append(baseTargetBuf.toString() + j);
				targetList.add(target.toString());
			}
		} else if (tempArr[3].contains("/")) {
			String[] str3Arr = tempArr[3].split("/");
			if (Integer.parseInt(str3Arr[0].trim()) <= Integer.parseInt(str3Arr[1].trim())) {
				for (int j = Integer.parseInt(str3Arr[0]); j <= Integer.parseInt(str3Arr[1]); j++) {
					if (target.length() != 0) {
						target = target.delete(0, target.length());
					}
					target.append(baseTargetBuf.toString() + j);
					targetList.add(target.toString());
				}
			} else {
				for (int j = Integer.parseInt(str3Arr[1].trim()); j <= Integer.parseInt(str3Arr[0].trim()); j++) {
					if (target.length() != 0) {
						target = target.delete(0, target.length());
					}
					target.append(baseTargetBuf.toString() + j);
					targetList.add(target.toString());
				}
			}
		} else {
			if (target.length() != 0) {
				target = target.delete(0, target.length());
			}
			target.append(baseTargetBuf.toString() + tempArr[3]);
			targetList.add(target.toString());
		}
		return targetList;
	}
	
	/**
	 * <p>方法名: isNumeric</p>
	 * <p>描述: 判断字符串是否是数字</p>
	 * <p>修改时间: 2016年12月1日 上午10:42:49</p>  
	 * @author lidongyang@eversec.cn  
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){  
        Pattern pattern = Pattern.compile("[0-9]*");  
        return pattern.matcher(str).matches();     
    }  
	
	public static void main(String[] args) {
		/**
		 * 支持解析的ip段格式
		 *1、192.168.*.*
		 *2、192.168.*.1/24
		 *3、192.168.1.*
		 *4、192.168.1.1/24
		 *5、192.168.1/24.1/24
		 *6、192.168.1.1/24-192.168.1.23-192.168.1.21  表示扫描1-24，但是不扫描23和21
		 *7、192.168.1.1/24-192.168.1.3/24 在1到24中排除3到24，只扫描1和2
		 *8、192.168.1/24.*-192.168.1/23.*-192.168.24.1/100
		 *9、192.168.*.*-192.168.3/256.*-192.168.1.*
		 */
		String targets = "192.168.1.1/24-192.168.1.3/24";
		List<String> targetList = parserTargets(targets);
		for(String target : targetList){
			System.out.println(target);
		}
		System.out.println(targetList.size());
	}
}
