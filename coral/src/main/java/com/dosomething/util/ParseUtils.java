package com.dosomething.util;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 時間/數字 的基本解析物件
 * 
 * @author shipper
 * 
 */
public class ParseUtils {

	/**
	 * 使用 pattern 解析 source 為 Date
	 * 
	 * @param pattern
	 * @param source
	 * @return
	 */
	public static Date parseDate(String pattern, String source) {

		try {
			return ThreadLocalUtils.getSimpleDateFormat(pattern).parse(source);
		} catch (ParseException e) {
			LogUtils.coral.error(e.getMessage(), e);
		}

		return null;

	}

	/**
	 * 使用 pattern 解析 source 為 Date
	 * @param pattern
	 * @param source
	 * @param locale
	 * @return
	 */
	public static Date parseDate(String pattern, String source, Locale locale) {

		try {
			return ThreadLocalUtils.getSimpleDateFormat(pattern, locale).parse(source);
		} catch (ParseException e) {
			LogUtils.coral.error(e.getMessage(), e);
		}

		return null;

	}

	/**
	 * 使用 NumberFormat.getInstance() 解析 source 為 Number
	 * 
	 * @param source
	 * @return
	 */
	public static Number parseNumber(String source) {

		try {
			return NumberFormat.getInstance().parse(source);
		} catch (ParseException e) {
			LogUtils.coral.error(e.getMessage(), e);
		}

		return null;

	}

	/**
	 * 使用 NumberFormat.getInstance() 解析 source 為 Number
	 * 
	 * @param source
	 * @return
	 */
	public static Number parseNumber(String pattern, String source) {

		try {
			return ThreadLocalUtils.getDecimalFormat(pattern).parse(source);
		} catch (ParseException e) {
			LogUtils.coral.error(e.getMessage(), e);
		}

		return null;

	}

	/**
	 * 使用 pattern 解析 source 為 Timestamp
	 * 
	 * @param pattern
	 * @param source
	 * @return
	 */
	public static Timestamp parseTimestamp(String pattern, String source) {

		Date date = ParseUtils.parseDate(pattern, source);

		if (date == null || source == null || source.length() == 0) {
			return null;
		}
		return new Timestamp(date.getTime());

	}
	
	/**
	 * 使用 pattern 解析 source 為 java.sql.Date
	 * 
	 * @param pattern
	 * @param source
	 * @return
	 */
	public static java.sql.Date parseSQLDate(String pattern, String source) {

		Date date = ParseUtils.parseDate(pattern, source);
		
		if (date == null || source == null || source.length() == 0) {
			return null;
		}
		//reset 時分秒
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new java.sql.Date(cal.getTime().getTime());
	}

	private ParseUtils() {
		throw new AssertionError();
	}

}
