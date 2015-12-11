package com.ge.predix.solsvc.restclient.impl;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.restclient.config.IOauthRestConfig;

/**
 * ability to remember the Token and auto-refresh it before the timer expires
 * 
 * @author 212325745
 */
@Component
@SuppressWarnings("nls")
public class SecurityTokenRefreshService
{
    private static final Logger                       log               = LoggerFactory
                                                                                .getLogger(SecurityTokenRefreshService.class);
    private static final ConcurrentMap<String, Token> tokenMap          = new ConcurrentHashMap<>();
    private static Timer                              tokenRefreshTimer = new Timer();

    @Autowired
    private IOauthRestConfig                               restConfig;

    /**
     * @param restClient -
     * @param tokenType -
     * @param oauthClientId -
     * @param oauthClientIdEncode -
     * @param oauthResource -
     * @param oauthHost -
     * @param oauthPort -
     * @param oauthGrantType -
     * @param proxyHost -
     * @param proxyPort -
     * @param userName -
     * @param password -
     * @param encodePassword -
     * @return token
     */
    public String authKey(RestClient restClient, String tokenType, String oauthClientId, boolean oauthClientIdEncode,
            String oauthResource, String oauthHost, String oauthPort, String oauthGrantType, String proxyHost,
            String proxyPort, String userName, String password, boolean encodePassword)
    {
        Token token = tokenMap.get(userName);
        if ( token != null && !token.isExpired() )
        {
            return token.getToken();
        }

        synchronized (tokenMap)
        {
            token = tokenMap.get(userName);
            if ( token == null )
            {
                token = new Token(tokenType);
                tokenMap.put(userName, token);
            }

            // new token - is expired no token at this time
            if ( token.isExpired() )
            {
                List<Header> headers = restClient.getOauthHttpHeaders(oauthClientId, oauthClientIdEncode);
                String tokenString = restClient.requestToken(headers, oauthResource, oauthHost, oauthPort,
                        oauthGrantType, proxyHost, proxyPort);
                token.update(tokenString);
                // trigger the first refresh - we have token now
                this.triggerRefresh(token, restClient, oauthClientId, oauthClientIdEncode, oauthResource, oauthHost,
                        oauthPort, oauthGrantType, proxyHost, proxyPort, userName, password, encodePassword);
            }
        }
        return token.getToken();
    }

    private void triggerRefresh(Token token, RestClient restClient, String oauthClientId, boolean oauthClientIdEncode,
            String oauthResource, String oauthHost, String oauthPort, String oauthGrantType, String proxyHost,
            String proxyPort, String userName, String password, boolean encodePassword)
    {
        if ( token == null || token.isExpired() )
        {
            return;
        }

        // refreshDelay in milli seconds
        long refreshDelay = token.refreshIn / 2;
        if ( refreshDelay < 1 )
        {
            refreshDelay = 500;
        }
        tokenRefreshTimer.scheduleAtFixedRate(new TokenRefresherTask(restClient, oauthClientId, oauthClientIdEncode,
                oauthResource, oauthHost, oauthPort, oauthGrantType, proxyHost, proxyPort, userName, password,
                encodePassword, this), refreshDelay, refreshDelay);
    }

    // called by refresh timer
    /**
     * @param restClient -
     * @param oauthClientId -
     * @param oauthClientIdEncode -
     * @param oauthResource -
     * @param oauthHost -
     * @param oauthPort -
     * @param oauthGrantType -
     * @param proxyHost -
     * @param proxyPort -
     * @param userName -
     * @param password -
     * @param encodePassword -
     */
    void refreshTokenWorker(RestClient restClient, String oauthClientId, boolean oauthClientIdEncode,
            String oauthResource, String oauthHost, String oauthPort, String oauthGrantType, String proxyHost,
            String proxyPort, String userName, String password, boolean encodePassword)
    {
        Token token = tokenMap.get(userName);
        if ( token == null )
        {
            return;
        }
        synchronized (tokenMap)
        {
            token = tokenMap.get(userName);
            if ( !token.isExpired() )
            {
                log.trace("*** Refreshing SAML token. ******");
                List<Header> headers = restClient.getOauthHttpHeaders(oauthClientId, oauthClientIdEncode);
                String tokenString = restClient.requestToken(headers, oauthResource, oauthHost, oauthPort,
                        oauthGrantType, proxyHost, proxyPort);
                token.update(tokenString);
                log.trace("*** Refreshed SAML token. ******");
            }
            else
            {
                log.trace("*** Requesting new token. ******");
                List<Header> headers = restClient.getOauthHttpHeaders(oauthClientId, oauthClientIdEncode);
                String tokenString = restClient.requestToken(headers, oauthResource, oauthHost, oauthPort,
                        oauthGrantType, proxyHost, proxyPort);
                token.update(tokenString);
                log.trace("*** Requested new token. ******");
            }
        }
    }

    /**
     * @param userName -
     * @return -
     */
    boolean isExpired(String userName)
    {
        Token token = tokenMap.get(userName);
        return !token.isExpired();
    }

    /**
     * 
     * @author predix
     */
    class TokenRefresherTask extends TimerTask
    {
        private RestClient                  restClient;
        private String                      oauthClientId;
        private boolean                     oauthClientIdEncode;
        private String                      oauthResource;
        private String                      oauthGrantType;
        private String                      proxyHost;
        private String                      proxyPort;
        private String                      userName;
        private String                      host;
        private String                      password;
        private boolean                     encodePassword;
        private String                      hostport;
        private SecurityTokenRefreshService securityTokenService = null;

        /**
         * @param restClient -
         * @param oauthClientId -
         * @param oauthClientIdEncode -
         * @param oauthResource -
         * @param oauthGrantType -
         * @param proxyHost -
         * @param proxyPort -
         * @param host -
         * @param port -
         * @param userName -
         * @param password -
         * @param encodePassword -
         * @param sts -
         * @param generator -
         */

        public TokenRefresherTask(RestClient restClient, String oauthClientId, boolean oauthClientIdEncode,
                String oauthResource, String oauthGrantType, String proxyHost, String proxyPort, String host,
                String port, String userName, String password, boolean encodePassword, SecurityTokenRefreshService sts)
        {
            this.restClient = restClient;
            this.oauthClientId = oauthClientId;
            this.oauthClientIdEncode = oauthClientIdEncode;
            this.oauthResource = oauthResource;
            this.proxyHost = proxyHost;
            this.proxyPort = proxyPort;
            this.host = host;
            this.userName = userName;
            this.password = password;
            this.encodePassword = encodePassword;
            this.securityTokenService = sts;
        }

        @Override
        public void run()
        {
            // log.trace("*** Token Refresh triggered. ******");
            this.securityTokenService.refreshTokenWorker(this.restClient, this.oauthClientId, this.oauthClientIdEncode,
                    this.oauthResource, this.oauthGrantType, this.proxyHost, this.proxyPort, this.host, this.hostport,
                    this.userName, this.password, this.encodePassword);
        }
    }

}
