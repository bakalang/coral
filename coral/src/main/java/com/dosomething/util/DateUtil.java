package com.dosomething.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DateUtils;

/**
 * also use DateUtils
 */
public class DateUtil {

	public static final int BEGIN = 0;

	public static final int END = 1;

	private DateUtil() {
		throw new AssertionError();
	}

	/**
	 * 設定時間部分(小時,分,秒,毫秒)的最小值
	 * 
	 * @param calendar
	 */
	public static void setTimeActualMinimum(Calendar calendar) {
		calendar.set(GregorianCalendar.HOUR_OF_DAY, calendar.getActualMinimum(GregorianCalendar.HOUR_OF_DAY));
		calendar.set(GregorianCalendar.MINUTE, calendar.getActualMinimum(GregorianCalendar.MINUTE));
		calendar.set(GregorianCalendar.SECOND, calendar.getActualMinimum(GregorianCalendar.SECOND));
		calendar.set(GregorianCalendar.MILLISECOND, calendar.getActualMinimum(GregorianCalendar.MILLISECOND));
	}

	/**
	 * 設定時間部分(小時,分,秒,毫秒)的最大值
	 * 
	 * @param calendar
	 */
	public static void setTimeActualMaximum(Calendar calendar) {
		calendar.set(GregorianCalendar.HOUR_OF_DAY, calendar.getActualMaximum(GregorianCalendar.HOUR_OF_DAY));
		calendar.set(GregorianCalendar.MINUTE, calendar.getActualMaximum(GregorianCalendar.MINUTE));
		calendar.set(GregorianCalendar.SECOND, calendar.getActualMaximum(GregorianCalendar.SECOND));
		calendar.set(GregorianCalendar.MILLISECOND, calendar.getActualMaximum(GregorianCalendar.MILLISECOND));
	}

	/**
	 * 取得指定日期的凌晨或是午夜時段
	 * 
	 * @param date
	 * @param point
	 *            (begin or end)
	 * @return
	 */
	public static Date getSpecifyDate(Date date, int point) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		if (point == BEGIN) {
			setTimeActualMinimum(calendar);
		} else {
			setTimeActualMaximum(calendar);
		}
		return (Date) calendar.getTime().clone();
	}

	/**
	 * 取得下一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNextDay(Date date) {
		return getNextNDay(date.getTime(), 1).getTime();
	}

	public static Calendar getNextDay(Timestamp date) {
		return getNextNDay(date.getTime(), 1);
	}

	/**
	 * 取得增減後的指定日期
	 * 
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date getNextNDay(Date date, int n) {
		return getNextNDay(date.getTime(), n).getTime();
		/*
		 * Calendar calendar = Calendar.getInstance(); calendar.setTime(date);
		 * calendar.add(GregorianCalendar.DAY_OF_MONTH, n); return (Date)
		 * calendar.getTime().clone();
		 */
	}

	public static Calendar getNextNDay(long timeMillis, int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		calendar.add(GregorianCalendar.DAY_OF_MONTH, n);
		return calendar;
		// return calendar.getTimeInMillis();
	}

	/**
	 * 由指定日期找出某一周的第一天或是最後一天
	 * 
	 * @param date
	 * @param point
	 * @return
	 */
	public static Date getSpecifyDateOfWeek(Date date, int point) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (point == BEGIN) {
			calendar.set(GregorianCalendar.DAY_OF_WEEK,
				calendar.getActualMinimum(GregorianCalendar.DAY_OF_WEEK));
			setTimeActualMinimum(calendar);
		} else {
			calendar.set(GregorianCalendar.DAY_OF_WEEK,
				calendar.getActualMaximum(GregorianCalendar.DAY_OF_WEEK));
			setTimeActualMaximum(calendar);
		}
		return (Date) calendar.getTime().clone();
	}

	/**
	 * 由指定日期找出增減月份後的某月第一天或最後一天
	 * 
	 * @param date
	 * @param point
	 * @return
	 */
	public static Date getSpecifyDateOfMonth(Date date, int point) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (point == BEGIN) {
			calendar.set(GregorianCalendar.DAY_OF_MONTH,
				calendar.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
		} else {
			calendar.set(GregorianCalendar.DAY_OF_MONTH,
				calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
		}
		return (Date) calendar.getTime().clone();
	}

	/**
	 * 由指定日期找出某年的第一天或最後一天
	 * 
	 * @param date
	 * @param point
	 * @return
	 */
	public static Date getSpecifyDateOfYear(Date date, int point) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (point == BEGIN) {
			calendar.set(GregorianCalendar.MONTH, calendar.getActualMinimum(GregorianCalendar.MONTH));
			calendar.set(GregorianCalendar.DAY_OF_MONTH,
				calendar.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
			setTimeActualMinimum(calendar);
		} else {
			calendar.set(GregorianCalendar.MONTH, calendar.getActualMaximum(GregorianCalendar.MONTH));
			calendar.set(GregorianCalendar.DAY_OF_MONTH,
				calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
			setTimeActualMaximum(calendar);
		}

		return (Date) calendar.getTime().clone();
	}

	/**
	 * 由指定年份找出某年的第一天或最後一天
	 * 
	 * @param year
	 * @param point
	 * @return
	 */
	public static Date getSpecifyDateOfYear(int year, int point) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(GregorianCalendar.YEAR, year);
		if (point == BEGIN) {
			calendar.set(GregorianCalendar.MONTH, calendar.getActualMinimum(GregorianCalendar.MONTH));
			calendar.set(GregorianCalendar.DAY_OF_MONTH,
				calendar.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
			setTimeActualMinimum(calendar);
		} else {
			calendar.set(GregorianCalendar.MONTH, calendar.getActualMaximum(GregorianCalendar.MONTH));
			calendar.set(GregorianCalendar.DAY_OF_MONTH,
				calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
			setTimeActualMaximum(calendar);
		}
		return (Date) calendar.getTime().clone();
	}

	public static Calendar getSixMonthBefore() {
		Calendar calendar = Calendar.getInstance(); // this takes current date
		int month = calendar.get(GregorianCalendar.MONTH);
		calendar.set(GregorianCalendar.MONTH, month - 6);
		calendar.set(GregorianCalendar.HOUR, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);

		return calendar;
	}

	public static Calendar getFirstDayOfMonth() {
		Calendar calendar = Calendar.getInstance(); // this takes current date
		calendar.set(GregorianCalendar.DAY_OF_MONTH, 1);
		calendar.set(GregorianCalendar.HOUR, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);

		// return new Timestamp(c.getTime().getTime());
		return calendar;
	}

	/**
	 * 是否在指定範圍內
	 * 
	 * @param targetDate
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean isBetween(Date targetDate, Date startDate, Date endDate) {
		GregorianCalendar c1 = new GregorianCalendar();
		GregorianCalendar c2 = new GregorianCalendar();
		c1.setTime(startDate);
		c2.setTime(endDate);
		return (startDate.before(targetDate) && endDate.after(targetDate));
	}

	/**
	 * 兩個日期間隔的月數
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int monthsBetween(Date startDate, Date endDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		int startMonth = calendar.get(GregorianCalendar.MONTH);
		int startYear = calendar.get(GregorianCalendar.YEAR);
		calendar.setTime(endDate);
		int endMonth = calendar.get(GregorianCalendar.MONTH);
		int endYear = calendar.get(GregorianCalendar.YEAR);
		return (endYear - startYear) * 12 + (endMonth - startMonth);
	}

	/**
	 * 兩個日期間隔的天數
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int daysBetween(Date startDate, Date endDate) {
		return (int) ((endDate.getTime() - startDate.getTime()) / (24 * 3600 * 1000));
	}

	/**
	 * 兩個日期間隔的天數
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int daysBetween(Timestamp startDate, Timestamp endDate) {
		return (int) ((endDate.getTime() - startDate.getTime()) / (24 * 3600 * 1000));
	}

	/**
	 * 兩個日期間隔的天數
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int daysBetween(java.sql.Date startDate, java.sql.Date endDate) {
		return (int) ((endDate.getTime() - startDate.getTime()) / (24 * 3600 * 1000));
	}

	/**
	 * 兩個日期間隔幾分鐘
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int minutesBetween(Date startDate, Date endDate) {
		return (int) ((endDate.getTime() - startDate.getTime()) / (60 * 1000));
	}

	public static int minutesBetween(Date startDate, long time) {
		return (int) ((time - startDate.getTime()) / (60 * 1000));
	}

	public static int minutesBetween(long startTime, long endTime) {
		return (int) ((endTime - startTime) / (60 * 1000));
	}

	/**
	 * 兩個日期間隔幾秒鐘
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long secondsBetween(Date startDate, Date endDate) {
		return (long) ((endDate.getTime() - startDate.getTime()) / (1000));
	}

	/**
	 * 兩個日期間隔幾秒鐘
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long secondsBetween(long startTime, long endTime) {
		return (long) ((endTime - startTime) / (1000));
	}

	/**
	 * 將字串轉成日期
	 * 
	 * @param dateStr
	 * @param pattern
	 * 
	 * @return
	 */
	public static Date toDate(String dateStr, String pattern) {
		try {
			return ThreadLocalUtils.getSimpleDateFormat(pattern).parse(dateStr);
		} catch (Exception e) {
			LogUtils.coral.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 將字串轉成日期
	 * 
	 * @param dateStr
	 * 
	 * @return
	 */
	public static Timestamp toTimestamp(String dateStr) {
		return Timestamp.valueOf(dateStr);
	}

	public static Timestamp toTimestamp(String dateStr, String pattern) {
		try {
			return new Timestamp(ThreadLocalUtils.getSimpleDateFormat(pattern).parse(dateStr).getTime());
		} catch (ParseException e) {
			LogUtils.coral.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 將日期轉成字串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String toString(Date date, String pattern) {
		DateFormat df = ThreadLocalUtils.getSimpleDateFormat(pattern);
		return df.format(date);
	}

	public static int getWeekNumber(Date date) {
		Calendar cd = Calendar.getInstance();
		cd.setTime(date);
		return cd.get(Calendar.DAY_OF_WEEK);
	}
	
	private static int getMondayPlus() {
		Calendar cd = Calendar.getInstance();
		// 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {
			return -6;
		} else {
			return 2 - dayOfWeek;
		}
	}

	public static String getLastMonday(String pattern) {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * -1);
		SimpleDateFormat sdf = ThreadLocalUtils.getSimpleDateFormat(pattern);
		return sdf.format(currentDate.getTime());
	}

	public static Date getLastMonday() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * -1);
		return currentDate.getTime();
	}

	public static String getLastSunday(String pattern) {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * -1 + 6);
		SimpleDateFormat sdf = ThreadLocalUtils.getSimpleDateFormat(pattern);
		return sdf.format(currentDate.getTime());
	}

	public static Date getLastSunday() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * -1 + 6);
		return currentDate.getTime();
	}

	public static Date getYesterday() {
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, -1);
		currentDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
		currentDate.set(GregorianCalendar.MINUTE, 0);
		currentDate.set(GregorianCalendar.SECOND, 0);
		currentDate.set(GregorianCalendar.MILLISECOND, 0);
		return currentDate.getTime();
	}

	public static Date getTomorrowEnd() {
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, 1);
		currentDate.set(GregorianCalendar.HOUR_OF_DAY, 23);
		currentDate.set(GregorianCalendar.MINUTE, 59);
		currentDate.set(GregorianCalendar.SECOND, 59);
		currentDate.set(GregorianCalendar.MILLISECOND, 0);
		return currentDate.getTime();
	}

	public static String getThisMonday(String pattern) {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus);
		SimpleDateFormat sdf = ThreadLocalUtils.getSimpleDateFormat(pattern);
		return sdf.format(currentDate.getTime());
	}

	public static Date getThisMonday() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus);
		return currentDate.getTime();
	}

	public static String getFirstDayOfThisMonth(String pattern) {
		return DateUtil.toString(DateUtil.getSpecifyDateOfMonth(new Date(), 0), pattern);
	}

	public static String getFirstDayOfPreviousMonth(String pattern) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		SimpleDateFormat sdf = ThreadLocalUtils.getSimpleDateFormat(pattern);
		return sdf.format(cal.getTime());
	}

	public static String getLastDayOfPreviousMonth(String pattern) {
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		SimpleDateFormat sdf = ThreadLocalUtils.getSimpleDateFormat(pattern);
		return sdf.format(cal.getTime());
	}

	public static String getFirstDayThisWeek(String pattern) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, 1);
		SimpleDateFormat sdf = ThreadLocalUtils.getSimpleDateFormat(pattern);
		return sdf.format(cal.getTime());
	}

	public static String getLastDayThisWeek(String pattern) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, 7);
		SimpleDateFormat sdf = ThreadLocalUtils.getSimpleDateFormat(pattern);
		return sdf.format(cal.getTime());
	}

	public static String getFirstDayNextWeek(String pattern) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_MONTH, 1);
		cal.set(Calendar.DAY_OF_WEEK, 1);
		SimpleDateFormat sdf = ThreadLocalUtils.getSimpleDateFormat(pattern);
		return sdf.format(cal.getTime());
	}

	public static String getLastDayNextWeek(String pattern) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_MONTH, 1);
		cal.set(Calendar.DAY_OF_WEEK, 7);
		SimpleDateFormat sdf = ThreadLocalUtils.getSimpleDateFormat(pattern);
		return sdf.format(cal.getTime());
	}

	public static String getDayByMoveDays(String pattern, int moveDays) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, moveDays);
		SimpleDateFormat sdf = ThreadLocalUtils.getSimpleDateFormat(pattern);
		return sdf.format(cal.getTime());
	}

	public static StringBuilder getDuration(int durationSeconds) {
		int hours = durationSeconds / (60 * 60);
		int leftSeconds = durationSeconds % (60 * 60);
		int minutes = leftSeconds / 60;
		int seconds = leftSeconds % 60;

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(addZeroPrefix(hours));
		sBuilder.append(":");
		sBuilder.append(addZeroPrefix(minutes));
		sBuilder.append(":");
		sBuilder.append(addZeroPrefix(seconds));

		return sBuilder;
	}

	public static String addZeroPrefix(int number) {
		if (number < 10) {
			return "0" + number;
		} else {
			return "" + number;
		}

	}

	/**
	 * isToday
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isToday(final Date date) {
		Date today = new Date();
		return date.getTime() >= getSpecifyDate(today, 0).getTime()
			&& date.getTime() <= getSpecifyDate(today, 1).getTime();
	}

	/**
	 * return 1:Sunday, 2:Monday .. etc.
	 * 
	 */
	public static int getFirstDayIndexOfThisMonth() {
		return getFirstDayOfMonth().getFirstDayOfWeek();
	}

	public static int getMaximumDaysOfAMonth(int addMonth) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, addMonth);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static String getThisYearMonth(String pattern) {
		Calendar cal = Calendar.getInstance();

		SimpleDateFormat sdf = ThreadLocalUtils.getSimpleDateFormat(pattern);
		return sdf.format(cal.getTime());
	}

	public static String getThisYearNextMonth(String pattern) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		SimpleDateFormat sdf = ThreadLocalUtils.getSimpleDateFormat(pattern);
		return sdf.format(cal.getTime());
	}

	public static String getLastDayOfNextMonth(String pattern) {
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		SimpleDateFormat sdf = ThreadLocalUtils.getSimpleDateFormat(pattern);
		return sdf.format(cal.getTime());
	}

	public static String getLastDayOfThisMonth(String pattern) {
		return DateUtil.toString(DateUtil.getSpecifyDateOfMonth(new Date(), 1), pattern);
	}

	public static boolean isBeforeToday(Timestamp aDate) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return aDate.before(new Timestamp(cal.getTime().getTime()));
	}

	public static String duration(long milliseconds, String format) {
		long dd = TimeUnit.MILLISECONDS.toDays(milliseconds);
		format = format.replace("dd", String.format("%d", dd));
		milliseconds -= TimeUnit.MILLISECONDS.convert(dd, TimeUnit.DAYS);
		long hh = TimeUnit.MILLISECONDS.toHours(milliseconds);
		format = format.replace("hh", String.format("%02d", hh));
		milliseconds -= TimeUnit.MILLISECONDS.convert(hh, TimeUnit.HOURS);
		long mm = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
		format = format.replace("mm", String.format("%02d", mm));
		milliseconds -= TimeUnit.MILLISECONDS.convert(mm, TimeUnit.MINUTES);
		long ss = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
		format = format.replace("ss", String.format("%02d", ss));
		return format;
	}

	public static boolean isBeforeToday11AM() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return System.currentTimeMillis() < cal.getTime().getTime();
	}

	public static long getStartOfToday() {
		Date today = new Date();
		return getSpecifyDate(today, 0).getTime();
	}

	/**
	 * 判斷傳入日期是否為當日
	 * 
	 * @param targetTime
	 * @return
	 */
	public static boolean isToday(long targetTime) {
		Calendar targetDate = Calendar.getInstance();
		targetDate.setTimeInMillis(targetTime);
		return DateUtils.isSameDay(targetDate, Calendar.getInstance());
	}

	/**
	 * 加秒數 - return新的Timestamp
	 * 
	 * @param original
	 * @param seconds
	 * @return
	 */
	public static Timestamp addSeconds(Timestamp original, int seconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(original.getTime());
		cal.add(Calendar.SECOND, seconds);
		return new Timestamp(cal.getTime().getTime());
	}

	public static String formatDate(Date date, String pattern) {
		if (date == null)
			throw new IllegalArgumentException("date is null");
		if (pattern == null) {
			throw new IllegalArgumentException("pattern is null");
		}
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.US);
		// formatter.setTimeZone(GMT);
		return formatter.format(date);
	}

	/**
	 * 根據傳入的日期，回傳該日期的Event起始時間。 同一天Event的定義=> 
	 * eventDate: 凌晨00:00:00 ~ 今天晚上23:59:59
	 * 
	 * @param thisDate
	 */
	public static Date getEventStartDate(Date thisDate) {
		return getEventStartDate(thisDate.getTime()).getTime();
	}

	public static Timestamp getEventStartTimestamp(Timestamp thisDate) {
		return new Timestamp(getEventStartDate(thisDate.getTime()).getTimeInMillis());
	}

	public static Calendar getEventStartDate(long millisecond) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millisecond);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
			0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	/**
	 * 根據傳入的日期，回傳該日期的Event起始時間。 同一天Event的定義 => 
	 * eventDate: 凌晨00:00:00 ~ 今天晚上23:59:59
	 * 
	 * @param thisDate
	 */
	public static Date getEventEndDate(Date thisDate) {
		return getEventEndDate(thisDate.getTime()).getTime();
	}

	public static Timestamp getEventEndTimestamp(Timestamp thisDate) {
		return new Timestamp(getEventEndDate(thisDate.getTime()).getTimeInMillis());
	}

	public static Calendar getEventEndDate(long millisecond) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millisecond);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
			23, 59, 59);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

}
