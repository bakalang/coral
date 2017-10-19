package com.dosomething.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.dosomething.commons.exceptions.ParameterNotFoundException;


public class PostReqParser {

	public static String getPostData(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = req.getReader();
			reader.mark(10000);

			String line = reader.readLine();
			while (line != null){
				sb.append(line).append("\n");
				line = reader.readLine();
			}
			reader.reset();
		} catch (IOException e) {
		}
		return sb.toString();
	}

	public static Map<String, Object> getJsonParameters(HttpServletRequest request) {
		String data = getPostData(request);
		if (data == null || data.length() == 0) {
			return null;
		}

		Map<String, Object> jsonMap = JSONUtils.jsonToMap(data, String.class, Object.class);
		if (jsonMap == null) {
			LogUtils.coral.error("[PostReqParser.getJsonParameters][Invalid Json Parameters] " + data);
		}

		return jsonMap;
	}

	public static String getStringParameter(Map<String, Object> map, String key, int length,
		String defaultValue) {
		try {
			String ret = (String) map.get(key);
			if (ret != null && ret.length() > length) {
				ret = ret.substring(0, length);
			}
			return ret;

		} catch (Exception e) {}
		return defaultValue;
	}

	public static String getStringParameter(Map<String, Object> map, String key, int length)
		throws ParameterNotFoundException {
		try {
			String ret = (String) map.get(key);
			if (ret != null && ret.length() > length) {
				ret = ret.substring(0, length);
			}
			return ret;

		} catch (Exception e) {
			throw new ParameterNotFoundException(key + " not found");
		}
	}

	public static Integer getIntParameter(Map<String, Object> map, String key, int length, int defaultValue) {
		try {
			return Integer.parseInt(getStringParameter(map, key, length));
		} catch (Exception e) {

		}
		return defaultValue;
	}

	public static Double getDoubleParameter(Map<String, Object> map, String key, int length) {
		return Double.parseDouble(getStringParameter(map, key, length));
	}
	public static Double getDoubleParameter(Map<String, Object> map, String key, int length,
		double defaultValue) {
		try {
			return Double.parseDouble(getStringParameter(map, key, length));
		} catch (Exception e) {

		}
		return defaultValue;
	}

	public static Long getLongParameter(Map<String, Object> map, String key, int length, long defaultValue) {
		try {
			return Long.parseLong(getStringParameter(map, key, length));
		} catch (Exception e) {

		}
		return defaultValue;
	}

	public static Boolean getBooleanParameter(Map<String, Object> map, String key, int length,
		boolean defaultValue) {

		try {
			String ret = (String) map.get(key);
			if (ret.equalsIgnoreCase("1") || (ret.equalsIgnoreCase("true")) || (ret.equalsIgnoreCase("on"))
				|| (ret.equalsIgnoreCase("yes"))) {
				return true;
			} else if (ret.equalsIgnoreCase("0") || (ret.equalsIgnoreCase("false"))
				|| (ret.equalsIgnoreCase("off")) || (ret.equalsIgnoreCase("no"))) {
				return false;
			}
		} catch (Exception e) {
		}
		return defaultValue;

	}
}