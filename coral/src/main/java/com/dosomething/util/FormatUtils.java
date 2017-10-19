package com.dosomething.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * 時間/數字 的基本格式化物件
 * 
 * @author shipper
 * 
 */
public class FormatUtils {

	public final static String DATE_PATTERN_YYYYMMDD = "yyyyMMdd";
	public final static String DATE_PATTERN_YYYYMMDD_HYPHEN = "yyyy-MM-dd";
	
		/**
	 * 對日期作格式化
	 * 
	 * @param c
	 * @return
	 */
	public static String dateFormat(Calendar c) {
		return dateFormat(c, "dd/MM/yyyy HH:mm:ss");
	}

	/**
	 * 對日期作格式化
	 * 
	 * @param c
	 * @param format
	 * @return
	 */
	public static String dateFormat(Calendar c, String format) {
		return (c == null ? "" : DateFormatUtils.format(c, format));
	}

	/**
	 * 對日期作格式化
	 * 
	 * @param d
	 * @return
	 */
	public static String dateFormat(Date d) {
		return dateFormat(d, "dd/MM/yyyy HH:mm:ss", null);
	}

	public static String dateFormat(Date d, String format) {
		return dateFormat(d, format, null);
	}

	/**
	 * 對日期作格式化
	 * 
	 * @param d
	 * @param format
	 * @return
	 */
	public static String dateFormat(Date d, String format, Locale locale) {
		if (d == null) {
			return "";
		}
		if (locale != null) {
			return DateFormatUtils.format(d, format, locale);
		} else {
			return DateFormatUtils.format(d, format);
		}
	}

	/**
	 * 對日期作格式化
	 * 
	 * @param d
	 * @return
	 */
	public static String dateFormat(java.sql.Date d) {
		return dateFormat(d, "dd/MM/yyyy HH:mm:ss");
	}

	/**
	 * 對日期作格式化
	 * 
	 * @param d
	 * @param format
	 * @return
	 */
	public static String dateFormat(java.sql.Date d, String format) {
		return (d == null ? "" : DateFormatUtils.format(d, format));
	}

	public static String dateFormat(java.sql.Date d, String format, Locale locale) {
		return (d == null ? "" : DateFormatUtils.format(d, format, locale));
	}

	public static String dateFormat(Timestamp t) {
		return (t == null ? "" : DateFormatUtils.format(t, "dd/MM/yyyy HH:mm:ss"));
	}

	public static String dateFormat(Timestamp t, String format) {
		return (t == null ? "" : DateFormatUtils.format(t, format));
	}

	public static String dateFormat(Timestamp t, String format, Locale locale) {
		return (t == null ? "" : DateFormatUtils.format(t, format, locale));
	}
	
	/**
	 * 對日期作格式化
	 * 
	 * @param mt
	 * @return
	 */
	public static String dateFormat(long mt) {
		return DateFormatUtils.format(mt, "dd/MM/yyyy HH:mm:ss");
	}

	/**
	 * 對日期作格式化
	 * 
	 * @param mt
	 * @param format
	 * @return
	 */
	public static String dateFormat(long mt, String format) {
		return DateFormatUtils.format(mt, format);
	}

	/**
	 * 對日期作 timeline 格式化
	 * 
	 * @param c
	 * @return
	 */
	public static String dateFormatToTimeline(Calendar c) {
		return dateFormatToTimeline(c.getTime());
	}

	/**
	 * 對日期作 timeline 格式化
	 * 
	 * @param date
	 * @return
	 */
	public static String dateFormatToTimeline(Date date) {
		return ThreadLocalUtils.getSimpleDateFormat("MMM dd yyyy HH:mm:ss", Locale.US).format(date).concat(" GMT");
	}

	/**
	 * 對日期作 timeline 格式化
	 * 
	 * @param mt
	 * @return
	 */
	public static String dateFormatToTimeline(long mt) {
		return dateFormatToTimeline(new Date(mt));
	}

	/**
	 * 將指定的日期字串, 依照指定的格式轉成日期物件
	 * 
	 * @param text
	 * @param timeFormat
	 * @return
	 */
	public static Date parseDate(String pattern, String source) {

		try {
			return ThreadLocalUtils.getSimpleDateFormat(pattern).parse(source);
		} catch (ParseException e) {
			LogUtils.coral.error(e.getMessage(), e);
			return null;
		}
	}

	//---------------------------------------------------

	/**
	 * 對數字做格式化
	 * 
	 * @param number
	 * @return
	 */
	public static String numberFormat(BigDecimal bigDecimal) {
		return numberFormat(bigDecimal, "###,###.##");
	}

	/**
	 * 對數字做格式化
	 * 
	 * @param number
	 * @return
	 */
	public static String numberFormat(double number) {
		//return String.format("%,f", number);
		return numberFormat(number, "###,###.##");
	}

	/**
	 * 對數字做格式化
	 * 
	 * @param number
	 * @return
	 */
	public static String numberFormat(double number, String format) {
		return ThreadLocalUtils.getDecimalFormat(format).format(number);
	}

	/**
	 * 對數字做格式化
	 * 
	 * @param number
	 * @return
	 */
	public static String numberFormat(int number) {
		return String.format("%,d", number);
	}

	/**
	 * 對數字做格式化
	 * 
	 * @param number
	 * @return
	 */
	public static String numberFormat(int number, String format) {
		return ThreadLocalUtils.getDecimalFormat(format).format(number);
	}

	/**
	 * 對數字做格式化
	 * 
	 * @param number
	 * @return
	 */
	public static String numberFormat(long number) {
		return String.format("%,d", number);
	}

	/**
	 * 對數字做格式化
	 * 
	 * @param number
	 * @return
	 */
	public static String numberFormat(long number, String format) {
		return ThreadLocalUtils.getDecimalFormat(format).format(number);
	}

	/**
	 * 對數字做格式化
	 * 
	 * @param number
	 * @return
	 */
	public static String numberFormat(Number number) {
		return String.format("%,d", number);
	}

	/**
	 * 對數字做格式化
	 * 
	 * @param number
	 * @return
	 */
	public static String numberFormat(Number number, String format) {
		return ThreadLocalUtils.getDecimalFormat(format).format(number);
	}

	private FormatUtils() {
		throw new AssertionError();
	}
	
	public static String doAdminFormatNumberCustomizedNegative(Object aObject) {
		String result = doAdminFormatNumber(aObject);
		double x = Double.parseDouble(aObject.toString());
		if (x >= 0) {
			return result;
		}
		return "<span style=\"color: #FF0000;\">" + result + "</span>";
	}
	
	public static String doAdminFormatNumber(Object aObject) {
		int maximumFractionDigits = 2;
		boolean isParenthesesNegative = false;
		boolean isGrouping = true;
		RoundingMode roundingMode = RoundingMode.DOWN;
		int minimumFractionDigits = 2;
		return ThreadLocalUtils.getAdvancedDecimalFormat(maximumFractionDigits, isParenthesesNegative, isGrouping, roundingMode, minimumFractionDigits).format(aObject);
	}
	
	public static String doFormatNumberCustomizedNegative(Object aObject, int maximumFractionDigits, boolean isParenthesesNegative, boolean isGrouping, RoundingMode roundingMode, int minimumFractionDigits) {
		String result = doFormatNumber(aObject, maximumFractionDigits, isParenthesesNegative, isGrouping, roundingMode, minimumFractionDigits);
		double x = Double.parseDouble(aObject.toString());
		if (x >= 0) {
			return result;
		}
		return "<span style=\"color: #FF0000;\">" + result + "</span>";
	}
	
	public static String doFormatNumber(Object aObject, int maximumFractionDigits, boolean isParenthesesNegative, boolean isGrouping, RoundingMode roundingMode, int minimumFractionDigits) {
		return ThreadLocalUtils.getAdvancedDecimalFormat(maximumFractionDigits, isParenthesesNegative, isGrouping, roundingMode, minimumFractionDigits).format(aObject);
	}
	
	public static DecimalFormat getAdvancedDecimalFormat(int maximumFractionDigits, boolean isParenthesesNegative, boolean isGrouping, RoundingMode roundingMode, int minimumFractionDigits) {
		DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();
		if (decimalFormat.getMaximumFractionDigits() != maximumFractionDigits) {
			decimalFormat.setMaximumFractionDigits(maximumFractionDigits);	
		}				
		if (isParenthesesNegative) {
			decimalFormat.setNegativePrefix("(");
			decimalFormat.setNegativeSuffix(")");
		} else {
			decimalFormat.setNegativePrefix("-");
			decimalFormat.setNegativeSuffix("");
		}
		if (decimalFormat.isGroupingUsed() != isGrouping) {
			decimalFormat.setGroupingUsed(isGrouping);	
		}		
		if (decimalFormat.getRoundingMode() != roundingMode) {
			decimalFormat.setRoundingMode(roundingMode);	
		}
		if (decimalFormat.getMinimumFractionDigits() != minimumFractionDigits) {
			decimalFormat.setMinimumFractionDigits(minimumFractionDigits);	
		}
		return decimalFormat;
	}
}
