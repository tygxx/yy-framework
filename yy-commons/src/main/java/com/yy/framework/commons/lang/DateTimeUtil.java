package com.yy.framework.commons.lang;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 类名称: DateTimeUtil<br>
 * 类描述: 日期时间工具类（apache DateFormatUtils、DateUtils的补充）<br>
 * 创建人: mateng@eversec.cn<br>
 * 创建时间: 2016年11月25日上午9:38:35<br>
 * 修改人: <br>
 * 修改时间: <br>
 */
public class DateTimeUtil {
	
	/**
	 * 时间格式 年-月-日 时:分:秒
	 */
	public static final String YYYY_MM_DD_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 日期格式 年-月-日
	 */
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	
	/**
	 * 日期格式 年-月
	 */
	public static final String YYYY_MM = "yyyy-MM";
	
	/**
	 * 格式化日期/时间至默认格式的字符串(yyyy-MM-dd HH:mm:ss).
	 * @param date the pattern to use to format the date
	 * @return 格式化后字符串格式的时间
	 */
	public static String formatDefault(Date date) {
		return DateFormatUtils.format(date, YYYY_MM_DD_HH_mm_ss);
	}
	
	/**
	 * 获取date日期所在月份的第一天的零点
	 * @param date 日期/时间，如果date为空，默认为当前日期
	 * @return 第一天的零点
	 */
	public static Date getFirstDateOfMonth(Date date){
		if(date == null) {
			date = new Date();
		}
		return DateUtils.truncate(date, Calendar.MONTH);
	}
	
	/**
	 * 获取date日期所在月份的最后一天的零点
	 * @param date 日期/时间，如果date为空，默认为当前日期
	 * @return 最后一天的零点
	 */
	public static Date getEndDateOfMonth(Date date){
		if(date == null) {
			date = new Date();
		}
		Date temp = DateUtils.addMonths(date, 1);
		temp = DateUtils.truncate(temp, Calendar.MONTH);
		return DateUtils.addDays(temp, -1);
	}
	
	/**
	 * 获取日期所在月份的总天数
	 * @param date 日期/时间，如果date为空，默认为当前日期
	 * @return 总天数
	 */
	public static int getDaysOfMonth(Date date) {
		Date end = getEndDateOfMonth(date);
		return (int) DateUtils.getFragmentInDays(end, Calendar.MONTH);
	}
	
	/**
	 * 获取两个时间点之间的月份数
	 * @param startDate 开始时间
	 * @param endDate	结束时间
	 * @return 月份集合
	 */
	public static List<String> getMonthListBetweenTwoDate(Date startDate,Date endDate){
		Date sd = DateUtils.truncate(startDate, Calendar.MONTH);
		Date ed = DateUtils.truncate(endDate, Calendar.MONTH);
		
		List<String> monthList = new ArrayList<String>();
		
		while(sd.compareTo(ed) <= 0){
			monthList.add(DateFormatUtils.format(sd, "MM"));
			sd = DateUtils.addMonths(sd, 1);
		}
		return monthList;
	}
	
	/**
	 * 获取两个时间点之间的年份数
	 * @param startDate 开始时间
	 * @param endDate	结束时间
	 * @return 年份集合
	 */
	public static List<String> getYearListBetweenTwoDate(Date startDate,Date endDate){
		Date sd = DateUtils.truncate(startDate, Calendar.YEAR);
		Date ed = DateUtils.truncate(endDate, Calendar.YEAR);
		
		List<String> yearList = new ArrayList<String>();
		
		while(sd.compareTo(ed) <= 0){
			yearList.add(DateFormatUtils.format(sd, "yyyy"));
			sd = DateUtils.addYears(sd, 1);
		}
		
		return yearList;
	}
	
	/**
	 * 获取两个时间点之间的天数集合
	 * @param startDate 开始时间
	 * @param endDate	结束时间
	 * @return 日期集合
	 */
	public static List<String> getDayListBetweenTwoDate(Date startDate,Date endDate){
		Date sd = DateUtils.truncate(startDate, Calendar.DATE);
		Date ed = DateUtils.truncate(endDate, Calendar.DATE);
		
		List<String> dayList = new ArrayList<String>();
		
		while(sd.compareTo(ed) <= 0){
			dayList.add(DateFormatUtils.format(sd, YYYY_MM_DD));
			sd = DateUtils.addDays(sd, 1);
		}
		
		return dayList;
	}
	
	/**
	 * <p>方法名: between</p>
	 * <p>描述: 计算两个时间差</p>
	 * <p>修改时间: 2016年3月24日 上午10:44:40</p>  
	 * @author jiangjun@eversec.cn  
	 * @param startDate 开始日期
	 * @param endDate	结束日期
	 * @return 带有单位的时间差，例如：x小时，xx分钟等
	 */
	public static String between(Date startDate, Date endDate){
		String str = "";
		long lo = endDate.getTime() - startDate.getTime();
		long second = lo/1000;
		long hour = second / 3600;
		long surplusSecond = second % 3600;
		if(hour > 0){
			str += hour+"小时";
		}
		if(surplusSecond > 0){
			long minu = surplusSecond / 60;
			long ssecond = surplusSecond % 60;
			if(minu > 0){
				str += minu+"分钟";
			}
			if(ssecond > 0){
				str += ssecond+"秒";
			}
		}
		return str;
	}
	
	/**
	 * 判断是否为时间格式字符串
	 * @param str 字符串格式的时间
	 * @param formatStr 时间的格式，例如：yyyy-MM-dd HH:mm:ss
	 * @return true/false
	 */
	public static boolean isValidDate(String str, String formatStr) {
		boolean convertSuccess = true;
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		try {
			//setLenient用于设置Calendar是否宽松解析字符串，如果为false，则严格解析；默认为true，宽松解析
			format.setLenient(false);
			format.parse(str);
		} catch (Exception e) {
			convertSuccess = false;
		}
		return convertSuccess;
	}
	
	public static void main(String[] args) {
		System.out.println(formatDefault(getFirstDateOfMonth(new Date())));
		System.out.println(formatDefault(getEndDateOfMonth(new Date())));
		System.out.println(getDaysOfMonth(new Date()));
		System.out.println(getDayListBetweenTwoDate(getFirstDateOfMonth(new Date()), new Date()));
		System.out.println(getMonthListBetweenTwoDate(DateUtils.addMonths(new Date(), -5), new Date()));
		System.out.println(getYearListBetweenTwoDate(DateUtils.addYears(new Date(), -5), new Date()));
	}
}
