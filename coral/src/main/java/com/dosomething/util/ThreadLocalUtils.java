package com.dosomething.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

public class ThreadLocalUtils {
	
	//SimpleDateFormat is not thread-safe
	private static ThreadLocal<HashMap<String, SimpleDateFormat>> results = new ThreadLocal<HashMap<String, SimpleDateFormat>>( ) {
        protected HashMap<String, SimpleDateFormat> initialValue( ) {
            return new HashMap<String, SimpleDateFormat>();
        }
    };
	
    //DecimalFormat is not thread-safe
    private static ThreadLocal<HashMap<String, DecimalFormat>> decimalFormats = new ThreadLocal<HashMap<String, DecimalFormat>>( ) {
        protected HashMap<String, DecimalFormat> initialValue( ) {
            return new HashMap<String, DecimalFormat>();
        }
    };
    
	//Locale is not thread-safe
    private static ThreadLocal<HashMap<String, Locale>> locales = new ThreadLocal<HashMap<String, Locale>>( ) {
        protected HashMap<String, Locale> initialValue( ) {
            return new HashMap<String, Locale>();
        }
    };
    
    private static ThreadLocal<HashMap<String, DecimalFormat>> advancedDecimalFormats = new ThreadLocal<HashMap<String, DecimalFormat>>()
    {
    	protected HashMap<String, DecimalFormat> initialValue() {
            return new HashMap<String, DecimalFormat>();
        }
    };
    
	public static SimpleDateFormat getSimpleDateFormat(String format, Locale locale) {
		String key;
		if (locale != null) {
			key = format + "_" + locale;
		} else {
			key = format;
		}
		HashMap<String, SimpleDateFormat> hm = results.get();
		SimpleDateFormat obj = hm.get(key);
		if (obj != null) {
			return obj;
		}
		if (locale != null) {
			obj = new SimpleDateFormat(format, locale);
		} else {
			obj = new SimpleDateFormat(format);
		}
		hm.put(key, obj);
		return obj;
	}
	
	public static SimpleDateFormat getSimpleDateFormat(String format) {
		return getSimpleDateFormat(format, null);
	}
	
	public static DecimalFormat getDecimalFormat(String format) {
		HashMap<String, DecimalFormat> hm = decimalFormats.get();
		DecimalFormat obj = hm.get(format);
		if (obj != null) {
			return obj;
		}
		obj = new DecimalFormat(format);
		hm.put(format, obj);
		return obj;
	}
	
	public static DecimalFormat getAdvancedDecimalFormat(int maximumFractionDigits, boolean isParenthesesNegative, boolean isGrouping, RoundingMode roundingMode, int minimumFractionDigits) {
		String key = maximumFractionDigits + "_" + isParenthesesNegative + "_" + isGrouping + "_" + roundingMode.name() + "_" + minimumFractionDigits;
		HashMap<String, DecimalFormat> hm = advancedDecimalFormats.get();
		DecimalFormat obj = hm.get(key);
		if (obj != null) {
			return obj;
		}
		obj = FormatUtils.getAdvancedDecimalFormat(maximumFractionDigits, isParenthesesNegative, isGrouping, roundingMode, minimumFractionDigits);
		hm.put(key, obj);
		return obj;
	}
	
	public static Locale getLocale(String language, String country) {
		String key = language + "_" + country;
		HashMap<String, Locale> hm = locales.get();
		Locale obj = hm.get(key);
		if (obj != null) {
			return obj;
		}
		obj = new Locale(language, country);
		hm.put(key, obj);
		return obj;
	}
	
	
}
