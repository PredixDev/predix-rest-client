package com.ge.predix.solsvc.restclient.impl;

import java.io.IOException;
import java.util.List;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.http.Header;

import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * Get the SAML or JWT token needed to make Predix Rest calls on either Karaf or CloudFoundry
 * 
 * @author predix
 */
public interface CxfAwareRestClient
        extends RestClient
{

    /**
     * @param url -
     * @return -
     * @throws IOException -
     */
    MessageContext getMessageContext(String url);

    /**
     * @param url -
     * @param token -
     * @return -
     */
    MessageContext getMessageContextWithToken(String url, String token);

    /**
     * @param url -
     * @return -
     */
    MessageContext getMessageContextWithToken(String url);

    /**
     * @param url -
     * @param headers -
     * @return -
     */
    MessageContext getMessageContext(String url, List<Header> headers);



}
