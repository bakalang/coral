package com.dosomething.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dosomething.util.JSONUtils;
import com.dosomething.util.LogUtils;

	
public class TwseConnect implements Runnable {

	private final String url = "http://www.twse.com.tw/exchangeReport/STOCK_DAY";
	private String date;	
	private String stockId;		
	private List<Object> respList;		
	
	public TwseConnect(String date, String stockId) {
		super();
		this.date = date;
		this.stockId = stockId;
	}
	
	@Override
	public void run(){
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("response", "json");
			params.put("date", date);
			params.put("stockNo", stockId);
			
			String rsMessage = ApiCaller.post(url, params, null, 5000);
			Map<String, Object> map = JSONUtils.jsonToMap(rsMessage, String.class, Object.class);
			this.respList = (List<Object>) map.get("data");
			System.out.println(this.respList);
			LogUtils.coral.debug("done");			
		} catch (IOException e) {
			LogUtils.coral.error(e.getMessage(), e);
		}		
	}

	public List<Object> getRespList() {
		return respList;
	}
}
