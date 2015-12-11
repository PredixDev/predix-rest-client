package com.ge.predix.solsvc.restclient.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.bus.CXFBusImpl;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.servlet.ServletDestination;
import org.apache.http.Header;
import org.springframework.stereotype.Component;

/**
 * 
 * @author predix
 */
@Component(value = "cxfAwareRestClient")
public class CxfAwareRestClientImpl extends RestClientImpl
        implements CxfAwareRestClient
{


    @SuppressWarnings("nls")
    @Override
    public MessageContext getMessageContext(String url)
    {

        try
        {
            Message m = new MessageImpl();
            m.put("org.apache.cxf.http.case_insensitive_queries", false);
            m.put("org.apache.cxf.endpoint.private", false);
            m.put(Message.REQUESTOR_ROLE, true);
            @SuppressWarnings("unchecked")
            Map<String, List<String>> headers = (Map<String, List<String>>) m.get(Message.PROTOCOL_HEADERS);
            if ( headers == null )
            {
                headers = new HashMap<String, List<String>>();
                m.put(Message.PROTOCOL_HEADERS, headers);
            }
            headers.put("Content-Type", Collections.singletonList("application/json"));
            // m.put(URITemplate.TEMPLATE_PARAMETERS, "");
            Exchange exchange = new ExchangeImpl();
            m.setExchange(exchange);
            exchange.setInMessage(m);
            exchange.setOutMessage(m);
            EndpointInfo epr = new EndpointInfo();
            epr.setAddress(url);
            ServletDestination d = new ServletDestination(new CXFBusImpl(), null, epr, url);
            exchange.setDestination(d);
            m.put(Message.REQUEST_URI, "http://testcase");
            MessageContext context = new MessageContextImpl(m);

            return context;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("nls")
    @Override
    public MessageContext getMessageContext(String url, List<Header> headers)
    {

        try
        {
            Message m = new MessageImpl();
            m.put("org.apache.cxf.http.case_insensitive_queries", false);
            m.put("org.apache.cxf.endpoint.private", false);
            m.put(Message.REQUESTOR_ROLE, true);
            @SuppressWarnings("unchecked")
            Map<String, List<String>> mHeaders = (Map<String, List<String>>) m.get(Message.PROTOCOL_HEADERS);
            if ( mHeaders == null )
            {
                mHeaders = new HashMap<String, List<String>>();
                m.put(Message.PROTOCOL_HEADERS, headers);
            }
            mHeaders.put("Content-Type", Collections.singletonList("application/json"));
            // m.put(URITemplate.TEMPLATE_PARAMETERS, "");
            Exchange exchange = new ExchangeImpl();
            m.setExchange(exchange);
            exchange.setInMessage(m);
            exchange.setOutMessage(m);
            EndpointInfo epr = new EndpointInfo();
            epr.setAddress(url);
            ServletDestination d = new ServletDestination(new CXFBusImpl(), null, epr, url);
            exchange.setDestination(d);
            m.put(Message.REQUEST_URI, "http://testcase");
            MessageContext context = new MessageContextImpl(m);

            return context;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @SuppressWarnings("nls")
    @Override
    public MessageContext getMessageContextWithToken(String url)
    {
        if ( url == null ) throw new UnsupportedOperationException("url is null when retreiving Token. set ");
        if ( this.restConfig.getOauthUserName() == null )
            throw new UnsupportedOperationException(
                    "userName is null when retreiving Token. Set predix.oauth.userName in property file.");
        if ( this.restConfig.getOauthUserPassword() == null )
            throw new UnsupportedOperationException(
                    "password is null when retreiving Token. Set predix.oauth.userPassword in property file");
        MessageContext context = getMessageContext(url);
        Token token = requestToken(this.restConfig.getOauthUserName(), this.restConfig.getOauthUserPassword(), this.restConfig.isOauthEncodeUserPassword());
        @SuppressWarnings("unchecked")
        Map<String, List<String>> headers = (Map<String, List<String>>) context.get(Message.PROTOCOL_HEADERS);
        headers.put("Authorization", Collections.singletonList(token.getToken()));
        // String[] realm = this.restConfig.getOauthClientId().split(":");
        // headers.put("x-tenant", Collections.singletonList(realm[0]));
        return context;
    }

    @SuppressWarnings("nls")
    @Override
    public MessageContext getMessageContextWithToken(String url, String token)
    {
        MessageContext context = getMessageContext(url);
        @SuppressWarnings("unchecked")
        Map<String, List<String>> headers = (Map<String, List<String>>) context.get(Message.PROTOCOL_HEADERS);
        headers.put("Authorization", Collections.singletonList(token));
        if ( token == null || (token != null && token.equals("")) )
        {
            //temporary workaround in Predix
            if ( this.restConfig.getOauthClientId() == null )
                throw new UnsupportedOperationException(
                        "oauthClientId is null when retreiving Token.  Set predix.oauth.clientId in property file.");

            String[] clientId = this.restConfig.getOauthClientId().split(":");
            headers.put("x-tenant", Collections.singletonList(clientId[0]));
        }
        return context;
    }
}
