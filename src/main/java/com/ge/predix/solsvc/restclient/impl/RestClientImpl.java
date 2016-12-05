package com.ge.predix.solsvc.restclient.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.solsvc.restclient.config.IOauthRestConfig;

/**
 * GET, PUT, POST, DELETE tailored for Predix.
 * 
 * @author predix -
 */
@SuppressWarnings("deprecation")
@Component(value="restClient")
@Scope("prototype")
public class RestClientImpl implements RestClient, ApplicationContextAware {

	private static Logger log = LoggerFactory.getLogger(RestClientImpl.class);
	

	/**
	 * 
	 */
	@Autowired
	@Qualifier("defaultOauthRestConfig")
	protected IOauthRestConfig restConfig;

	private ApplicationContext applicationContext;

	static private final ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	/**
	 * 
	 */
	static final String DEFAULT_CONTENT_TYPE = "application/json"; //$NON-NLS-1$

	private PoolingHttpClientConnectionManager poolManager;

	/**
	 * 
	 */
	public RestClientImpl() {		
		super();
	}
	
	/**
	 * @param config - Override the default rest config
	 */
	@Override
	public void overrideRestConfig(IOauthRestConfig config){
		this.restConfig= config;
	}
	
	/**
	 * set up the http connection pool
	 */
	@PostConstruct
	private void init() {
		setupSecureContext(this.restConfig.getOauthCertLocation(), this.restConfig.getOauthCertPassword());
		this.poolManager = new PoolingHttpClientConnectionManager();
		if (this.poolManager != null) {
			this.poolManager.setMaxTotal(this.restConfig.getPoolMaxSize());
			this.poolManager.setDefaultMaxPerRoute(this.restConfig.getPoolMaxSize());
			this.poolManager.setValidateAfterInactivity(this.restConfig.getPoolValidateAfterInactivityTime());
		}

	}

	/**
	 * Gets the http client, be sure to close the client connection when you are
	 * done
	 *
	 * @param proxyHost
	 *            the proxy host
	 * @param proxyPort
	 *            the proxy port
	 * @return the http client
	 */
	private CloseableHttpClient getHttpClient(int connectionTimeout, int socketTimeout, String proxyHost,
			String proxyPort) {

		Builder requestBuilder = RequestConfig.custom();
		if (this.restConfig.getPoolConnectionRequestTimeout() > 0) {
			requestBuilder = requestBuilder
					.setConnectionRequestTimeout(this.restConfig.getPoolConnectionRequestTimeout()); // timeout
																										// to
																										// request
																										// from
																										// connection
																										// manager
		}
		if (connectionTimeout > 0) {
			requestBuilder = requestBuilder.setConnectTimeout(connectionTimeout); // timeout
																					// till
																					// connection
																					// with
																					// server
																					// is
																					// established
		}
		if (socketTimeout > 0) {
			requestBuilder = requestBuilder.setSocketTimeout(socketTimeout); // timeout
																				// to
																				// receive
																				// the
																				// data
																				// from
																				// the
																				// server
		}

		HttpClientBuilder builder = HttpClients.custom().setConnectionManager(this.poolManager)
				.setConnectionManagerShared(true).setDefaultRequestConfig(requestBuilder.build());
		if (proxyHost != null && !proxyHost.equals("")) { //$NON-NLS-1$
			HttpHost proxy = new HttpHost(proxyHost, Integer.valueOf(proxyPort));
			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
			builder = builder.setRoutePlanner(routePlanner);
		}

		CloseableHttpClient httpClient = builder.build();

		return httpClient;
	}

	private void printPoolStats() {
		log.trace("this.poolManager.getTotalStats().getMax()" + this.poolManager.getTotalStats().getMax()); //$NON-NLS-1$
		log.trace("this.poolManager.getTotalStats().getLeased()" + this.poolManager.getTotalStats().getLeased()); //$NON-NLS-1$

	}


	@Override
    public List<Header> getSecureTokenForClientId() {
	    List<Header> headers = new ArrayList<Header>();
        addSecureTokenForHeaders(headers);
        return headers;
    }

   @Override
    public List<Header> addSecureTokenForHeaders(List<Header> headers) {
        String tokenResponse = requestToken(this.restConfig);
        Token token = new Token(this.restConfig.getOauthTokenType());
        token.update(tokenResponse);
        List<Header> headersOut = addSecureTokenToHeaders(headers, token.getToken());
        return headersOut;
    }
   
   
   @SuppressWarnings("nls")
   @Override
   public List<Header> addSecureTokenToHeaders(List<Header> headers, String token) {
       List<Header> localHeaders = headers;
       if (localHeaders == null)
           localHeaders = new ArrayList<Header>();
       List<Header> headersToRemove = new ArrayList<Header>();
       for (Header header : localHeaders) {
           if (header.getName().equals("Authorization"))
               headersToRemove.add(header);
       }
       localHeaders.removeAll(headersToRemove);
       localHeaders.add(new BasicHeader("Authorization", token));
       return localHeaders;
   }

	
	@SuppressWarnings({ "nls" })
    private String requestToken(IOauthRestConfig oauthRestConfig) {
        List<Header> getTokenHeaders = getOauthHttpHeaders();
		log.trace("requestToken headers=" + getTokenHeaders);
//		log.debug("oauthResource=" + oauthResource);
//		log.debug("oauthHost=" + oauthHost);
//		log.debug("oauthPort=" + oauthPort);
//		log.debug("oauthGrantType=" + oauthGrantType);
//		log.debug("proxyHost=" + proxyHost);
//		log.debug("proxyPort=" + proxyPort);
//		log.debug("userName=" + userName);
//		// log.debug("password=" + password);
//		log.debug("encodePassword=" + encodePassword);

		String url = null;

		try (CloseableHttpClient httpClient = getHttpClient(oauthRestConfig.getOauthConnectionTimeout(),
		        oauthRestConfig.getOauthSocketTimeout(), oauthRestConfig.getOauthProxyHost(), oauthRestConfig.getOauthProxyPort());) {
			String queryParams = null;
			if (oauthRestConfig.getOauthUserName() == null) {
				queryParams = "grant_type=" + oauthRestConfig.getOauthGrantType();
			} else {
				queryParams = "grant_type=" + oauthRestConfig.getOauthGrantType() + "&username=" + oauthRestConfig.getOauthUserName() + "&password=";

				if (!this.restConfig.isOauthEncodeUserPassword())
					queryParams += oauthRestConfig.getOauthUserPassword();
				else
					queryParams += new String(Base64.encodeBase64(oauthRestConfig.getOauthUserPassword().getBytes()));
			}

			// Spring 4.1.5
			// HttpClientBuilder httpClientBuilder =
			// HttpClientBuilder.create().setRedirectStrategy(
			// new LaxRedirectStrategy());

			// setupProxy(proxyHost, proxyPort, httpClient);

			// httpClient = httpClientBuilder.build();
			// HttpEntity<String> requestEntity = new HttpEntity<>(requestBody,
			// headers);

			url = oauthRestConfig.getOauthIssuerId();

			// String token = performPost(url, httpClient, requestBody,
			// headers2);
			log.trace("tokenUrl=" + url + " queryParams=" + queryParams);
			String token = performGet(url, httpClient, queryParams, getTokenHeaders);

			log.debug("token=" + token);

			return token;
		} catch (ClientProtocolException e) {
			throw new RuntimeException("unable to call url=" + url, e);
		} catch (IOException e) {
			throw new RuntimeException("unable to call url=" + url, e);
		} finally {
			printPoolStats();
		}

	}



	@Override
	public List<Header> getRequestHeadersToKeep(MessageContext context, List<String> headersToKeep) {
		List<Header> headers = new ArrayList<Header>();
		for (String key : context.getHttpHeaders().getRequestHeaders().keySet()) {
			if (headersToKeep.contains(key))
				headers.add(new BasicHeader(key, context.getHttpHeaders().getHeaderString(key)));
		}
		return headers;
	}

	/**
	 * @param url
	 * @param httpClient
	 * @param requestBody
	 * @param headers2
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws ClientProtocolException
	 *             -
	 */
	@SuppressWarnings({ "nls" })
	private String performGet(String url, CloseableHttpClient httpClient, String queryParams, List<Header> headers)
			throws UnsupportedEncodingException, IOException, ClientProtocolException {

		String url2 = url;
		url2 += "?" + queryParams;
		HttpGet method = new HttpGet(url2);
		method.setHeaders(headers.toArray(new Header[headers.size()]));
		String token = null;
		try (CloseableHttpResponse httpResponse = httpClient.execute(method);) {

			if ((httpResponse.getStatusLine().getStatusCode() < 200
					|| httpResponse.getStatusLine().getStatusCode() >= 300)) {
				throw new RuntimeException("unable able to connect to the url=" + url2 + " response=" + httpResponse);
			}
			HttpEntity responseEntity = httpResponse.getEntity();
			token = EntityUtils.toString(responseEntity);
		}
		return token;

	}

	/**
	 * @param url
	 * @param httpClient
	 * @param requestBody
	 * @param headers2
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 *             -
	 */
	@SuppressWarnings({ "unused" })
	private String performPost(String url, CloseableHttpClient httpClient, String requestBody, List<Header> headers2)
			throws ClientProtocolException, IOException {
		HttpPost method = new HttpPost(url);
		org.apache.http.HttpEntity entity = new StringEntity(requestBody);
		method.setEntity(entity);
		method.setHeaders(headers2.toArray(new Header[headers2.size()]));
		try (CloseableHttpResponse httpResponse = httpClient.execute(method)) {
			if (httpResponse.getStatusLine().getStatusCode() < 200
					|| httpResponse.getStatusLine().getStatusCode() >= 300) {
				throw new RuntimeException("unable able to connect to the url=" + url + " response=" + httpResponse); //$NON-NLS-1$//$NON-NLS-2$
			}
			HttpEntity responseEntity = httpResponse.getEntity();
			String token = EntityUtils.toString(responseEntity);
			return token;
		}
	}

	/**
	 * @param proxyHost
	 * @param proxyPort
	 * @param httpClient
	 */
	@SuppressWarnings({ "nls", "unused" })
	private void setupProxy(String proxyHost, String proxyPort, CloseableHttpClient httpClient) {
		if (proxyHost != null && !proxyHost.equals("")) {
			log.debug("setupProxy proxyHost=" + proxyHost + " proxyPort=" + proxyPort);
			HttpHost proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort));
			// Spring 4.1.5
			// httpClientBuilder = httpClientBuilder.setProxy(proxy);
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
	}

	@SuppressWarnings("nls")
	@Override
	public List<Header> getOauthHttpHeaders() {
		String auth = this.restConfig.getOauthClientId();
		byte[] encodedAuth = auth.getBytes();
		if (this.restConfig.getOauthClientIdEncode())
			encodedAuth = Base64.encodeBase64(auth.getBytes());
		String authHeader = "Basic " + new String(encodedAuth);
		List<Header> headers = new ArrayList<Header>();
		headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
		headers.add(new BasicHeader("Authorization", authHeader));
		headers.add(new BasicHeader("Pragma", "no-cache"));
		// String[] realmArray = auth.split(":");
		// headers.add(new BasicHeader("x-tenant", realmArray[0]));
		return headers;
	}

	@SuppressWarnings("nls")
	private void setupSecureContext(String certLocation, String certPassword) {
		String certLocation2 = certLocation;
		try {
			SSLContext localSSLContext = null;
			if (certLocation2 != null && !certLocation2.isEmpty() && certPassword != null && !certPassword.isEmpty()) {
				if (certLocation2.contains("$JAVA_HOME")) {
					certLocation2 = certLocation2.replace("$JAVA_HOME", System.getenv("JAVA_HOME"));
				}

				// We need to connect using HTTPS.
				KeyStore trustStore = KeyStore.getInstance("JKS");

				// injected certLocation of format:
				// file:certs/authTruststore.jks
				Resource resource = this.applicationContext.getResource(certLocation2);
				InputStream trustStream = resource.getURL().openStream();
				trustStore.load(trustStream, certPassword.trim().toCharArray());
				trustStream.close();

				TrustManagerFactory trustFactory = TrustManagerFactory
						.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				trustFactory.init(trustStore);
				TrustManager[] trustManager = trustFactory.getTrustManagers();

				localSSLContext = SSLContext.getInstance("TLS");
				localSSLContext.init(null, trustManager, new SecureRandom());

				// SSLConnectionSocketFactory sslsf = new
				// SSLConnectionSocketFactory(localSSLContext,
				// new String[] { "TLSv1" }, null,
				// SSLConnectionSocketFactory.getDefaultHostnameVerifier());

				SSLContext.setDefault(localSSLContext);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to create secure context for certLocation=" + certLocation, e);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("Unable to create secure context for certLocation=" + certLocation, e);
		} catch (CertificateException e) {
			throw new IllegalArgumentException("Unable to create secure context for certLocation=" + certLocation, e);
		} catch (KeyStoreException e) {
			throw new IllegalArgumentException("Unable to create secure context for certLocation=" + certLocation, e);
		} catch (KeyManagementException e) {
			throw new IllegalArgumentException("Unable to create secure context for certLocation=" + certLocation, e);
		}
	}



	@SuppressWarnings("nls")
	@Override
	public List<Header> addZoneToHeaders(List<Header> headers, String value) {
		log.debug("add Predix-Zone-Id To Headers=" + value);
		List<Header> localHeaders = headers;
		if (localHeaders == null)
			localHeaders = new ArrayList<Header>();
		List<Header> headersToRemove = new ArrayList<Header>();
		for (Header header : localHeaders) {
			if (header.getName().equals("Predix-Zone-Id"))
				headersToRemove.add(header);
		}
		localHeaders.removeAll(headersToRemove);
		localHeaders.add(new BasicHeader("Predix-Zone-Id", value));
		return localHeaders;
	}

    @Override
    public CloseableHttpResponse get(String url, List<Header> headers) {
        return get(url, headers, this.restConfig.getDefaultConnectionTimeout(), this.restConfig.getDefaultSocketTimeout());
    }


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ge.predix.solsvc.restclient.impl.RestClient#get(java.lang.String,
	 * org.springframework.http.HttpHeaders)
	 */
	@SuppressWarnings({ "nls" })
	@Override
	public CloseableHttpResponse get(String url, List<Header> headers, int connectionTimeout, int socketTimeout) {
		
	    try (CloseableHttpClient httpClient = getHttpClient(connectionTimeout, socketTimeout,
				this.restConfig.getOauthProxyHost(), this.restConfig.getOauthProxyPort());) {
		    if ( log.isTraceEnabled()) {
                log.trace("url=" + url);
                log.trace("headers=" + headers);
		        log.trace("connectionTimeout=" + connectionTimeout);
		        log.trace("socketTimeout=" + socketTimeout);
		        log.trace("proxyHost=" + this.restConfig.getOauthProxyHost());
		        log.trace("proxyPort=" + this.restConfig.getOauthProxyPort());
		    }
            HttpGet method = new HttpGet(url);
			method.setHeaders(headers.toArray(new Header[headers.size()]));
			CloseableHttpResponse httpResponse = httpClient.execute(method);
			return httpResponse;
		} catch (IOException e) {
			throw new RuntimeException("unable to call url=" + url + " with headers=" + headers, e);
		}
	}

	@Override
	public CloseableHttpResponse post(String url, String request, List<Header> headers) {
		return post(url, request, headers, this.restConfig.getDefaultConnectionTimeout(), this.restConfig.getDefaultSocketTimeout());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ge.predix.solsvc.restclient.impl.RestClient#post(java.lang.String,
	 * java.lang.String, org.springframework.http.HttpHeaders)
	 */
	@SuppressWarnings({ "nls" })
	@Override
	public CloseableHttpResponse post(String url, String request, List<Header> headers, int connectionTimeout,
			int socketTimeout) {
		try {
			HttpPost method = new HttpPost(url);
			org.apache.http.HttpEntity entity = new StringEntity(request);
			method.setEntity(entity);
			method.setHeaders(headers.toArray(new Header[headers.size()]));
			try (CloseableHttpClient httpClient = getHttpClient(connectionTimeout, socketTimeout,
					this.restConfig.getOauthProxyHost(), this.restConfig.getOauthProxyPort());) {
				CloseableHttpResponse httpResponse = httpClient.execute(method);
				return httpResponse;
			} catch (Exception e) {
				throw new RuntimeException(
						"unable to call url=" + url + " with headers=" + headers + " and body=" + request, e);
			}
		} catch (IOException e) {
			throw new RuntimeException(
					"unable to call url=" + url + " with headers=" + headers + " and body=" + request, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ge.predix.solsvc.restclient.impl.RestClient#post(java.lang.String,
	 * java.lang.String, org.springframework.http.HttpHeaders)
	 */
	@SuppressWarnings({ "nls" })
	@Override
	public CloseableHttpResponse post(String url, HttpEntity entity, List<Header> headers, int connectionTimeout,
			int socketTimeout) {

		try (CloseableHttpClient httpClient = getHttpClient(connectionTimeout, socketTimeout,
				this.restConfig.getOauthProxyHost(), this.restConfig.getOauthProxyPort());) {
			HttpPost method = new HttpPost(url);
			method.setEntity(entity);
			if (headers != null) {
				method.setHeaders(headers.toArray(new Header[headers.size()]));
			}
			CloseableHttpResponse httpResponse = httpClient.execute(method);
			return httpResponse;
		} catch (IOException e) {
			try {
				throw new RuntimeException("unable to call url=" + url + " with headers=" + headers + " and body="
						+ EntityUtils.toString(entity), e);
			} catch (ParseException e1) {
				throw new RuntimeException("Unable to parse the HTTPEntity passed");
			} catch (IOException e1) {
				throw new RuntimeException("Unable to parse the HTTPEntity passed");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ge.predix.solsvc.restclient.impl.RestClient#put(java.lang.String,
	 * java.lang.String, org.springframework.http.HttpHeaders)
	 */
	@SuppressWarnings({ "nls" })
	@Override
	public CloseableHttpResponse put(String url, String request, List<Header> headers, int connectionTimeout,
			int socketTimeout) {

		try (CloseableHttpClient httpClient = getHttpClient(connectionTimeout, socketTimeout,
				this.restConfig.getOauthProxyHost(), this.restConfig.getOauthProxyPort());) {
			HttpPut method = new HttpPut(url);
			org.apache.http.HttpEntity entity = new StringEntity(request);
			method.setEntity(entity);
			method.setHeaders(headers.toArray(new Header[headers.size()]));
			CloseableHttpResponse httpResponse = httpClient.execute(method);
			return httpResponse;
		} catch (IOException e) {
			throw new RuntimeException("unable to call url=" + url + " with headers=" + headers, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ge.predix.solsvc.restclient.impl.RestClient#delete(java.lang.String,
	 * org.springframework.http.HttpHeaders)
	 */
	@SuppressWarnings({ "nls" })
	@Override
	public CloseableHttpResponse delete(String url, List<Header> headers, int connectionTimeout, int socketTimeout) {
		try (CloseableHttpClient httpClient = getHttpClient(connectionTimeout, socketTimeout,
				this.restConfig.getOauthProxyHost(), this.restConfig.getOauthProxyPort());) {
			HttpDelete method = new HttpDelete(url);
			method.setHeaders(headers.toArray(new Header[headers.size()]));

			CloseableHttpResponse httpResponse = httpClient.execute(method);
			return httpResponse;
		} catch (IOException e) {
			throw new RuntimeException("unable to call url=" + url + " with headers=" + headers, e);
		}
	}

	/**
	 * @param object
	 *            -
	 * @param <T>
	 *            -
	 * @return -
	 */
	public static <T> String toPrettyJson(T object) {
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings("nls")
	@Override
	public String getResponse(HttpResponse httpResponse) {
		String response = null;
		try {
			org.apache.http.HttpEntity responseEntity = httpResponse.getEntity();
			if (responseEntity != null)
				response = EntityUtils.toString(responseEntity);
		} catch (ParseException | IOException e) {
			throw new RuntimeException("unable to get response=" + httpResponse, e);
		}
		return response;
	}

	/**
	 * @param range
	 *            -
	 * @param headers
	 *            -
	 */
	public void setRangeHeaders(String range, List<Header> headers) {
		headers.remove(org.apache.http.HttpHeaders.RANGE);
		headers.add(new BasicHeader(org.apache.http.HttpHeaders.RANGE, range));
	}

	@SuppressWarnings("nls")
	@Override
	public boolean hasToken(List<Header> headers) {
		if (headers != null) {
			for (Header header : headers) {
				if (header.getName().equals("Authorization"))
					return true;
			}
		}
		return false;
	}

	@SuppressWarnings("nls")
	@Override
	public boolean hasZoneId(List<Header> headers) {
		if (headers != null) {
			for (Header header : headers) {
				if (header.getName().equals("Predix-Zone-Id"))
					return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext
	 * (org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

    /* (non-Javadoc)
     * @see com.ge.predix.solsvc.restclient.impl.RestClient#getOauthConfig()
     */
    @Override
    public IOauthRestConfig getRestConfig()
    {
        return this.restConfig;
    }

}
