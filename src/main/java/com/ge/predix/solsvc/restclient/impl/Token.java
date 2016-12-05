/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.restclient.impl;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * a DTO class to carry around the Token
 * 
 * @author predix
 */
@SuppressWarnings("nls")
public class Token
{
    /**
     * 
     */
    // Note: by default token expires in 3600 seconds (60 minutes)
    static final long          MILLI_SECOND = 1000l;
    /**
     * 
     */
    public static final String SAML         = "SAML";
    /**
     * 
     */
    public final String        JWT          = "JWT";
    /**
     * 
     */
    private String             token;
    private String             tokenType;
    private long               expiry       = 0;
    /**
     * 
     */
    long                       refreshIn    = 0;
    /**
     * 
     */
    String                     refreshTokenId;

    // the request token body with user name

    /**
     * @param tokenType -
     */
    Token(String tokenType)
    {
        this.tokenType = tokenType;
    }

    /**
     * @param response -
     */
    void update(String response)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = (ObjectNode) mapper.readTree(response);
            if ( this.tokenType.equals(SAML) )
            {
                this.token = "SAML " + node.get("tokenKey").asText();
                this.refreshIn = node.get("expiresIn").asLong() * MILLI_SECOND;
                this.expiry = System.currentTimeMillis() + this.refreshIn - 30 * MILLI_SECOND;
                this.refreshTokenId = node.get("refreshToken").asText();
            }
            else if ( this.tokenType.equals(this.JWT) )
            {
                this.token = "Bearer " + node.get("access_token").asText();
                this.refreshIn = node.get("expires_in").asLong() * MILLI_SECOND;
                this.expiry = System.currentTimeMillis() + this.refreshIn - 30 * MILLI_SECOND;
                //this.refreshTokenId = node.get("refresh_token").asText();
            }
            else
                throw new UnsupportedOperationException("tokenType=" + this.tokenType + " not supported");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return -
     */
    boolean isExpired()
    {
        return System.currentTimeMillis() >= this.expiry;
    }

    /**
     * @return the token
     */
    public String getToken()
    {
        return this.token;
    }



}
