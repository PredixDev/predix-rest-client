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
     * @param url -
     * @param headers -
     * @param oauthClientId -
     * @param oauthClientIdEncode -
     * @param userName -
     * @param password -
     * @param encodePassword -
     * @return -
     */
    CloseableHttpResponse get(String url, List<Header> headers, String oauthClientId, boolean oauthClientIdEncode);

    /**
     * @param url -
     * @param headers -
     * @param username -
     * @param password -
     * @param encodePassword -
     * @return -
     */
    CloseableHttpResponse get(String url, List<Header> headers, String username, String password, boolean encodePassword);

    /**
     * @param url -
     * @param request -
     * @param headers -
     * @param oauthClientId -
     * @param oauthClientIdEncode -
     * @return -
     */
    CloseableHttpResponse post(String url, String request, List<Header> headers, String oauthClientId,
            boolean oauthClientIdEncode);

    /**
     * @param url -
     * @param request -
     * @param headers -
     * @param userName -
     * @param password -
     * @param encodePassword -
     * @return -
     */
    CloseableHttpResponse post(String url, String request, List<Header> headers, String userName, String password,
            boolean encodePassword);

    /**
     * @param url -
     * @param request -
     * @param headers -
     * @param oauthClientId -
     * @param oauthClientIdEncode -
     * @return -
     */
    HttpResponse put(String url, String request, List<Header> headers, String oauthClientId, boolean oauthClientIdEncode);

    /**
     * @param url -
     * @param request -
     * @param headers -
     * @param userName -
     * @param password -
     * @param encodePassword -
     * @return -
     */
    CloseableHttpResponse put(String url, String request, List<Header> headers, String userName, String password,
            boolean encodePassword);


    /**
     * @param url -
     * @param headers -
     * @param userName -
     * @param password -
     * @param encodePassword -
     * @return -
     */
    CloseableHttpResponse delete(String url, List<Header> headers, String userName, String password, boolean encodePassword);

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
     * @return -
     */
    public List<Header> getRequestHeaders(MessageContext context);

    /**
     * @param context -
     * @param headersToKeep -
     * @return -
     */
    List<Header> getRequestHeadersToKeep(MessageContext context, List<String> headersToKeep);

    /**
     * @param oauthClientId -
     * @param oauthClientIdEncode -
     * @return -
     */
    List<Header> getOauthHttpHeaders(String oauthClientId, boolean oauthClientIdEncode);

    /**
     * @return -
     */
    List<Header> getOauthHttpHeaders();

    /**
     * @return -
     */
    public abstract Token requestTokenForClientId();

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
    public abstract Token requestToken(String oauthClientId, boolean oauthClientIdEncode);

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
     * @param oauthClientId -
     * @param oauthClientIdEncode -  
     * @return -
     */
    List<Header> getSecureToken(String oauthResource, String oauthHost, String oauthPort,
            String oauthGrantType, String proxyHost, String proxyPort, String tokenType, String oauthClientId, boolean oauthClientIdEncode);

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
     * @param headersIn -
     * @param userName -
     * @param password -
     * @param encodePassword -
     * @return -
     */
    List<Header> addSecureTokenForHeaders(List<Header> headersIn, String userName, String password,
            boolean encodePassword);

    /**
     * @param headers -
     * @param oauthClientId -
     * @param oauthClientIdEncode -
     * @return -
     */
    public List<Header> addSecureTokenForHeaders(List<Header> headers, String oauthClientId, boolean oauthClientIdEncode);


}
