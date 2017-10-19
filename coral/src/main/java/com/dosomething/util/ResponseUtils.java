package com.dosomething.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.dosomething.commons.constants.ApiCodeConstants;


public class ResponseUtils {
	
	public static final String JSON_CONTENT_TYPE = "application/json; charset=UTF-8";
	
	public static void sendResponse(HttpServletResponse response, String message) throws IOException {
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = null;

		try {
			int contentLength = StringUtil.countByteArrayLengthOfString(message);
			
			response.setContentLength(contentLength);
			writer = response.getWriter();
			writer.write(message);
			writer.flush();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static void sendJsonSuccessResponse(HttpServletResponse response)throws IOException {
    	sendJsonResponse(response, JSONUtils.getJSONString("status", ApiCodeConstants.STATUS_SUCCESS));
	}
	
    public static void sendJsonResponse(HttpServletResponse response, Object obj)throws IOException {
    	sendJsonResponse(response, JSONUtils.toJsonString(obj));
	}
    
    public static void sendJsonResponse(HttpServletResponse response, String json)throws IOException {
    	response.setContentType(JSON_CONTENT_TYPE);
    	ResponseUtils.sendResponse(response, json);	
	}
    
    public static void sendJsonErrorResponse(HttpServletResponse response, Object errorObj) throws IOException {
    	sendJsonErrorResponse(response, JSONUtils.toJsonString(errorObj));
    }
    
    public static void sendJsonErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
    	response.setContentType(JSON_CONTENT_TYPE);
        ResponseUtils.sendResponse(response, JSONUtils.getJSONString("error", errorMessage));
    }
    
    public static void sendJsonErrorResponse(HttpServletResponse response, String errorMessage, String statusCode) throws IOException {
    	response.setContentType(JSON_CONTENT_TYPE);
        ResponseUtils.sendResponse(response, JSONUtils.getJSONString("status", statusCode, "error", errorMessage));
    }
    
    public static void sendJsonLoginErrorResponse(HttpServletResponse response, String errorMessage, String statusCode, String newRandomCode) throws IOException {
    	response.setContentType(JSON_CONTENT_TYPE);
		ResponseUtils.sendJsonResponse(response,
			JSONUtils.getJSONString("status", statusCode, "error", errorMessage, "new_valid", newRandomCode));
	}
}