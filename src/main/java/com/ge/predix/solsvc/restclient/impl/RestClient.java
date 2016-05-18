package com.ge.predix.solsvc.restclient.impl;

import java.util.List;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;

/**
 * Works for both Predix on Karaf or Cloud Foundry, just change the Properties as gathered by RestConfig
 * 
 * @author predix
 */
public interface RestClient
{

    /**
     * @param url -
     * @param headers -
     * @return -
     */
    public abstract CloseableHttpResponse get(String url, List<Header> headers);

    /**
     * @param url -
     * @param request -
     * @param headers -
     * @return -
     */
    public abstract CloseableHttpResponse post(String url, String request, List<Header> headers);

    /**
     * @param url -
     * @param request -
     * @param headers -
     * @return -
     */
    public abstract CloseableHttpResponse put(String url, String request, List<Header> headers);

    /**
     * @param url -
     * @param headers -
     * @return -
     */
    public abstract CloseableHttpResponse delete(String url, List<Header> headers);

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
     * @param headers -
     * @param userName -
     * @param password -
     * @param encodePassword -
     * @return -
     */
    Token requestToken(String userName, String password, boolean encodePassword);

    /**
     * @param headers -
     * @param oauthClientId -
     * @param oauthClientIdEncode -
     * @param userName -
     * @param password -
     * @param encodePassword -
     * @return -
     */
    public abstract Token requestToken();

    /**
     * @param headers -
     * @param oauthResource -
     * @param oauthHost -
     * @param oauthPort -
     * @param oauthGrantType -
     * @param proxyHost -
     * @param proxyPort -
     * @return -
     */
    String requestToken(List<Header> headers, String oauthResource, String oauthHost, String oauthPort,
            String oauthGrantType, String proxyHost, String proxyPort);
    
    /**
     * @param oauthResource -
     * @param oauthHost -
     * @param oauthPort -
     * @param oauthGrantType -
     * @param proxyHost -
     * @param proxyPort -
     * @param tokenType -
     * @param oauthClientId -
     * @param oauthClientIdEncode -  
     * @return -
     */
    List<Header> getSecureToken(String oauthResource, String oauthHost, String oauthPort,
            String oauthGrantType, String proxyHost, String proxyPort, String tokenType);

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
     * @param value -
     * @return -
     */
    List<Header> addZoneToHeaders(List<Header> headers, String value);


    /**
     * @param headers -
     * @param oauthClientId -
     * @param oauthClientIdEncode -
     * @return -
     */
    public List<Header> addSecureTokenForHeaders(List<Header> headers);


}
