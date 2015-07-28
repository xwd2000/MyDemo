package com.hikvision.parentdotworry.dataprovider.httpdata.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGetHC4;
import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtilsHC4;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifierHC4;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.DefaultHttpResponseFactoryHC4;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParser;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.LineParser;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.hikvision.parentdotworry.dataprovider.httpdata.HttpDataProvider;
import com.hikvision.parentdotworry.utils.QEncodeUtil;

public class HttpCore {
	private Logger logger = Logger.getLogger(HttpCore.class);
	private static Map<String, HttpCore> instanceMap = new HashMap<String, HttpCore>();

	private static int MAXTOTAL = 100;
	private static int MAXPERROUTE = 10;

	private static int TIMEOUT = 5000;

	private CloseableHttpClient httpclient;
	private RequestConfig requestConfig;
	private CookieStore cookieStore;
	private CredentialsProvider credentialsProvider;

	private String baseUri;
	
	private ResponseHandler<String> responseHandlerDefault = new ResponseHandler<String>() {
		@Override
		public String handleResponse(final HttpResponse response)
				throws ClientProtocolException, IOException {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				if (entity == null) {
					return null;
				}
				String responseBody = EntityUtils.toString(entity);
				logger.debug("----------------------------------------");
				logger.debug(response.getStatusLine());
				if (entity != null) {
					logger.debug("Response content length: "
							+ entity.getContentLength());
				}
				logger.debug("----------------------------------------");

				return responseBody;
				

			} else {
				logger.error("请求返回错误码: " + status);
				throw new ClientProtocolException(
						"Unexpected response status: " + status);
			}
		}
	};

	private HttpCore() {
	}

	public static HttpCore getInstance(String key) {
		if (instanceMap.get(key) == null) {
			synchronized (HttpDataProvider.class) {
				HttpCore ins = instanceMap.get(key);
				if (ins == null) {
					ins = new HttpCore();
					instanceMap.put(key, ins);
					return ins;
				}
			}
		}
		return instanceMap.get(key);

	}

	public void init(String urlBase) {

		String scheme = urlBase.split("://")[0];
		String noSchemeStr = urlBase.split("://")[1];
		int indexSplitor = noSchemeStr.indexOf('/');
		String serviceIpAndPort = noSchemeStr.substring(0, indexSplitor);
		String basePath = noSchemeStr.substring(indexSplitor+1);
		String serverIp;
		int serverPort;
		String[] splitIpPort = serviceIpAndPort.split(":");
		if (splitIpPort.length > 1) {
			serverIp = splitIpPort[0];
			serverPort = Integer.parseInt(splitIpPort[1]);
		} else {
			serverIp = serviceIpAndPort;
			serverPort = 80;
		}

		init(scheme, serverIp, serverPort, basePath);

	}

	public void init(String scheme, String serverIp, int serverPort,
			String basePath) {
		
		this.baseUri = new StringBuilder().append(scheme).append("://")
				.append(serverIp).append(":").append(serverPort).append("/")
				.append(basePath).toString();
		// Use custom message parser / writer to customize the way HTTP
		// messages are parsed from and written out to the data stream.
		// 可以接受错误的头信息，这边没有用到
		HttpMessageParserFactory<HttpResponse> responseParserFactory = new DefaultHttpResponseParserFactory() {
			@Override
			public HttpMessageParser create(SessionInputBuffer buffer,
					MessageConstraints constraints) {
				LineParser lineParser = new BasicLineParser() {

					@Override
					public Header parseHeader(final CharArrayBuffer buffer) {
						try {
							return super.parseHeader(buffer);
						} catch (ParseException ex) {
							return new BasicHeader(buffer.toString(), null);
						}
					}

				};
				return new DefaultHttpResponseParser(buffer, lineParser,
						DefaultHttpResponseFactoryHC4.INSTANCE, constraints) {
					@Override
					protected boolean reject(final CharArrayBuffer line,
							int count) {
						// try to ignore all garbage preceding a status line
						// infinitely
						return false;
					}
				};
			}
		};
		HttpMessageWriterFactory<HttpRequest> requestWriterFactory = new DefaultHttpRequestWriterFactory();

		// Use a custom connection factory to customize the process of
		// initialization of outgoing HTTP connections. Beside standard
		// connection
		// configuration parameters HTTP connection factory can define message
		// parser / writer routines to be employed by individual connections.
		HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(
				requestWriterFactory, responseParserFactory);

		// Client HTTP connection objects when fully initialized can be bound to
		// an arbitrary network socket. The process of network socket
		// initialization,
		// its connection to a remote address and binding to a local one is
		// controlled
		// by a connection socket factory.

		// SSL context for secure connections can be created either based on
		// system or application specific properties.
		SSLContext sslcontext = SSLContexts.createSystemDefault();
		// Use custom hostname verifier to customize SSL hostname verification.
		X509HostnameVerifier hostnameVerifier = new BrowserCompatHostnameVerifierHC4();

		// Create a registry of custom connection socket factories for supported
		// protocol schemes.
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
				.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register(
						"https",
						new SSLConnectionSocketFactory(sslcontext,
								hostnameVerifier)).build();

		// Use custom DNS resolver to override the system DNS resolution.
		/*
		DnsResolver dnsResolver = new SystemDefaultDnsResolver() {

			@Override
			public InetAddress[] resolve(final String host)
					throws UnknownHostException {
				if (host.equalsIgnoreCase("myhost")) {
					return new InetAddress[] { InetAddress
							.getByAddress(new byte[] { 127, 0, 0, 1 }) };
				} else {
					return super.resolve(host);
				}
			}

		};
		 */
		// Create a connection manager with custom configuration.
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry, connFactory/*, dnsResolver*/);

		// Create socket configuration
		SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true)
				.build();
		// Configure the connection manager to use socket configuration either
		// by default or for a specific host.
		connManager.setDefaultSocketConfig(socketConfig);
		connManager.setSocketConfig(new HttpHost(serverIp, serverPort),
				socketConfig);

		// Create message constraints
		MessageConstraints messageConstraints = MessageConstraints.custom()
				.setMaxHeaderCount(200).setMaxLineLength(2000).build();
		// Create connection configuration
		ConnectionConfig connectionConfig = ConnectionConfig.custom()
				.setMalformedInputAction(CodingErrorAction.IGNORE)
				.setUnmappableInputAction(CodingErrorAction.IGNORE)
				.setCharset(Consts.UTF_8)
				.setMessageConstraints(messageConstraints).build();
		// Configure the connection manager to use connection configuration
		// either
		// by default or for a specific host.
		connManager.setDefaultConnectionConfig(connectionConfig);
		connManager.setConnectionConfig(new HttpHost(serverIp, serverPort),
				ConnectionConfig.DEFAULT);

		// Configure total max or per route limits for persistent connections
		// that can be kept in the pool or leased by the connection manager.
		connManager.setMaxTotal(MAXTOTAL);
		connManager.setDefaultMaxPerRoute(MAXPERROUTE);
		connManager.setMaxPerRoute(new HttpRoute(new HttpHost(serverIp,
				serverPort)), 20);

		// Use custom cookie store if necessary.
		cookieStore = new BasicCookieStore();
		// Use custom credentials provider if necessary.
		credentialsProvider = new BasicCredentialsProvider();
		// Create global request configuration
		RequestConfig defaultRequestConfig = RequestConfig
				.custom()
				.setCookieSpec(CookieSpecs.BEST_MATCH)
				.setExpectContinueEnabled(true)
				.setStaleConnectionCheckEnabled(true)
				.setTargetPreferredAuthSchemes(
						Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
				.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
				.build();

		// Create an HttpClient with the given custom dependencies and
		// configuration.
		httpclient = HttpClients.custom().setConnectionManager(connManager)
				.setDefaultCookieStore(cookieStore)
				.setDefaultCredentialsProvider(credentialsProvider)
				// .setProxy(new HttpHost("myproxy", 8080))
				.setDefaultRequestConfig(defaultRequestConfig).build();
		requestConfig = RequestConfig.copy(defaultRequestConfig)
				.setSocketTimeout(TIMEOUT).setConnectTimeout(TIMEOUT)
				.setConnectionRequestTimeout(TIMEOUT)
				// .setProxy(new HttpHost("myotherproxy", 8080))
				.build();
	}

	/**
	 * 用context发送请求
	 * 
	 * @param url
	 * @param param
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private String postMore(String url, HttpEntity param) throws IOException {
		try {
			if (httpclient == null) {
				logger.error("httpclient not inited");
			}
			HttpPostHC4 httpPost = new HttpPostHC4(url);
			httpPost.setConfig(requestConfig);
			httpPost.setEntity(param);

			// Execution context can be customized locally.
			HttpClientContext context = HttpClientContext.create();
			// Contextual attributes set the local context level will take
			// precedence over those set at the client level.
			context.setCookieStore(cookieStore);
			context.setCredentialsProvider(credentialsProvider);

			System.out.println("executing request " + httpPost.getURI());

			ResponseHandler<String> responseHandler = null;
			String responseBody = httpclient
					.execute(httpPost,responseHandler /* ,context */);

			// Once the request has been executed the local context can
			// be used to examine updated state and various objects affected
			// by the request execution.

			// Last executed request
			context.getRequest();
			// Execution route
			context.getHttpRoute();
			// Target auth state
			context.getTargetAuthState();
			// Proxy auth state
			context.getProxyAuthState();
			// Cookie origin
			context.getCookieOrigin();
			// Cookie spec used
			context.getCookieSpec();
			// User security token
			context.getUserToken();

			return responseBody;
		} finally {
			// httpclient.close();
		}
	}

	public String get(String url) throws IOException {
		Args.check(httpclient != null, "httpclient not inited");
		HttpGetHC4 httpGet = new HttpGetHC4(url);
		httpGet.setConfig(requestConfig);
		System.out.println("executing request " + httpGet.getURI());
		String responseBody = httpclient.execute(httpGet,responseHandlerDefault);
		
		return responseBody;
	}

	public String post(String url, HttpEntity param) throws IOException {
		Args.check(httpclient != null, "httpclient not inited");
		HttpPostHC4 httpPost = new HttpPostHC4(url);
		httpPost.setConfig(requestConfig);
		httpPost.setEntity(param);
		System.out.println("executing request " + httpPost.getURI());
		String responseBody = httpclient.execute(httpPost,responseHandlerDefault);
		
		return responseBody;

	}

	public String post(String url, Map<String, Object> params)
			throws IOException {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			nvps.add(new BasicNameValuePair(key, "" + params.get(key)));
		}
		return post(url, new UrlEncodedFormEntity(nvps));
	}

	public String get(String url, Map<String, Object> params)
			throws IOException {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			nvps.add(new BasicNameValuePair(key, "" + params.get(key)));
		}

		String queryString = URLEncodedUtilsHC4.format(nvps, "utf-8");

		// (2) 创建Get实例
		url += ("?" + queryString);
		return get(url);
	}

	public String postByInfName(String intfPath, Map<String, Object> params)
			throws IOException {
		return post(baseUri + intfPath, params);
	}

	public String getByInfName(String intfPath, Map<String, Object> params)
			throws IOException {
		return get(baseUri + intfPath, params);
	}
	
}
