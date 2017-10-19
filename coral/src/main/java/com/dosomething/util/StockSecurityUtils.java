package com.dosomething.util;

import java.util.Date;

public class StockSecurityUtils {

	public static String getURLParameters(String url, Date date) {
		if(date == null) {
			return url+"&c=E";
		} else {
			String dateString = DateUtil.toString(date, FormatUtils.DATE_PATTERN_YYYYMMDD_HYPHEN);
			return url+"&c=E&e="+dateString+"&f="+dateString;
		}
	}
}
