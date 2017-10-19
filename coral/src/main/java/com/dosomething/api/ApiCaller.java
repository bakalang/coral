package com.dosomething.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.dosomething.commons.model.Setting;
import com.dosomething.util.ApacheHttpClientManager;


public class ApiCaller {

	private ApiCaller() {
		throw new AssertionError();
	}

	private static CloseableHttpClient getPoolingHttpClient() {
		return ApacheHttpClientManager.getInstance().getHttpClient();
	}

	private static String execute(Request request,List<Header> headers,int timeout) throws ClientProtocolException, IOException{

		if (null != headers) {
			for (Header header : headers) {
				request.addHeader(header);
			}
		}

		request
			.connectTimeout(timeout)
			.socketTimeout(timeout);

		if(Setting.ENABLE_API_POOLING_HTTP_CONNECTION){

			return Executor.newInstance(getPoolingHttpClient())
				.execute(request)
				.returnContent()
				.asString();

		}else{

			return request
				.execute()
				.returnContent()
				.asString();
		}
	}

	public static String post(String uri, String jsonContent, List<Header> headers, int timeout)
		throws ClientProtocolException, IOException {

		Request request = Request.Post(uri);
		request.addHeader("Content-Type", "application/json");
		request.body(getParamsEntity(jsonContent));

		return execute(request,headers, timeout);
	}



	public static String post(String uri, Map<String, String> params, List<Header> headers, int timeout)
		throws ClientProtocolException, IOException {

		Request request = Request.Post(uri);
		request.bodyForm(getParamsList(params));

		return execute(request,headers, timeout);
	}
	
	public static String post(Map<String, String[]> params, String uri, List<Header> headers, int timeout)
		throws ClientProtocolException, IOException {

		Request request = Request.Post(uri);
		request.bodyForm(getParamsListForArray(params));

		return execute(request,headers, timeout);
	}

	public static String get(String uri, Map<String, String> params, List<Header> headers, int timeout)
		throws ClientProtocolException, IOException {

		return execute(Request.Get(uri + getParamsString(params)), headers, timeout);
	}

	private static String getParamsString(Map<String, String> paramMap) {
		StringBuffer params = new StringBuffer("?");
		for (Entry<String, String> param : paramMap.entrySet()) {
			params.append(param.getKey() + "=" + param.getValue() + "&");
		}
		return params.toString();
	}

	private static StringEntity getParamsEntity(String jsonContent) {

		try {
			if (null == jsonContent) {
				return new StringEntity("", "UTF-8");
			}
			return new StringEntity(jsonContent, "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	private static List<NameValuePair> getParamsList(Map<String, String> paramMap) {

		if (null == paramMap) {
			return new ArrayList<NameValuePair>();
		}

		return paramMap.entrySet().stream()
			.map(entry -> new BasicNameValuePair(entry.getKey(),entry.getValue()))
			.collect(Collectors.toList());
	}
	
	private static List<NameValuePair> getParamsListForArray(Map<String, String[]> paramMap) {

		if (null == paramMap) {
			return new ArrayList<NameValuePair>();
		}
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		for(Map.Entry<String, String[]> entry : paramMap.entrySet()){
			if(entry.getValue().length > 0){
				for(String attr : entry.getValue()){
					nameValuePairs.add(new BasicNameValuePair(entry.getKey(), attr));
				}
			}
		}

		return nameValuePairs;
	}
}
