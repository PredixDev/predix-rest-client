package com.ge.predix.solsvc.restclient.impl;

import java.util.List;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;

import com.ge.predix.solsvc.restclient.config.IOauthRestConfig;

/**
 * Works for both Predix on Karaf or Cloud Foundry, just change the Properties as gathered by RestConfig
 * 
 * @author predix
 */
public interface RestClient
{
	/**
	 * @param config - Override the default rest config
	 */
	public void overrideRestConfig(IOauthRestConfig config);

    /**
     * @param url -
     * @param headers -
     * @param connectionTimeout -
     * @param socketTimeout -
     * @return -
     */
    public abstract CloseableHttpResponse get(String url, List<Header> headers);

    /**
     * @param url -
     * @param headers -
     * @param connectionTimeout -
     * @param socketTimeout -
     * @return -
     */
    public abstract CloseableHttpResponse get(String url, List<Header> headers,int connectionTimeout, int socketTimeout);

    /**
     * @param url -
     * @param request -
     * @param headers -
     * @param connectionTimeout -
     * @param socketTimeout -
     * @return -
     */
    public abstract CloseableHttpResponse post(String url, String request, List<Header> headers, int connectionTimeout, int socketTimeout );

    /**
     * @param url -
     * @param request -
     * @param headers -
     * @param connectionTimeout -
     * @param socketTimeout -
     * @return -
     */
	public CloseableHttpResponse post(String url, HttpEntity request, List<Header> headers, int connectionTimeout,
			int socketTimeout);
	
    /**
     * @param url -
     * @param request -
     * @param headers -
     * @return -
     */
	public CloseableHttpResponse post(String url, String request, List<Header> headers);
	
    /**
     * @param url -
     * @param request -
     * @param headers -
     * @param connectionTimeout -
     * @param socketTimeout -
     * @return -
     */
    public abstract CloseableHttpResponse put(String url, String request, List<Header> headers, int connectionTimeout, int socketTimeout);

    /**
     * @param url -
     * @param headers -
     * @param connectionTimeout -
     * @param socketTimeout -
     * @return -
     */
    public abstract CloseableHttpResponse delete(String url, List<Header> headers, int connectionTimeout, int socketTimeout);

    /**
     * @return -
     */
    List<Header> getSecureTokenForClientId();

    /**
     * @param headers -
     * @param token -
     * @return -
     */
    List<Header> addSecureTokenToHeaders(List<Header> headers, String token);

    /**
     * @param headers -
     * @return -
     */
    public List<Header> addSecureTokenForHeaders(List<Header> headers);

    /**
     * @param headers -
     * @param value -
     * @return -
     */
    List<Header> addZoneToHeaders(List<Header> headers, String value);



    /**
     * @param headers -
     * @return -
     */
    boolean hasToken(List<Header> headers);

    /**
     * @param headers -
     * @return -
     */
    boolean hasZoneId(List<Header> headers);

    /**
     * @param httpResponse -
     * @return -
     */
    String getResponse(HttpResponse httpResponse);

    /**
     * @param context -
     * @param headersToKeep -
     * @return -
     */
    List<Header> getRequestHeadersToKeep(MessageContext context, List<String> headersToKeep);


    /**
     * @return -
     */
    List<Header> getOauthHttpHeaders();


    /**
     * @return -
     */
    public IOauthRestConfig getRestConfig();




}
