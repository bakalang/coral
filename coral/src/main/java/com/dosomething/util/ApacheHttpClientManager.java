package com.dosomething.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;


/**
 * ApacheHttpClientManager
 * 
 * @author Stan
 *
 */
/**
 *
 */
public class ApacheHttpClientManager {

	private final static Log log = LogFactory.getLog(ApacheHttpClientManager.class);

	public static int CONNECTION_REQUEST_TIMEOUT = 5 * 1000;

	public static int CONNECTION_TIMEOUT = 5 * 1000;

	private final static String DEFALUT_CHARSET = "UTF-8";

	public static int MAX_PER_ROUTE = 200;

	public static int MAX_TOTAL = 500;

	public static int SOCKET_HOLD_TIMEOUT = 30 * 1000;

	private ScheduledExecutorService cleaner = null;

	private PoolingHttpClientConnectionManager cm = null;

	private CloseableHttpClient httpClient = null;

	public boolean isTrace = false;

	public boolean isTraceTimeout = true;

	private ConnectionKeepAliveStrategy keepAliveStrategy = null;

	protected RequestConfig requestConfig = null;

	private final static ApacheHttpClientManager theInstance = new ApacheHttpClientManager();

	public static ApacheHttpClientManager getInstance() {
		return theInstance;
	}

	private ApacheHttpClientManager() {

		try {

			initConnectionManager();

			initKeepAliveStrategy();

			connectionCleaner();

			initHttpClient();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * connectionCleaner
	 */
	private void connectionCleaner() {
		cleaner = Executors.newSingleThreadScheduledExecutor(ThreadFactoryUtils.getDaemonThreadFactory("ApacheHttpClientManager"));
		Runnable clean = new Runnable() {

			public void run() {
				cm.closeExpiredConnections();
				cm.closeIdleConnections(6, TimeUnit.SECONDS);
			}
		};
		cleaner.scheduleAtFixedRate(clean, 6, 3, TimeUnit.SECONDS);
	}

	private void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	private String getResponse(HttpEntity entity) throws Exception {
		InputStream in = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuffer contentSb = new StringBuffer();
		try {
			in = new BufferedInputStream(entity.getContent());
			isr = new InputStreamReader(in, "UTF-8");
			br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				contentSb.append(line.trim());
			}
			EntityUtils.consumeQuietly(entity);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			close(br);
			close(isr);
			close(in);
		}
		return contentSb.toString();
	}
	
	public void init(String propertiesFile) {
//		try (InputStream in = PlatformProxy.class.getResourceAsStream(propertiesFile)) {
//			Properties props = new Properties();
//			props.load(in);
//			CONNECTION_REQUEST_TIMEOUT = Integer.parseInt(props.getProperty("connection.request.timeout"));
//			CONNECTION_TIMEOUT = Integer.parseInt(props.getProperty("connection.timeout"));
//			SOCKET_HOLD_TIMEOUT = Integer.parseInt(props.getProperty("socket.timeout"));
//		} catch (IOException e) {
//			log.error(e.getMessage(), e);
//		}
	}
	
	public HTTPResponse execute(HttpGetRequest request) throws Exception {
		String url = request.getUrl();
		if(request.getQueryString() != null) {
			url = url + "?" + request.getQueryString();
		}
		Header[] headers = request.getHeaders();
		String charset = request.getCharset();
		if (charset == null) {
			charset = DEFALUT_CHARSET;
		}
		HttpClientContext proxyHttpContext = request.getHttpContext();
		RequestConfig requestConfig = request.getRequestConfig();
		
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Accept-Charset", charset);
		httpGet.setProtocolVersion(HttpVersion.HTTP_1_1);
		if (requestConfig != null) {
			httpGet.setConfig(requestConfig);
		}
		if (headers != null) {
			httpGet.setHeaders(headers);
		}

		CloseableHttpResponse response = null;
		String content = null;
		CookieStore cookieStore = null;
		int statusCode = 0;
		Header[] responseHeaders;
		try {

			if (proxyHttpContext != null) {
				response = httpClient.execute(httpGet, proxyHttpContext);
				if(request.receiveCookie()) {
					cookieStore = proxyHttpContext.getCookieStore();
				}
			} else {
				if(request.receiveCookie()) {
					HttpClientContext httpClientContext = HttpClientContext.create();
					response = httpClient.execute(httpGet, httpClientContext);
					cookieStore = httpClientContext.getCookieStore();
				} else {
					response = httpClient.execute(httpGet);
				}
			}
			responseHeaders = response.getAllHeaders();
			statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != 200 && statusCode != 404) {
				log.info("HTTP Status-Code (" + statusCode + ") Length w/o headers: [" + url.length() + "] " + url);
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				content = getResponse(entity);
			}
		} catch (HttpHostConnectException e) {	
			log.error(e.getMessage(), e);
			httpGet.abort();
			HttpHost host = e.getHost();
			PoolStats pstat = cm.getTotalStats();
			PoolStats pst = cm.getStats(new HttpRoute(host));
			log.info(String.format("Total PoolStats: %s & %s PoolStats: %s.", pstat, host.toHostString(), pst));
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			httpGet.abort();
			throw e;
		} finally {
			close(response);
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}
		if (isTrace) {
			log.info("HttpGet content:" + content);
		}
		return new HTTPResponse(statusCode, content, responseHeaders, (cookieStore == null ? null : cookieStore.getCookies()));
	}
	
	public HttpGetRequest getHttpGetRequest(String url) throws Exception {
		return new HttpGetRequest(url);
	}
	
	public HttpPostRequest getHttpPostRequest(String url) throws Exception {
		return new HttpPostRequest(url);
	}

	public CloseableHttpClient getHttpClient() {
		return this.httpClient;
	}
	/**
	 * executePostProcess
	 * 
	 * @param url
	 * @param headers
	 * @param pairs
	 * @param reqEntity
	 * @param proxyHttpClientContext
	 * @return
	 * @throws Exception
	 */
	public HTTPResponse execute(HttpPostRequest request) throws Exception {
		String url = request.getUrl(); 
		Header[] headers = request.getHeaders();
		HttpEntity httpEntity = request.getHttpEntity(); 
		HttpClientContext proxyHttpContext = request.getHttpContext();
		//HttpClientContext HttpClientContext;
		
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept-Charset", "UTF-8");
		httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);
		if (headers != null && headers.length > 0) {
			httpPost.setHeaders(headers);
		}

		if (httpEntity != null) {
			httpPost.setEntity(httpEntity);
		}
		
		RequestConfig requestConfig = request.getRequestConfig();

		if (requestConfig != null) {
			httpPost.setConfig(requestConfig);
		}
		
		CloseableHttpResponse response = null;
		String content = null;
		Header[] responseHeaders = null;
		int statusCode = -1;
		CookieStore cookieStore = null;
		try {
			if (proxyHttpContext != null) {
				response = httpClient.execute(httpPost, proxyHttpContext);
				if(request.receiveCookie()) {
					cookieStore = proxyHttpContext.getCookieStore();
				}
			} else {
				if(request.receiveCookie()) {
					HttpClientContext httpClientContext = HttpClientContext.create();
					response = httpClient.execute(httpPost, httpClientContext);
					cookieStore = httpClientContext.getCookieStore();
				} else {
					response = httpClient.execute(httpPost);
				}
			}
			responseHeaders = response.getAllHeaders();
			statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != 200 && statusCode != 404) {
				log.info("HTTP Status-Code (" + statusCode + ") Length w/o headers: [" + url.length() + "] " + url);
			}
			
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				content = getResponse(entity);
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			httpPost.abort();
			throw e;
		} finally {
			close(response);
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
		return new HTTPResponse(statusCode, content, responseHeaders, (cookieStore == null ? null : cookieStore.getCookies()));
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		cleaner.shutdown();
		cm.close();
	}

	/**
	 * initConnectionManager
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws KeyManagementException
	 */
	private void initConnectionManager() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		SSLContext sslContext = builder.build();
		HostnameVerifier verifier = new HostnameVerifier() {
			@Override
			public boolean verify(String s, SSLSession sslSession) {
				return true;
			}
		};
		
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", new MySSLConnectionSocketFactory(sslContext, verifier)).register("http", new MyConnectionSocketFactory()).build();

		cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

		// Increase max total connection to 500
		cm.setMaxTotal(MAX_TOTAL);
		// Increase default max connection per route to 50
		cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);

		SocketConfig defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).setSoKeepAlive(true).setSoReuseAddress(true).build();
		cm.setDefaultSocketConfig(defaultSocketConfig);

		MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200).setMaxLineLength(2000).build();
		ConnectionConfig defaultConnectionConfig = ConnectionConfig.custom().setMessageConstraints(messageConstraints).setMalformedInputAction(CodingErrorAction.IGNORE)
				.setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).build();
		cm.setDefaultConnectionConfig(defaultConnectionConfig);
	}

	/**
	 * initHttpClient
	 */
	private void initHttpClient() {
		requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_HOLD_TIMEOUT).setConnectTimeout(CONNECTION_TIMEOUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
				.setStaleConnectionCheckEnabled(true).build();
		httpClient = HttpClients.custom().setConnectionManager(cm).setKeepAliveStrategy(keepAliveStrategy).setDefaultRequestConfig(requestConfig)
				.setUserAgent(
						"mozilla/4.0 (compatible; msie 8.0; windows nt 6.1; wow64; trident/4.0; slcc2; .net clr 2.0.50727; .net clr 3.5.30729; .net clr 3.0.30729; media center pc 6.0; masn)")
				.disableAutomaticRetries().build();
	}

	/**
	 * initKeepAliveStrategy
	 */
	private void initKeepAliveStrategy() {
		keepAliveStrategy = new ConnectionKeepAliveStrategy() {

			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
				// Honor 'keep-alive' header

				Header[] hdrs = response.getHeaders(HTTP.CONN_KEEP_ALIVE);
				for (Header he : hdrs) {
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase("timeout")) {
						try {
							if (isTraceTimeout && Long.parseLong(value) * 1000 > 6) {
								log.info(Arrays.toString(hdrs));
							}
							return Long.parseLong(value) * 1000;
						} catch (NumberFormatException ignore) {
						}
					}
				}

				// Keep alive for 6 seconds only
				return 6 * 1000;
			}
		};
	}
	

	class MyConnectionSocketFactory implements ConnectionSocketFactory {
		@Override
		public Socket createSocket(final HttpContext context) throws IOException {
			InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");

			if (socksaddr == null) {
				return new Socket();
			}
			return new Socket(new Proxy(Proxy.Type.SOCKS, socksaddr));
		}

		@Override
		public Socket connectSocket(final int connectTimeout, final Socket socket, final HttpHost host, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress,
				final HttpContext context) throws IOException, ConnectTimeoutException {
			Socket sock;
			if (socket != null) {
				sock = socket;
			} else {
				sock = createSocket(context);
			}
			if (localAddress != null) {
				sock.bind(localAddress);
			}
			try {
				sock.connect(remoteAddress, connectTimeout);
			} catch (SocketTimeoutException ex) {
				throw new ConnectTimeoutException(ex, host, remoteAddress.getAddress());
			}
			return sock;
		}

	}

	class MySSLConnectionSocketFactory extends SSLConnectionSocketFactory {

		public MySSLConnectionSocketFactory(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
			super(sslContext, hostnameVerifier);
		}

		@Override
		public Socket createSocket(final HttpContext context) throws IOException {
			InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");

			if (socksaddr == null) {
				return new Socket();
			}
			return new Socket(new Proxy(Proxy.Type.SOCKS, socksaddr));
		}
	}

	public class HttpGetRequest {

		private String url;
		
		private RequestConfig requestConfig;
		
		private Header[] headers;
		
		private String queryString;
		
		private HttpClientContext httpContext;
		
		private String charset;
		
		private boolean cookie = false;
		
		public HttpGetRequest(String url) {
			this.url = url;
		}
		
		public String getUrl() {
			return this.url;
		}
		
		/**
		 * 指定 Timeout 時間  單位為毫秒
		 * 
		 * @param connectTimeout 設置連接超時時間，單位毫秒
		 * @param socketTimeout 請求獲取數據的超時時間，單位毫秒。 如果訪問一個接口，多少時間內無法返回數據，就直接放棄此次調用。
		 * @param connectionRequestTimeout 設置從connect Manager獲取Connection 超時時間，單位毫秒。這個屬性是新加的屬性，因為目前版本是可以共享連接池的。
		 * @return
		 */
		public void setTimeout(int socketTimeout, int connectTimeout, int connectionRequestTimeout) {
			this.requestConfig = RequestConfig.copy(ApacheHttpClientManager.getInstance().requestConfig)
					.setSocketTimeout(socketTimeout)
					.setConnectTimeout(connectTimeout)
					.setConnectionRequestTimeout(connectionRequestTimeout).build();
		}

		public void setTimeout(int socketTimeout) {
			this.requestConfig = RequestConfig.copy(ApacheHttpClientManager.getInstance().requestConfig)
					.setSocketTimeout(socketTimeout)
					.setConnectTimeout(5000)
					.setConnectionRequestTimeout(5000).build();
		}

		public RequestConfig getRequestConfig() {
			return requestConfig;
		}
		
		public void setHeaders(Header[] headers) {
			this.headers = headers;
		}
		
		public void setHeaders(Collection<Header> headers) {
			this.headers = headers.toArray(new Header[headers.size()]);
		}
		
		public void setParameters(Map<String, String> parameters) throws Exception {
			setParameters(parameters, "UTF-8");
		}
		
		public void setParameters(Map<String, String> parameters, String charset) throws Exception {
			StringBuilder sb = new StringBuilder();
			for(Entry<String, String> entry : parameters.entrySet()) {
				sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), charset)).append("&");
			}
			if(sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			this.queryString = sb.toString();
			this.charset = charset;
		}

		public Header[] getHeaders() {
			return headers;
		}

		public String getQueryString() {
			return queryString;
		}

		public HttpClientContext getHttpContext() {
			return httpContext;
		}

		public void setHttpContext(HttpClientContext httpContext) {
			this.httpContext = httpContext;
		}

		public String getCharset() {
			return charset;
		}
		
		public boolean receiveCookie() {
			return this.cookie;
		}
		
		public void setReceiveCookie(boolean value) {
			this.cookie = value;
		}
	}

	public class HttpPostRequest {

		private String url;
		
		private RequestConfig requestConfig;
		
		private Header[] headers;
		
		private HttpEntity httpEntity;
		
		private HttpClientContext httpContext;
		
		private String charset;
		
		private boolean cookie = false;
		
		
		public HttpPostRequest(String url) {
			this.url = url;
		}
		
		public String getUrl() {
			return this.url;
		}
		
		/**
		 * 指定 Timeout 時間  單位為毫秒
		 * 
		 * @param connectTimeout 設置連接超時時間，單位毫秒
		 * @param socketTimeout 請求獲取數據的超時時間，單位毫秒。 如果訪問一個接口，多少時間內無法返回數據，就直接放棄此次調用。
		 * @param connectionRequestTimeout 設置從connect Manager獲取Connection 超時時間，單位毫秒。這個屬性是新加的屬性，因為目前版本是可以共享連接池的。
		 * @return
		 */
		public void setTimeout(int socketTimeout, int connectTimeout, int connectionRequestTimeout) {
			this.requestConfig = RequestConfig.copy(ApacheHttpClientManager.getInstance().requestConfig)
					.setSocketTimeout(socketTimeout)
					.setConnectTimeout(connectTimeout)
					.setConnectionRequestTimeout(connectionRequestTimeout).build();
		}

		public void setTimeout(int socketTimeout) {
			this.requestConfig = RequestConfig.copy(ApacheHttpClientManager.getInstance().requestConfig)
					.setSocketTimeout(socketTimeout)
					.setConnectTimeout(5000)
					.setConnectionRequestTimeout(5000).build();
		}

		public RequestConfig getRequestConfig() {
			return requestConfig;
		}
		
		public void setHeaders(Header[] headers) {
			this.headers = headers;
		}
		
		public void setHeaders(Collection<Header> headers) {
			this.headers = headers.toArray(new Header[headers.size()]);
		}
		
		public void setParameters(Map<String, String> parameters) throws Exception {
			setParameters(parameters, "UTF-8");
		}
		
		public void setParameters(Map<String, String> parameters, String charset) throws Exception {
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			for(Entry<String, String> entry : parameters.entrySet()) {
				nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			this.httpEntity = new UrlEncodedFormEntity(nameValuePairs, charset);
			this.charset = charset;
		}

		public Header[] getHeaders() {
			return headers;
		}

		public HttpClientContext getHttpContext() {
			return httpContext;
		}

		public void setHttpContext(HttpClientContext httpContext) {
			this.httpContext = httpContext;
		}

		public String getCharset() {
			return charset;
		}

		public HttpEntity getHttpEntity() {
			return httpEntity;
		}
		
		public void setReceiveCookie(boolean value) {
			this.cookie = value;
		}
		
		public boolean receiveCookie() {
			return this.cookie;
		}
	}
	
	public class HTTPResponse {

		private final int statuscode;

		private final String content;

		private final Header[] header;

		private List<Cookie> cookies;
		
		public HTTPResponse(int statusCode, String content, Header[] header, List<Cookie> cookies) {
			this.statuscode = statusCode;
			this.content = content;
			this.header = header;
			this.cookies = cookies;
		}

		public int getStatuscode() {
			return statuscode;
		}

		public String getContent() {
			return content;
		}

		public String getValueFromHeader(String headName, String elementName) {
			if (header == null) {
				return null;
			}

			for (Header header : header) {
				HeaderElement[] headerElement = header.getElements();
				for (HeaderElement element : headerElement) {
					if (elementName.equals(element.getName())) {
						return element.getValue();
					}
				}
			}
			return "";
		}

		public List<Cookie> getCookies() {
			return cookies;
		}
	}

}


class ThreadFactoryUtils {

	private static class DaemonThreadFactory implements ThreadFactory {

		private String threadName = null;

		DaemonThreadFactory() {
		}

		DaemonThreadFactory(String threadName) {
			this.threadName = threadName;
		}

		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setDaemon(true);
			if (threadName != null && !"".equals(threadName)) {
				thread.setName(threadName);
			}
			return thread;
		}
	}

	public static ThreadFactory getDaemonThreadFactory() {
		return new DaemonThreadFactory();
	}

	public static ThreadFactory getDaemonThreadFactory(String threadName) {
		return new DaemonThreadFactory(threadName);
	}
}


