/*
 * Copyright (c) 2012-2032 ACCA.
 * All Rights Reserved.
 */
package com.wjb.zero.util.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;

/**
 * @version Zero v1.0
 * @author 
 */
public class DateTimeUtils {

	public static String getCurrentDate() {
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.format(new Date());
	}
	
	public static String getCurrentMonth() {
		DateFormat df = new SimpleDateFormat("yyyyMM");
		return df.format(new Date());
	}
	
	public static String getCurrentTime() {
		DateFormat df = new SimpleDateFormat("HHmmss");
		return df.format(new Date());
	}

	public static String getCurrentDateTime() {
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(new Date());
	}

	public static boolean isValidDate(String dateStr) {// 20091001字符串
		if (StringUtils.isEmpty(dateStr)) {
			return false;
		}
		if (!StringUtils.isNumeric(dateStr)) {
			return false;
		}
		if (dateStr.length() != 8) {
			return false;
		}
		int d = Integer.parseInt(dateStr.substring(6, 8));
		int m = Integer.parseInt(dateStr.substring(4, 6));
		int y = Integer.parseInt(dateStr.substring(0, 4));

		if (d < 1 || m < 1 || m > 12)
			return false;

		if (m == 2) {
			if (isLeapYear(y))
				return d <= 29;
			else
				return d <= 28;
		} else if (m == 4 || m == 6 || m == 9 || m == 11)
			return d <= 30;
		else
			return d <= 31;
	}

	public static boolean isValidDate(int y, int m, int d) {// 20091001字符串

		if (d < 1 || m < 1 || m > 12)
			return false;

		if (m == 2) {
			if (isLeapYear(y))
				return d <= 29;
			else
				return d <= 28;
		} else if (m == 4 || m == 6 || m == 9 || m == 11)
			return d <= 30;
		else
			return d <= 31;
	}

	public static boolean isLeapYear(int y) {// 判断是否为闰年
		return y % 4 == 0 && (y % 400 == 0 || y % 100 != 0);
	}
	/**
	 * 根据传进的日期 对日期的加减操作
	 * @author 
	 * @param month 日期
	 * @param field 表示年,月.日等. 1年 2月
	 * @param value 表示要加减的数量.
	 * @param type 格式
	 * @return
	 */
    public static String lastYearOrMonth(String month,int field,int value,String type){
		   SimpleDateFormat aSimpleDateFormat = new SimpleDateFormat(type);
			GregorianCalendar gc=new GregorianCalendar(); 
			try {
				gc.setTime(aSimpleDateFormat.parse(month));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			gc.add(field,value); 
			
			return aSimpleDateFormat.format(gc.getTime());
	   }
    public static String dayTime(Date date,String type){
    	   SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			  // Calendar c = new GregorianCalendar();
			   Calendar calendar = Calendar.getInstance();
			   String newDay="";
			   if("newDay".equals(type)){
				   //系统当前时间
				newDay=df.format(date);
			   }else if("befOneDay".equals(type)){
				   //系统时间前两天
				   calendar.setTime(date);//设置参数时间
				   calendar.add(Calendar.DATE,-1);//把日期往后增加tian.整数往后推,负数往前移动
				   date=calendar.getTime(); //这个时间就是日期往后推一天的结果
				   //系统前两天
				newDay = df.format(date);
			   }else if("befFourDay".equals(type)){
				   //系统时间前两天
				   calendar.setTime(date);//设置参数时间
				   calendar.add(Calendar.DATE,-4);//把日期往后增加tian.整数往后推,负数往前移动
				   date=calendar.getTime(); //这个时间就是日期往后推一天的结果
				   //系统前两天
				newDay = df.format(date);
			   }
			   else if("befOneMonth".equals(type)){
				   //系统日期上月
				   calendar.setTime(date);//设置参数时间
				   calendar.add(Calendar.MONTH,-1);//把日期往后增加tian.整数往后推,负数往前移动
				   date=calendar.getTime(); //这个时间就是日期往后推一天的结果
				   //系统上月
				newDay = df.format(date);
			   }
			   else if("befTwoDay".equals(type)){
				   //系统时间前两天
				   calendar.setTime(date);//设置参数时间
				   calendar.add(Calendar.DATE,-2);//把日期往后增加tian.整数往后推,负数往前移动
				   date=calendar.getTime(); //这个时间就是日期往后推一天的结果
				   //系统前两天
				newDay = df.format(date);
			   }else if("minDay".equals(type)){
				   calendar.setTime(date);//设置参数时间
				   calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
					  //系统月初1号时间
				   newDay=df.format(calendar.getTime());
			   }else if("twoDay".equals(type)){
				   calendar.setTime(date);//设置参数时间
				   calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
				
				   calendar.setTime(calendar.getTime());//设置参数时间
				   calendar.add(Calendar.DATE,1);//把日期往后增加1天.整数往后推,负数往前移动
				   date=calendar.getTime(); //这个时间就是日期往后推一天的结果
					   //系统月初2号时间
				    newDay = df.format(date);
			   }else if("maxDay".equals(type)){
				   calendar.setTime(date);//设置参数时间
				   calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			       //系统月底时间
			     newDay=df.format(calendar.getTime());
			   }
			 
			 
			return newDay;
    }
    /**
     * 获取当月的 天数
     */
    public static int getCurrentMonthDay() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 根据年 月 获取对应的月份 天数
     */
    public static int getDaysByYearMonth(int year, int month) {

        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

}
