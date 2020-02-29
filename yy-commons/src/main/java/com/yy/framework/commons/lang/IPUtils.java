package com.yy.framework.commons.lang;

import org.apache.commons.lang.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 类名称: IPUtils<br>
 * 类描述: IP地址处理的工具类<br>
 * 创建人: mateng@eversec.cn<br>
 * 创建时间: 2016年11月25日上午9:38:48<br>
 * 修改人: lidongyang@eversec.cn <br>
 * 修改时间: 2017年07月25日上午9:35:43<br>
 */
public class IPUtils {
	
	/**ip地址的正则表达式*/
	public static final String IP_MATCHER = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
	/**ip地址的正则表达式*/
	public static final Pattern PATTERN_IP_MATCHER = Pattern.compile(IP_MATCHER);
	
	/*
	 * 私有IP的范围
	 * A类 10.0.0.0 -10.255.255.255 
	 * B类 172.16.0.0 -172.31.255.255 
	 * C类 192.168.0.0 -192.168.255.255 
	 * D类 127.0.0.0 -127.255.255.255(环回地址)
	 */
	
	/**私有IP：A类 10.0.0.0*/
	public static final long A_BEGIN = ipToLong("10.0.0.0");
	/**私有IP：A类 10.255.255.255 */
	public static final long A_END = ipToLong("10.255.255.255");
	/**私有IP：B类 172.16.0.0*/
	public static final long B_BEGIN = ipToLong("172.16.0.0");
	/**私有IP：B类 172.31.255.255 */
	public static final long B_END = ipToLong("172.31.255.255");
	/**私有IP：C类 192.168.0.0*/
	public static final long C_BEGIN = ipToLong("192.168.0.0");
	/**私有IP：C类 192.168.255.255 */
	public static final long C_END = ipToLong("192.168.255.255");
	/**环回地址：D类 127.0.0.0*/
	public static final long D_BEGIN = ipToLong("127.0.0.0");
	/**环回地址：D类 127.255.255.255*/
	public static final long D_END = ipToLong("127.255.255.255");
	
	
	/**
	 * 判断字符串str是否为ip地址
	 * @param str 要判断的字符串
	 * @return true/false
	 */
	public static boolean isIp(String str) {
		Matcher matcher = PATTERN_IP_MATCHER.matcher(str);
		return matcher.matches();
	}
	
	/**
	 * <p>方法名: getLocalIP</p>
	 * <p>描述: 获取主机ip地址，自动区分windows还是linux</p>
	 * <p>修改时间: 2016年3月24日 下午5:34:02</p>  
	 * @author lidongyang@eversec.cn  
	 * @param unContainsNames 不包含的网卡名称，例如String[] unContainsNames = ["docker", "lo"]
	 * @return 本机IP
	 */
	public static String getLocalIP(String... unContainsNames) {
		InetAddress inetAddress = getLocal(unContainsNames);
		if(inetAddress != null) {
			return inetAddress.getHostAddress();
		}
		return null;
	}
	
	/**
	 * 获取本机网卡的信息
	 * @param unContainsNames 不包含的网卡名称，例如String[] unContainsNames = ["docker", "lo"]
	 * @return 网卡信息
	 */
	private static InetAddress getLocal(String... unContainsNames) {
		if (isWindowsOS()) {// 如果是Windows操作系统
			try {
				return InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
		} 
		
		// 如果是Linux操作系统
		InetAddress ip = null;
		Enumeration<NetworkInterface> netInterfaces = null;
		try {
			netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		while (netInterfaces.hasMoreElements()) {
			NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
			String name = ni.getName();
			boolean isFilter = false;
			for (String str : unContainsNames) {
				if(name.contains(str)) {
					isFilter = true;
				}
			}
			if(!isFilter) {
				// 遍历所有ip
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					ip = (InetAddress) ips.nextElement();
					if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
						return ip;
					}
				}
			}
		}
		return null;
	}

	/**
	 * <p>方法名: isWindowsOS</p>
	 * <p>描述: 判断操作系统是否是windows</p>
	 * <p>修改时间: 2016年3月24日 下午5:38:11</p>  
	 * @author lidongyang@eversec.cn  
	 * @return true/false
	 */
	public static boolean isWindowsOS() {
		boolean isWindowsOS = false;
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("windows") > -1) {
			isWindowsOS = true;
		}
		return isWindowsOS;
	}
	
	/**
	 * 点分十进制的ip地址转换为10进制long<br>
	 * 例如：将127.0.0.1形式的ip地址转换成10进制整数
	 * @param strIp 点分十进制的ip地址
	 * @return long类型的ip地址
	 */
	public static long ipToLong(String strIp) {
		if (StringUtils.isBlank(strIp)) {
			return 0;
		}
		String[] ipArr = strIp.split("\\.");
		if (ipArr.length != 4) {
			return 0;
		}
		long[] ip = new long[4];
		// 先找到IP地址字符串中.的位置
		// 将每个.之间的字符串转换成整型
		ip[0] = Long.parseLong(ipArr[0]);
		ip[1] = Long.parseLong(ipArr[1]);
		ip[2] = Long.parseLong(ipArr[2]);
		ip[3] = Long.parseLong(ipArr[3]);
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}  
	
	/**
	 * 将十进制的整数ip转换为点分十进制ip字符串
	 * @param longIp 类型的ip地址
	 * @return 点分十进制的ip地址
	 */
	public static String longToIP(long longIp) {
		StringBuffer sb = new StringBuffer("");
		// 直接右移24位
		sb.append(String.valueOf((longIp >>> 24)));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((longIp & 0x000000FF)));
		return sb.toString();
	}
	
	
	/**
	 * <p>方法名: ipToBytes</p>
	 * <p>描述: 把IP地址转化为字节数组</p>
	 * <p>修改时间: 2017年7月25日 上午10:23:21</p>  
	 * @author lidongyang@eversec.cn  
	 * @param ip 点分ip地址
	 * @return  字节数组
	 */
    public static byte[] ipToBytes(String ip) {
        try {
            return InetAddress.getByName(ip).getAddress();
        } catch (Exception e) {
            throw new IllegalArgumentException(ip + " is invalid IP");
        }
    }
    
    
    /**
     * <p>方法名: bytesToIp</p>
     * <p>描述: 字节数组转化为IP</p>
     * <p>修改时间: 2017年7月25日 上午10:26:17</p>  
     * @author lidongyang@eversec.cn  
     * @param bytes 字节数组
     * @return ip 点分ip地址
     */
    public static String bytesToIp(byte[] bytes) {
        return new StringBuffer().append(bytes[0] & 0xFF).append('.').append(
                bytes[1] & 0xFF).append('.').append(bytes[2] & 0xFF)
                .append('.').append(bytes[3] & 0xFF).toString();
    }
    
	/**
	 * <p>方法名: getIPLongScope</p>
	 * <p>描述: 根据带子网掩码的ip地址（如：192.168.1.1/24） 获取ip起始地址（long格式）</p> 
	 * <p>修改时间: 2017年7月25日 上午10:35:39</p>  
	 * @author lidongyang@eversec.cn  
	 * @param ipAndMask 带子网掩码的ip地址
	 * @return long[] 起始ip数组
	 */
    public static long[] getIPLongScope(String ipAndMask) {
        String[] ipArr = ipAndMask.split("/");
        if (ipArr.length != 2) {
            throw new IllegalArgumentException("invalid ipAndMask with: " + ipAndMask);
        }
        int netMask = Integer.valueOf(ipArr[1].trim());
        if(netMask == 32){
            return new long[] { ipToLong(ipArr[0]), ipToLong(ipArr[0]) };
        }
        if (netMask < 0 || netMask > 31) {
            throw new IllegalArgumentException("invalid ipAndMask with: " + ipAndMask);
        }
        long ipLong = ipToLong(ipArr[0]);
        long netIP = ipLong & (0xFFFFFFFF << (32 - netMask));
        long hostScope = (0xFFFFFFFF >>> netMask);
        return new long[] { netIP, netIP + hostScope };
    }
    
    /**
	 * <p>方法名: getIPStrScope</p>
	 * <p>描述: 根据带子网掩码的ip地址（如：192.168.1.1/24） 获取ip起始地址</p> 
	 * <p>修改时间: 2017年7月25日 上午10:35:39</p>  
	 * @author lidongyang@eversec.cn  
	 * @param ipAndMask 带子网掩码的ip地址
	 * @return String[] 起始ip数组
	 */
    public static String[] getIPStrScope(String ipAndMask) {
        long[] ipLongArr = getIPLongScope(ipAndMask);
        return new String[] { longToIP(ipLongArr[0]), longToIP(ipLongArr[1]) };
    }
    
    /**
     * <p>方法名: getIPLongScope</p>
     * <p>描述: 根据IP 子网掩码（192.168.1.1 255.255.255.0）获取ip起始地址（long格式）</p>
     * <p>修改时间: 2017年7月25日 上午10:39:39</p>  
     * @author lidongyang@eversec.cn  
     * @param ipStr ip地址
     * @param mask 子网掩码
     * @return long[] 起始ip数组
     */
    public static long[] getIPLongScope(String ipStr, String mask) {
        long ipLong;
        long netMaskLong = 0, ipcount = 0;
        try {
        	ipLong = ipToLong(ipStr);
            if (null == mask || "".equals(mask)) {
                return new long[] { ipLong, ipLong };
            }
            netMaskLong = ipToLong(mask);
            ipcount = ipToLong("255.255.255.255") - netMaskLong;
            long netIP = ipLong & netMaskLong;
            long hostScope = netIP + ipcount;
            return new long[] { netIP, hostScope };
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid ip scope express  ip:"
                    + ipStr + "  mask:" + mask);
        }
    }
    
    /**
     * <p>方法名: getIPStrScope</p>
     * <p>描述: 根据IP 子网掩码（192.168.1.1 255.255.255.0）获取ip起始地址</p>
     * <p>修改时间: 2017年7月25日 上午10:39:39</p>  
     * @author lidongyang@eversec.cn  
     * @param ipStr ip地址
     * @param mask 子网掩码
     * @return String[] 起始ip数组
     */
    public static String[] getIPStrScope(String ipStr, String mask) {
        long[] ipLongArr = getIPLongScope(ipStr, mask);
        return new String[] { longToIP(ipLongArr[0]), longToIP(ipLongArr[1]) };
    }
    
    /**
     * <p>方法名: getIPLongList</p>
     * <p>描述: 根据带子网掩码的ip地址（如：192.168.1.1/24） 获取ip列表（long格式）</p>
     * <p>修改时间: 2017年7月25日 上午10:50:06</p>  
     * @author lidongyang@eversec.cn  
     * @param ipAndMask 带子网掩码的ip地址
     * @return List ip列表
     */
    public static List<Long> getIPLongList(String ipAndMask){
    	long[] ipLongArr = getIPLongScope(ipAndMask);
    	List<Long> list = new ArrayList<Long>();
    	for(long i = ipLongArr[0]; i <= ipLongArr[1]; i++){
    		list.add(i);
    	}
    	
		return list;
    }
    
    /**
     * <p>方法名: getIPStrList</p>
     * <p>描述: 根据带子网掩码的ip地址（如：192.168.1.1/24） 获取ip列表</p>
     * <p>修改时间: 2017年7月25日 上午10:51:50</p>  
     * @author lidongyang@eversec.cn
     * @param ipAndMask 带子网掩码的ip地址
     * @return List ip列表
     */
    public static List<String> getIPStrList(String ipAndMask){
    	long[] ipLongArr = getIPLongScope(ipAndMask);
    	List<String> list = new ArrayList<String>();
    	for(long i = ipLongArr[0]; i <= ipLongArr[1]; i++){
    		list.add(longToIP(i));
    	}
    	
		return list;
    }
    
    /**
     * <p>方法名: getIPLongList</p>
     * <p>描述: 根据IP 子网掩码（192.168.1.1 255.255.255.0）获取ip列表（long格式）</p>
     * <p>修改时间: 2017年7月25日 上午10:51:58</p>  
     * @author lidongyang@eversec.cn  
     * @param ipStr ip地址
     * @param mask 子网掩码
     * @return List ip列表
     */
    public static List<Long> getIPLongList(String ipStr, String mask){
    	long[] ipLongArr = getIPLongScope(ipStr, mask);
    	List<Long> list = new ArrayList<Long>();
    	for(long i = ipLongArr[0]; i <= ipLongArr[1]; i++){
    		list.add(i);
    	}
    	
		return list;
    }
    
    /**
     * <p>方法名: getIPStrList</p>
     * <p>描述: 根据IP 子网掩码（192.168.1.1 255.255.255.0）获取ip列表</p>
     * <p>修改时间: 2017年7月25日 上午10:52:11</p>  
     * @author lidongyang@eversec.cn  
     * @param ipStr ip地址
     * @param mask 子网掩码
     * @return List ip列表
     */
    public static List<String> getIPStrList(String ipStr, String mask){
    	long[] ipLongArr = getIPLongScope(ipStr, mask);
    	List<String> list = new ArrayList<String>();
    	for(long i = ipLongArr[0]; i <= ipLongArr[1]; i++){
    		list.add(longToIP(i));
    	}
    	
		return list;
    }
    
    /**
     * 是否为私网ip（包括判断回环地址）
     * @author lidongyang@eversec.cn  
     * @param address ip地址
     * @return 是否为私网地址
     */
    public static boolean isInnerIP(String address) {
		long ip = ipToLong(address);
		return isInner(ip, A_BEGIN, A_END) || isInner(ip, B_BEGIN, B_END) || isInner(ip, C_BEGIN, C_END) || isInner(ip, D_BEGIN, D_END);
	}
    
    private static boolean isInner(long userIp, long begin, long end) {
		return (userIp >= begin) && (userIp <= end);
	}
    
	public static void main(String[] args) {
        String ipStr = "221.131.65.160/30";
        long[] arr = getIPLongScope(ipStr);
        System.out.println(arr[0]+"\t"+arr[1]+"\t"+(arr[1] - arr[0]+1));
	}
}
