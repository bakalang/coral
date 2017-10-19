package com.dosomething.util;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

	public static String getPathInfo(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		int questionMark = pathInfo.indexOf("?");

		if (questionMark > -1) {
			pathInfo = pathInfo.substring(0, questionMark);
		}

		return pathInfo;
	}
}
