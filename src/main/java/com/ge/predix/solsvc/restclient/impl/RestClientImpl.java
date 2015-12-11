package com.ge.predix.solsvc.restclient.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.SecureRandom;
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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.restclient.config.IOauthRestConfig;

/**
 * 
 * @author predix -
 */
@Component(value = "restClient")
public class RestClientImpl
        implements RestClient, ApplicationContextAware
{

    private static Logger                  log                  = LoggerFactory.getLogger(RestClientImpl.class);

    /**
     * 
     */
    @Autowired
    protected IOauthRestConfig             restConfig;

    private javax.net.ssl.SSLSocketFactory sslSocketFactory;

    private SSLContext                     sslContext;

    private ApplicationContext             applicationContext;

    static private final ObjectMapper      mapper               = new ObjectMapper()
                                                                        .configure(
                                                                                DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
                                                                                false).setSerializationInclusion(
                                                                                JsonSerialize.Inclusion.NON_NULL);
    /**
     * 
     */
    static final String                    DEFAULT_CONTENT_TYPE = "application/json";                             //$NON-NLS-1$
    
  
    private PoolingHttpClientConnectionManager poolManager;

    /**
     * 
     */
    public RestClientImpl()
    {
        super();
    }

    @PostConstruct
    private void init()
    {
        setupSecureContext(this.restConfig.getOauthCertLocation(), this.restConfig.getOauthCertPassword());
        poolManager = new PoolingHttpClientConnectionManager();
        if(poolManager !=null ) {
        	poolManager.setMaxTotal(this.restConfig.getOauthPoolMaxSize());
        	poolManager.setDefaultMaxPerRoute(this.restConfig.getOauthPoolMaxSize());
        }
        
    }

    @Override
    public List<Header> getSecureTokenForClientId()
    {
        List<Header> headers = new ArrayList<Header>();
        addSecureTokenForHeaders(headers, this.restConfig.getOauthClientId(), this.restConfig.getOauthClientIdEncode());
        return headers;
    }

    @SuppressWarnings("nls")
    @Override
    public List<Header> getSecureToken(String oauthResource, String oauthHost, String oauthPort, String oauthGrantType,
            String proxyHost, String proxyPort, String tokenType, String oauthClientId, boolean oauthClientIdEncode)
    {
        List<Header> headers = new ArrayList<Header>();
        List<Header> tokenHeaders = getOauthHttpHeaders(oauthClientId, oauthClientIdEncode);
        String tokenResponse = requestToken(tokenHeaders, oauthResource, oauthHost, oauthPort,
                oauthGrantType, proxyHost, proxyPort);
        Token token = new Token(tokenType);
        token.update(tokenResponse);
        headers.add(new BasicHeader("Authorization", token.getToken()));

        return headers;
    }

    @Override
    public Token requestTokenForClientId()
    {
        List<Header> tokenHeaders = getOauthHttpHeaders(this.restConfig.getOauthClientId(),
                this.restConfig.getOauthClientIdEncode());
        String tokenResponse = requestToken(tokenHeaders, this.restConfig.getOauthResource(),
                this.restConfig.getOauthRestHost(), this.restConfig.getOauthRestPort(),
                this.restConfig.getOauthGrantType(), this.restConfig.getOauthProxyHost(),
                this.restConfig.getOauthProxyPort(), null, null, false);
        Token token = new Token(this.restConfig.getOauthTokenType());
        token.update(tokenResponse);
        return token;
    }

    @Override
    public Token requestToken(String oauthClientId, boolean oauthClientIdEncode)
    {
        List<Header> tokenHeaders = getOauthHttpHeaders(oauthClientId, oauthClientIdEncode);
        String tokenResponse = requestToken(tokenHeaders, this.restConfig.getOauthResource(),
                this.restConfig.getOauthRestHost(), this.restConfig.getOauthRestPort(),
                this.restConfig.getOauthGrantType(), this.restConfig.getOauthProxyHost(),
                this.restConfig.getOauthProxyPort(), null, null, false);
        Token token = new Token(this.restConfig.getOauthTokenType());
        token.update(tokenResponse);
        return token;
    }

    @SuppressWarnings("nls")
    @Override
    public Token requestToken(String username, String password, boolean encodePassword)
    {
        if ( this.restConfig.getOauthClientId() == null )
            throw new UnsupportedOperationException(
                    "oauthClientId is null when retreiving Token.  Set predix.oauth.clientId in property file.");
        List<Header> tokenHeaders = getOauthHttpHeaders(this.restConfig.getOauthClientId(),
                this.restConfig.getOauthClientIdEncode());
        String tokenResponse = requestToken(tokenHeaders, this.restConfig.getOauthResource(),
                this.restConfig.getOauthRestHost(), this.restConfig.getOauthRestPort(),
                this.restConfig.getOauthGrantType(), this.restConfig.getOauthProxyHost(),
                this.restConfig.getOauthProxyPort(), username, password, encodePassword);
        Token token = new Token(this.restConfig.getOauthTokenType());
        token.update(tokenResponse);
        return token;
    }

    @SuppressWarnings("nls")
    @Override
    public String requestToken(List<Header> headers, String oauthResource, String oauthHost, String oauthPort,
            String oauthGrantType, String proxyHost, String proxyPort)
    {
        log.debug("headers=" + headers);
        log.debug("oauthResource=" + oauthResource);
        log.debug("oauthHost=" + oauthHost);
        log.debug("oauthPort=" + oauthPort);
        log.debug("oauthGrantType=" + oauthGrantType);
        log.debug("proxyHost=" + proxyHost);
        log.debug("proxyPort=" + proxyPort);

        String url = null;
        CloseableHttpClient httpClient = getHttpClient();
        try
        {
            String requestBody = null;
            requestBody = "grant_type=" + oauthGrantType;

            // Spring 4.1.5
            // HttpClientBuilder httpClientBuilder =
            // HttpClientBuilder.create().setRedirectStrategy(
            // new LaxRedirectStrategy());

            setupProxy(proxyHost, proxyPort, httpClient);

            setupPeerToPeerCertificate(httpClient);
            // httpClient = httpClientBuilder.build();
            // HttpEntity<String> requestEntity = new HttpEntity<>(requestBody,
            // headers);

            String oauthResource2 = oauthResource;
            if ( !oauthResource2.startsWith("/") ) oauthResource2 = "/" + oauthResource2;

            if ( oauthPort.equals("80") )
                url = this.restConfig.getOauthResourceProtocol() + "://" + oauthHost + oauthResource2;
            else
                url = this.restConfig.getOauthResourceProtocol() + "://" + oauthHost + ":" + oauthPort + oauthResource2;

            // String token = performPost(url, httpClient, requestBody, headers2);
            String token = performGet(url, httpClient, requestBody, headers);

            log.trace("token=" + token);

            return token;
        }
        catch (ClientProtocolException e)
        {
            throw new RuntimeException("unable to call url=" + url, e);
        }
        catch (IOException e)
        {
            throw new RuntimeException("unable to call url=" + url, e);
        }
        finally
        {
        	printPoolStats();
          	
        }

    }

    private void printPoolStats() {
    	log.debug("poolManager.getTotalStats().getMax()"+poolManager.getTotalStats().getMax());
    	log.debug("poolManager.getTotalStats().getLeased()"+poolManager.getTotalStats().getLeased());
		
		
	}

	@SuppressWarnings("nls")
    private String requestToken(List<Header> headers, String oauthResource, String oauthHost, String oauthPort,
            String oauthGrantType, String proxyHost, String proxyPort, String userName, String password,
            boolean encodePassword)
    {
        log.debug("headers=" + headers);
        log.debug("oauthResource=" + oauthResource);
        log.debug("oauthHost=" + oauthHost);
        log.debug("oauthPort=" + oauthPort);
        log.debug("oauthGrantType=" + oauthGrantType);
        log.debug("proxyHost=" + proxyHost);
        log.debug("proxyPort=" + proxyPort);
        log.debug("userName=" + userName);
        // log.debug("password=" + password);
        log.debug("encodePassword=" + encodePassword);

        String url = null;
        CloseableHttpClient httpClient = getHttpClient();
        try
        {
            String queryParams = null;
            if ( userName == null )
            {
                queryParams = "grant_type=" + oauthGrantType;
            }
            else
            {
                queryParams = "grant_type=" + oauthGrantType + "&username=" + userName + "&password=";

                if ( !encodePassword )
                    queryParams += password;
                else
                    queryParams += new String(Base64.encodeBase64(password.getBytes()));
            }

            // Spring 4.1.5
            // HttpClientBuilder httpClientBuilder =
            // HttpClientBuilder.create().setRedirectStrategy(
            // new LaxRedirectStrategy());

            setupProxy(proxyHost, proxyPort, httpClient);

            setupPeerToPeerCertificate(httpClient);
            // httpClient = httpClientBuilder.build();
            // HttpEntity<String> requestEntity = new HttpEntity<>(requestBody,
            // headers);

            String oauthResource2 = oauthResource;
            if ( !oauthResource2.startsWith("/") ) oauthResource2 = "/" + oauthResource2;

            if ( oauthPort.equals("80") )
                url = this.restConfig.getOauthResourceProtocol() + "://" + oauthHost + oauthResource2;
            else
                url = this.restConfig.getOauthResourceProtocol() + "://" + oauthHost + ":" + oauthPort + oauthResource2;

            // String token = performPost(url, httpClient, requestBody, headers2);
            log.debug("tokenUrl=" + url + " queryParams=" + queryParams);
            String token = performGet(url, httpClient, queryParams, headers);

            log.trace("token=" + token);

            return token;
        }
        catch (ClientProtocolException e)
        {
            throw new RuntimeException("unable to call url=" + url, e);
        }
        catch (IOException e)
        {
            throw new RuntimeException("unable to call url=" + url, e);
        }
           finally
       {
        	   printPoolStats();
        }

    }

    @Override
    public List<Header> getRequestHeaders(MessageContext context)
    {
        List<Header> headers = new ArrayList<Header>();
        for (String key : context.getHttpHeaders().getRequestHeaders().keySet())
        {
            headers.add(new BasicHeader(key, context.getHttpHeaders().getHeaderString(key)));
        }
        return headers;
    }

    @Override
    public List<Header> getRequestHeadersToKeep(MessageContext context, List<String> headersToKeep)
    {
        List<Header> headers = new ArrayList<Header>();
        for (String key : context.getHttpHeaders().getRequestHeaders().keySet())
        {
            if ( headersToKeep.contains(key) )
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
     * @throws ClientProtocolException -
     */
    private String performGet(String url, CloseableHttpClient httpClient, String queryParams, List<Header> headers)
            throws UnsupportedEncodingException, IOException, ClientProtocolException
    {
        String url2 = url;
        url2 += "?" + queryParams;
        HttpGet method = new HttpGet(url2);
        method.setHeaders(headers.toArray(new Header[headers.size()]));
        CloseableHttpResponse httpResponse = httpClient.execute(method);
        if ( httpResponse.getStatusLine().getStatusCode() != 200 )
        {
            throw new RuntimeException("unable able to connect to the UAA url=" + url2 + " response=" + httpResponse);
        }
        HttpEntity responseEntity = httpResponse.getEntity();
        httpResponse.close();
        String token = EntityUtils.toString(responseEntity);
        return token;
    }

    /**
     * @param url
     * @param httpClient
     * @param requestBody
     * @param headers2
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws ClientProtocolException -
     */
    @SuppressWarnings("unused")
    private String performPost(String url, CloseableHttpClient httpClient, String requestBody, List<Header> headers2)
            throws UnsupportedEncodingException, IOException, ClientProtocolException
    {
        HttpPost method = new HttpPost(url);
        org.apache.http.HttpEntity entity = new StringEntity(requestBody);
        method.setEntity(entity);
        method.setHeaders(headers2.toArray(new Header[headers2.size()]));
        HttpResponse httpResponse = httpClient.execute(method);
        if ( httpResponse.getStatusLine().getStatusCode() != 200 )
        {
            throw new RuntimeException("unable able to connect to the UAA url=" + url + " response=" + httpResponse);
        }
        HttpEntity responseEntity = httpResponse.getEntity();
        String token = EntityUtils.toString(responseEntity);
        return token;
    }

    /**
     * @param proxyHost
     * @param proxyPort
     * @param httpClient
     */
    @SuppressWarnings("nls")
    private void setupProxy(String proxyHost, String proxyPort, CloseableHttpClient httpClient)
    {
        if ( proxyHost != null && !proxyHost.equals("") )
        {
            log.debug("setupProxy proxyHost=" + proxyHost + " proxyPort=" + proxyPort);
            HttpHost proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort));
            // Spring 4.1.5
            // httpClientBuilder = httpClientBuilder.setProxy(proxy);
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
    }

    /**
     * @param httpClient
     */
    private void setupPeerToPeerCertificate(CloseableHttpClient httpClient)
    {
        if ( this.sslSocketFactory != null )
        {

            ClientConnectionManager connectionManager = httpClient.getConnectionManager();
            SSLSocketFactory factory = new SSLSocketFactory(this.sslContext, new StrictHostnameVerifier());
            connectionManager.getSchemeRegistry().register(new Scheme("https", 443, factory));
            // spring 4.1.5
            // SSLConnectionSocketFactory sslConnectionFactory = new
            // SSLConnectionSocketFactory(this.sslContext,
            // SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            // httpClientBuilder.setSSLSocketFactory(sslConnectionFactory);
        }
    }

    @SuppressWarnings("nls")
    @Override
    public List<Header> getOauthHttpHeaders(String oauthClientId, boolean oauthClientIdEncode)
    {
        log.debug("predix.oauth.clientId=" + oauthClientId.substring(0, oauthClientId.indexOf(":")));

        String auth = oauthClientId;
        byte[] encodedAuth = oauthClientId.getBytes();
        if ( oauthClientIdEncode ) encodedAuth = Base64.encodeBase64(auth.getBytes());
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
    @Override
    public List<Header> getOauthHttpHeaders()
    {
        String auth = this.restConfig.getOauthClientId();
        byte[] encodedAuth = auth.getBytes();
        if ( this.restConfig.getOauthClientIdEncode() ) encodedAuth = Base64.encodeBase64(auth.getBytes());
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
    private void setupSecureContext(String certLocation, String certPassword)
    {
        try
        {
            SSLContext localSSLContext = null;
            if ( certLocation != null && !certLocation.isEmpty() && certPassword != null && !certPassword.isEmpty() )
            {
                // We need to connect using HTTPS.
                KeyStore trustStore = KeyStore.getInstance("JKS");

                // injected certLocation of format:
                // file:certs/authTruststore.jks
                Resource resource = this.applicationContext.getResource(certLocation);
                InputStream trustStream = resource.getURL().openStream();
                trustStore.load(trustStream, certPassword.trim().toCharArray());
                trustStream.close();

                TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory
                        .getDefaultAlgorithm());
                trustFactory.init(trustStore);
                TrustManager[] trustManager = trustFactory.getTrustManagers();

                localSSLContext = SSLContext.getInstance("TLS");
                localSSLContext.init(null, trustManager, new SecureRandom());

                this.sslSocketFactory = localSSLContext.getSocketFactory();

                this.sslContext = localSSLContext;
                SSLContext.setDefault(localSSLContext);
                // HttpsURLConnection.setDefaultSSLSocketFactory(localSSLContext.getSocketFactory());
                // HttpsURLConnection.setDefaultHostnameVerifier(new
                // AllowAllHostnameVerifier());

            }
            // else
            // {
            // localSSLContext = SSLContext.getInstance("SSL");
            // localSSLContext.init(null, new TrustManager[]
            // {
            // new AllCertificatesTrustManager()
            // }, new java.security.SecureRandom());
            // }

        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("Unable to create secure context for certLocation=" + certLocation, ex);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ge.predix.solsvc.restclient.impl.RestClient#get(java.lang.String,
     * org.springframework.http.HttpHeaders)
     */
    @Override
    public CloseableHttpResponse get(String url, List<Header> headers, String oauthClientId, boolean oauthClientIdEncode)
    {
        Token token = requestToken(oauthClientId, oauthClientIdEncode);
        List<Header> localHeaders = addSecureTokenToHeaders(headers, token.getToken());
        return get(url, localHeaders);

    }

    @Override
    public CloseableHttpResponse get(String url, List<Header> headers, String username, String password, boolean encodePassword)
    {
        Token token = requestToken(username, password, encodePassword);
        List<Header> localHeaders = addSecureTokenToHeaders(headers, token.getToken());
        return get(url, localHeaders);

    }

    @SuppressWarnings("nls")
    @Override
    public List<Header> addSecureTokenToHeaders(List<Header> headers, String token)
    {
        List<Header> localHeaders = headers;
        if ( localHeaders == null ) localHeaders = new ArrayList<Header>();
        List<Header> headersToRemove = new ArrayList<Header>();
        for ( Header header : localHeaders) {
            if ( header.getName().equals("Authorization"))
                headersToRemove.add(header);
        }
        localHeaders.removeAll(headersToRemove);
        localHeaders.add(new BasicHeader("Authorization", token));
        return localHeaders;
    }

    @SuppressWarnings("nls")
    @Override
    public List<Header> addZoneToHeaders(List<Header> headers, String value)
    {
        log.debug("add Predix-Zone-Id ToHeaders=" + value);
        List<Header> localHeaders = headers;
        if ( localHeaders == null ) localHeaders = new ArrayList<Header>();
        List<Header> headersToRemove = new ArrayList<Header>();
        for ( Header header : localHeaders) {
            if ( header.getName().equals("Predix-Zone-Id"))
                headersToRemove.add(header);
        }
        localHeaders.removeAll(headersToRemove);
        localHeaders.add(new BasicHeader("Predix-Zone-Id", value));
        return localHeaders;
    }

    @Override
    public List<Header> addSecureTokenForHeaders(List<Header> headers, String userName, String password,
            boolean encodePassword)
    {
        Token token = requestToken(userName, password, encodePassword);
        List<Header> headersOut = addSecureTokenToHeaders(headers, token.getToken());
        return headersOut;
    }

    @Override
    public List<Header> addSecureTokenForHeaders(List<Header> headers, String oauthClientId, boolean oauthClientIdEncode)
    {
        Token token = requestToken(oauthClientId, oauthClientIdEncode);
        List<Header> headersOut = addSecureTokenToHeaders(headers, token.getToken());
        return headersOut;
    }

    /*
     * (non-Javadoc)
     * @see com.ge.predix.solsvc.restclient.impl.RestClient#get(java.lang.String,
     * org.springframework.http.HttpHeaders)
     */
    @SuppressWarnings("nls")
    @Override
    public CloseableHttpResponse get(String url, List<Header> headers)
    {
        try
        {
            HttpGet method = new HttpGet(url);
            method.setHeaders(headers.toArray(new Header[headers.size()]));

            CloseableHttpClient httpClient = getHttpClient();
            setupPeerToPeerCertificate(httpClient);
            CloseableHttpResponse httpResponse = httpClient.execute(method);

            return httpResponse;
        }
        catch (IOException e)
        {
            throw new RuntimeException("unable to call url=" + url + " with headers=" + headers, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ge.predix.solsvc.restclient.impl.RestClient#get(java.lang.String,
     * org.springframework.http.HttpHeaders)
     */
    @Override
    public CloseableHttpResponse post(String url, String request, List<Header> headers, String oauthClientId,
            boolean oauthClientIdEncode)
    {
        Token token = requestToken(oauthClientId, oauthClientIdEncode);
        List<Header> localHeaders = addSecureTokenToHeaders(headers, token.getToken());
        return post(url, request, localHeaders);
    }

    @Override
    public CloseableHttpResponse post(String url, String request, List<Header> headers, String username, String password,
            boolean encodePassword)
    {
        Token token = requestToken(username, password, encodePassword);
        List<Header> localHeaders = addSecureTokenToHeaders(headers, token.getToken());
        return post(url, request, localHeaders);
    }

    /*
     * (non-Javadoc)
     * @see com.ge.predix.solsvc.restclient.impl.RestClient#post(java.lang.String,
     * java.lang.String, org.springframework.http.HttpHeaders)
     */
    @SuppressWarnings("nls")
    @Override
    public CloseableHttpResponse post(String url, String request, List<Header> headers)
    {
        try
        {
            HttpPost method = new HttpPost(url);
            org.apache.http.HttpEntity entity = new StringEntity(request);
            method.setEntity(entity);
            method.setHeaders(headers.toArray(new Header[headers.size()]));
            CloseableHttpClient httpClient = getHttpClient();
            setupPeerToPeerCertificate(httpClient);
            CloseableHttpResponse httpResponse = httpClient.execute(method);
            return httpResponse;
        }
        catch (IOException e)
        {
            throw new RuntimeException("unable to call url=" + url + " with headers=" + headers + " and body="
                    + request, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ge.predix.solsvc.restclient.impl.RestClient#get(java.lang.String,
     * org.springframework.http.HttpHeaders)
     */
    @Override
    public HttpResponse put(String url, String request, List<Header> headers, String oauthClientId,
            boolean oauthClientIdEncode)
    {
        Token token = requestToken(oauthClientId, oauthClientIdEncode);
        List<Header> localHeaders = addSecureTokenToHeaders(headers, token.getToken());
        return put(url, request, localHeaders);
    }

    @Override
    public CloseableHttpResponse put(String url, String request, List<Header> headers, String username, String password,
            boolean encodePassword)
    {
        Token token = requestToken(username, password, encodePassword);
        List<Header> localHeaders = addSecureTokenToHeaders(headers, token.getToken());
        return put(url, request, localHeaders);
    }

    /*
     * (non-Javadoc)
     * @see com.ge.predix.solsvc.restclient.impl.RestClient#put(java.lang.String,
     * java.lang.String, org.springframework.http.HttpHeaders)
     */
    @SuppressWarnings("nls")
    @Override
    public CloseableHttpResponse put(String url, String request, List<Header> headers)
    {
        try
        {
            HttpPut method = new HttpPut(url);
            org.apache.http.HttpEntity entity = new StringEntity(request);
            method.setEntity(entity);
            method.setHeaders(headers.toArray(new Header[headers.size()]));
            CloseableHttpClient httpClient = getHttpClient();
            setupPeerToPeerCertificate(httpClient);
            CloseableHttpResponse httpResponse = httpClient.execute(method);
            return httpResponse;
        }
        catch (IOException e)
        {
            throw new RuntimeException("unable to call url=" + url + " with headers=" + headers, e);
        }
    }

    @Override
    public CloseableHttpResponse delete(String url, List<Header> headers, String username, String password,
            boolean encodePassword)
    {
        Token token = requestToken(username, password, encodePassword);
        List<Header> localHeaders = addSecureTokenToHeaders(headers, token.getToken());
        return delete(url, localHeaders);
    }

    /*
     * (non-Javadoc)
     * @see com.ge.predix.solsvc.restclient.impl.RestClient#delete(java.lang.String,
     * org.springframework.http.HttpHeaders)
     */
    @SuppressWarnings("nls")
    @Override
    public CloseableHttpResponse delete(String url, List<Header> headers)
    {
        try
        {
            HttpDelete method = new HttpDelete(url);
            method.setHeaders(headers.toArray(new Header[headers.size()]));
            CloseableHttpClient httpClient = getHttpClient();
            setupPeerToPeerCertificate(httpClient);
            CloseableHttpResponse httpResponse = httpClient.execute(method);
            return httpResponse;
        }
        catch (IOException e)
        {
            throw new RuntimeException("unable to call url=" + url + " with headers=" + headers, e);
        }
    }

    /**
     * @return
     */
    private CloseableHttpClient getHttpClient()
    {
       
        // HttpConnectionParams.setConnectionTimeout(params, this.restConfig.getConnectionTimeout());
        // if ( this.restConfig.getSocketTimeout() > 0 )
        // HttpConnectionParams.setSoTimeout(params, this.restConfig.getSocketTimeout());
    	//introducing pooling connections :
    	
        HttpClientBuilder builder = HttpClientBuilder.create().setConnectionManager(poolManager);
        RequestConfig.Builder requestBuilder = RequestConfig.custom().setStaleConnectionCheckEnabled(Boolean.TRUE);
   	 	if ( this.restConfig.getOauthConnectionTimeout() > 0 )
        {
	       	requestBuilder = requestBuilder.setConnectTimeout(this.restConfig.getOauthConnectionTimeout());
	        requestBuilder = requestBuilder.setConnectionRequestTimeout(this.restConfig.getOauthConnectionTimeout());
	    }
   	 	
	   	 if ( this.restConfig.getOauthSocketTimeout() > 0 )
	     {
		       	requestBuilder = requestBuilder.setSocketTimeout(this.restConfig.getOauthConnectionTimeout());
	     }
	 
	   	 builder.setDefaultRequestConfig(requestBuilder.build());
   	 
        CloseableHttpClient httpClient = builder.build();      
        return httpClient;
    }

    /**
     * @param object -
     * @return -
     */
    public static <T> String toPrettyJson(T object)
    {
        try
        {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    @SuppressWarnings("nls")
    @Override
    public String getResponse(HttpResponse httpResponse)
    {
        org.apache.http.HttpEntity responseEntity = httpResponse.getEntity();
        String response = null;
        try
        {
            if ( responseEntity != null ) response = EntityUtils.toString(responseEntity);
        }
        catch (ParseException | IOException e)
        {
            throw new RuntimeException("unable to get response=" + httpResponse, e);
        }

        return response;
    }

    /**
     * @param range -
     * @param headers -
     */
    public void setRangeHeaders(String range, List<Header> headers)
    {
        headers.remove(org.apache.http.HttpHeaders.RANGE);
        headers.add(new BasicHeader(org.apache.http.HttpHeaders.RANGE, range));
    }

    @SuppressWarnings("nls")
    @Override
    public boolean hasToken(List<Header> headers)
    {
        if ( headers != null )
        {
            for (Header header : headers)
            {
                if ( header.getName().equals("Authorization") ) return true;
            }
        }
        return false;
    }

    @SuppressWarnings("nls")
    @Override
    public boolean hasZoneId(List<Header> headers)
    {
        if ( headers != null )
        {
            for (Header header : headers)
            {
                if ( header.getName().equals("Predix-Zone-Id") ) return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationContextAware#setApplicationContext
     * (org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException
    {
        this.applicationContext = applicationContext;

    }
    
    

}
