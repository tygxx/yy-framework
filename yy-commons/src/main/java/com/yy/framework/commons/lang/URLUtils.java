package com.yy.framework.commons.lang;

import org.apache.commons.lang.StringUtils;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>类名: URLUtils</p>
 * <p>描述: internet地址工具类</p>
 * <p>公司： www.eversec.com.cn</p>
 * <p>修改时间: 2016年10月17日 上午10:38:12</p> 
 * @author lidongyang@eversec.cn
 * @author mateng@eversec.cn
 */
public class URLUtils {
	
	private static String DOMAIN_RULES = "ac.cn|com.cn|net.cn|org.cn|gov.cn|com.hk|公司|中国|网络|我爱你|网址|集团|com|net|org|int|edu|gov"
			+ "|mil|arpa|Asia|biz|info|name|pro|coop|aero|museum|ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|az|ba|bb"
			+ "|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cf|cg|ch|ci|ck|cl|cm|cn|co|cq|cr|cu|cv|cx|cy|cz"
			+ "|de|dj|dk|dm|do|dz|ec|ee|eg|eh|es|et|ev|fi|fj|fk|fm|fo|fr|ga|gb|gd|ge|gf|gh|gi|gl|gm|gn|gp|gr|gt|gu|gw|gy"
			+ "|hk|hm|hn|hr|ht|hu|id|ie|il|in|io|iq|ir|is|it|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr"
			+ "|ls|lt|lu|lv|ly|ma|mc|md|me|mg|mh|ml|mm|mn|mo|mp|mq|mr|ms|mt|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr"
			+ "|nt|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|pt|pw|py|qa|re|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn"
			+ "|so|sr|st|su|sy|sz|tc|td|tf|tg|th|tj|tk|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|us|uy|va|vc|ve|vg|vn|vu|wf|ws"
			+ "|ye|yu|za|zm|zr|zw|wang|group|xin|shop|ltd|club|top|xyz|site|vip|ren|red|link|mobi|ink|kim";
	
	/**
	 * <p>方法名: isUrl</p>
	 * <p>描述: 判断是否是url</p>
	 * <p>修改时间: 2016年12月1日 上午10:38:06</p>  
	 * @author lidongyang@eversec.cn  
	 * @param url 域名字符串
	 * @return true/false
	 */
	public static boolean isUrl(String url) {
		String domain = getDomain(url);
		
		//端口合法性检验
        String [] arr = domain.split(":");
        if (arr.length == 2) {
        	int port = Integer.parseInt(arr[1]);
        	if(port < 1 || port > 65535){
        		return false;
        	}
        	domain = arr[0];
        }
		
		//过滤掉不合法的ip格式的url
		arr = domain.split("\\.");
		if(arr.length == 4){
			if(StringUtils.isNumeric(arr[0])
					&& StringUtils.isNumeric(arr[1])
					&& StringUtils.isNumeric(arr[2])
					&& StringUtils.isNumeric(arr[3])
					&& !IPUtils.isIp(domain)){
				return false;
			}
		}
		
		String regex = "^((https|http|ftp|rtsp|mms)?://)"
				+ "?(([0-9a-zA-Z_!~*'().&=+$%-]+: )?[0-9a-zA-Z_!~*'().&=+$%-]+@)?" // ftp的user@
				+ "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
				+ "|" // 允许IP和DOMAIN（域名）
				+ "(([0-9a-zA-Z][0-9a-zA-Z-]{0,61})?[0-9a-zA-Z]+\\.)?" // 域名www.
				+ "(([0-9a-zA-Z\u4e00-\u9fa5][0-9a-zA-Z-\u4e00-\u9fa5]{0,61})?[0-9a-zA-Z\u4e00-\u9fa5]\\.)+" // 二级域名
				+ "(" + DOMAIN_RULES + "))" // first level domain- .com or .museum
				+ "(:[0-9]{1,4})?" // 端口- :80
				+ "((/?)|" // a slash isn't required if there is no file name
				+ "(/[0-9a-zA-Z_!~*'().;?:@&=+$,%#-]+)+/?)$";
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(domain);
		return matcher.matches();
	}
	
	/**
	 * <p>方法名: parseUrl</p>
	 * <p>描述: 解析http url地址</p>
	 * <p>修改时间: 2016年10月17日 上午10:43:59</p>  
	 * @author lidongyang@eversec.cn  
	 * @param url url
	 * @return 返回map，map里面包括协议，端口，地址等
	 */
	public static Map<String, String> parseUrl(String url){
		Map<String, String> map = new HashMap<String, String>();
		String protocol = "";//协议
		//解析协议
		if (url.startsWith("https")) {
			protocol = "https";
		} else {
			protocol = "http";
		}
		
		String[] arr = url.split(":");
		
		if(arr.length == 1){//不包含协议和端口
			map = parseTarget(arr[0]);
		}else if(arr.length == 2){//包含协议和端口中的一个
			if(arr[0].contains("http")){//包含协议
				map = parseTarget(arr[1]);
			}else{//包含端口
				map = parseTarget(arr[0],arr[1]);
			}
		}else if(arr.length == 3){//既包含协议又包含端口
			map = parseTarget(arr[1],arr[2]);
		}
		
		map.put("protocol", protocol);
		return map;
	}
	
	/**
	 * <p>方法名: parseTarget</p>
	 * <p>描述: 解析访问地址</p>
	 * <p>修改时间: 2016年10月17日 下午3:23:57</p>  
	 * @author lidongyang@eversec.cn  
	 * @param target
	 * @return
	 */
	private static Map<String, String> parseTarget(String target){
		Map<String, String> map = new HashMap<String, String>();
		String ip = "";
		String portNum = "";//端口号
		String projectName = "";//项目名
		target = target.replace("//", "");
		int index = target.indexOf("/");
		if(index != -1){
			if(target.length() > index+1){
				projectName = target.substring(index+1);
			}
			target = target.substring(0, index);
		}
		if(IPUtils.isIp(target)){
			ip = target;
		}else{
			ip = getDomainIp(target);
		}
		portNum = "80";
		
		map.put("ip", ip);
		map.put("portNum", portNum);
		map.put("projectName", projectName);
		
		return map;
	}
	
	/**
	 * <p>方法名: parseTarget</p>
	 * <p>描述: 解析访问地址</p>
	 * <p>修改时间: 2016年10月17日 下午3:26:29</p>  
	 * @author lidongyang@eversec.cn  
	 * @param containsIpStr
	 * @param containsPortStr
	 * @return
	 */
	private static Map<String, String> parseTarget(String containsIpStr, String containsPortStr){
		Map<String, String> map = new HashMap<String, String>();
		String ip = "";
		String portNum = "";//端口号
		String projectName = "";//项目名
		String target = containsIpStr.replaceAll("//", "");
		if(IPUtils.isIp(target)){
			ip=target;
		}else{
			ip = getDomainIp(target);
		}
		portNum = containsPortStr;
		int index = portNum.indexOf("/");
		if(index != -1){
			if(portNum.length() > index+1){
				projectName = portNum.substring(index+1);
			}
			portNum = portNum.substring(0, index);
		}

		map.put("ip", ip);
		map.put("portNum", portNum);
		map.put("projectName", projectName);
		
		return map;
	}
	
	/**
	 * <p>方法名: getDomain</p>
	 * <p>描述: 获取域名</p>
	 * <p>修改时间: 2017年6月19日 下午6:17:56</p>  
	 * @author lidongyang@eversec.cn  
	 * @param url
	 * @return
	 */
	public static String getDomain(String url) {
		Pattern patternForProtocal = Pattern.compile("[\\w]+://");
		String domain = patternForProtocal.matcher(url).replaceAll("");
        int i = StringUtils.indexOf(domain, "/", 1);
        if (i > 0) {
            domain = StringUtils.substring(domain, 0, i);
        }
        return domain;
    }
	
	/**
	 * <p>方法名: getDomainIp</p>
	 * <p>描述: 解析域名，获取ip</p>
	 * <p>修改时间: 2016年10月17日 上午10:40:15</p>  
	 * @author lidongyang@eversec.cn  
	 * @param url 访问地址
	 * @return ip
	 */
	public static String getDomainIp(String url){
		String ip = null;
		InetAddress address = null;
		try {
			address = InetAddress.getByName(url);
			ip = address.getHostAddress();
		} catch (Exception e) {
			System.out.println("非法地址："+url);
			//e.printStackTrace();
		}
		return ip;
	}
	
	public static void main(String[] args) {
		/*System.out.println(isUrl("192.168.100.12"));
		System.out.println(isUrl("http://dadadada.d23.cn"));
		System.out.println(isUrl("192.168.200.264:80/aaa/bbb.html"));
		System.out.println(isUrl("http://192.168.102.135:20120/task/screenShotResultSubmit"));*/
		System.out.println(isUrl("eeib.wang"));
//		String url = "http://b.cloud.189.cn";
//		Map<String, String> map = parseUrl(url);
//		System.out.println("url = "+url);
//		System.out.println(map);
//		System.out.println();
//		
//		url = "http://www.baidu.com";
//		System.out.println("url = "+url);
//		System.out.println(isSimpleDomain(url));
//		System.out.println(isDomain(url));
//		System.out.println();
//		
//		url = "192.168.101.84";
//		map = parseUrl(url);
//		System.out.println("url = "+url);
//		System.out.println(map);
//		System.out.println();
//		
//		url = "192.168.101.84/Satanbox";
//		map = parseUrl(url);
//		System.out.println("url = "+url);
//		System.out.println(map);
//		System.out.println();
//		
//		url = "https://www.baidu.com";
//		map = parseUrl(url);
//		System.out.println("url = "+url);
//		System.out.println(map);
//		System.out.println();
//		
//		url = "http://192.168.101.84";
//		map = parseUrl(url);
//		System.out.println("url = "+url);
//		System.out.println(map);
//		System.out.println();
//		
//		url = "www.baidu.com:8080";
//		map = parseUrl(url);
//		System.out.println("url = "+url);
//		System.out.println(map);
//		System.out.println();
//		
//		
//		url = "192.168.101.84:8080/Satanbox";
//		map = parseUrl(url);
//		System.out.println("url = "+url);
//		System.out.println(map);
//		System.out.println();
//		
//		url = "http://192.168.101.84:8080";
//		map = parseUrl(url);
//		System.out.println("url = "+url);
//		System.out.println(map);
//		System.out.println();
//		
//		url = "http://192.168.101.84:8080/Satanbox";
//		map = parseUrl(url);
//		System.out.println("url = "+url);
//		System.out.println(map);
	
	
	}
	
}
