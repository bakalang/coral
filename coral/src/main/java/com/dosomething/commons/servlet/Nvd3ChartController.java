package com.dosomething.commons.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dosomething.bo.StockTWBO;
import com.dosomething.commons.dto.DailyStake;
import com.dosomething.util.JSONUtils;
import com.dosomething.util.LogUtils;
import com.dosomething.util.PostReqParser;
import com.dosomething.util.RequestParser;
import com.dosomething.util.RequestUtils;
import com.dosomething.util.ResponseUtils;
import com.fasterxml.jackson.core.JsonGenerator;

@SuppressWarnings("serial")
@WebServlet("/service/nvd3_chart/*")
public class Nvd3ChartController extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
		IOException {
		doProcess(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
		IOException {
		doProcess(request, response);
	}

	protected void doProcess(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		try {
			String pathInfo = RequestUtils.getPathInfo(request);

			if ("/top".equals(pathInfo)) {
				top(request, response, session);
			} else {
				System.err.println("incorrect pathInfo of " + pathInfo);
				ResponseUtils.sendResponse(response, pathInfo + " is not supported");
			}

		} catch (Exception e) {
			LogUtils.coral.error(e.getMessage(), e);
		}
	}

	private void top(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {

		try {
			String securityId = RequestParser.getStringParameter(request, 2000, "securityId", null);
			String days = RequestParser.getStringParameter(request, 2000, "days", null);
			String type = RequestParser.getStringParameter(request, 1, "type", null);

			Map<String, List<DailyStake>> rtnMap = new HashMap<String, List<DailyStake>>();
			
			for(String stockId : StockTWBO.getTopSecurityTrade("1040", type, 10)){
				 List<DailyStake> stList = StockTWBO.getSecurityTradeByStockIdAndSecurityId(stockId, "1040");
				 System.out.println(stockId+", "+securityId+", "+stList);
				 for(DailyStake st : stList){
					 st.setTradeDateMinSec(st.getCreatedDate().getTime());
				 }
				 System.out.println(stList);
				 rtnMap.put(stockId, stList);
			}
			
			ResponseUtils.sendJsonResponse(response, rtnMap);
		} catch (Exception e) {
			LogUtils.coral.error("[MobileAppService][queryWeatherHistory] " + e.getMessage(), e);
			ResponseUtils.sendJsonErrorResponse(response, e.getMessage());
			return;
		} 
		
	}
}
