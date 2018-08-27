package com.hec.app.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	private static SimpleDateFormat formatter;

	public static String getStringShortDate() {
		Date currentTime = new Date();
		formatter = new SimpleDateFormat("MM.dd.yyyy");
		return formatter.format(currentTime);
	}

	public static String getStringFormatData() {
		Date currentTime = new Date();
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(currentTime);
	}

	public static String getStringShortFormatData(String currentTime) {
		if (currentTime == null) {
			return null;
		}
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date date = formatter.parse(currentTime, pos);
		return formatter.format(date);
	}

	public static Date getShortDate(String strDate) {
		formatter = new SimpleDateFormat("MM.dd.yyyy");
		ParsePosition pos = new ParsePosition(0);
		return formatter.parse(strDate, pos);
	}

	public static Date getShortDate() {
		Date currentTime = new Date();
		formatter = new SimpleDateFormat("MM.dd.yyyy");
		String dateString = formatter.format(currentTime);
		ParsePosition pos = new ParsePosition(0);
		Date currentTime_2 = formatter.parse(dateString, pos);
		return currentTime_2;
	}

	public static String getDataFormat(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date;
		String endString;
		try {
			date = sdf.parse(strDate);
			SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
			endString = sdf2.format(date);

		} catch (Exception e) {
			endString = "";
		}
		return endString;
	}

	public static String getTsinaDataFormat(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss Z yyyy", Locale.ENGLISH);
		Date date;
		String endString;
		try {
			date = sdf.parse(str);
			SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm");
			endString = sdf2.format(date);

		} catch (Exception e) {
			endString = "";

		}
		return endString;
	}

	public static boolean isBefore(String dateString, int specifiedDayNumber) {
		final long DAY = 24L * 60L * 60L * 1000L;
		final Date currendDate = new Date();
		final Date beforeDate = getShortDate(dateString);
		final long dayPoor = (currendDate.getTime() - beforeDate.getTime()) / DAY;
		return dayPoor >= specifiedDayNumber;
	}

	public static boolean isBefore(String dateString, String dateString2) {
		return false;
	}

	public static boolean isBefore(Date date1, Date date2) {
		return false;
	}

	public static String formatDate(Long time) {
		Calendar nowcal = Calendar.getInstance();

		long longtime = (nowcal.getTimeInMillis() - time) / 1000;
		long sec = longtime % 60;
		long min = longtime / 60;
		long hour = longtime / (60 * 60);
		long day = longtime / (60 * 60 * 24);

		if (longtime >= 24 * 60 * 60)
			return day + "天前";
		else if (longtime > 60 * 60 && longtime < 24 * 60 * 60)
			return hour + "小时前";
		else if (longtime > 60 && longtime < 60 * 60)
			return min + "分钟前";
		else if (longtime > 10 && longtime < 60)
			return sec + "秒前";
		else
			return "刚刚";
	}

	public static Long getLongBytime(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Long mytiem = null;

		try {
			Date d1 = sdf.parse(time);
			mytiem = d1.getTime();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mytiem;
	}

	public static String formatTime(long seconds,Boolean truncateIfLong) {
		long minutes = seconds / 60;
		long hours = minutes / 60;
		seconds = seconds % 60;
		minutes = minutes % 60;
		//小时数超过100就显示成99
		if(truncateIfLong){
			if(hours>=100){
				hours=99;
			}
		}

		String secondsD = String.valueOf(seconds);
		String minutesD = String.valueOf(minutes);
		String hoursD = String.valueOf(hours);
		secondsD = secondsD.length() > 1 ? secondsD : "0" + secondsD;
		minutesD = minutesD.length() > 1 ? minutesD : "0" + minutesD;
		hoursD = hoursD.length() > 1 ? hoursD : "0" + hoursD;
//		String time = hoursD + " : " + minutesD + " : " + secondsD;
		String time = minutesD + ":" + secondsD;
		return time;
	}
}
