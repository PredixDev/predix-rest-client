package com.ge.predix.solsvc.restclient.config;

/**
 * 
 * @author predix
 */

public interface IOauthRestConfig
{
    /**
     * @return String
     */
    public String printName();
    
    /**
     * @return -
     */
    public String getOauthIssuerId();


    /**
     * @return -
     */
    public String getOauthGrantType();

    /**
     * The location of the SSL Certificate Store, e.g. java keystore
     * 
     * @return String
     */
    public String getOauthCertLocation();

    /**
     * The password to the SSL Certificate Store
     * 
     * @return String
     */
    public String getOauthCertPassword();

    /**
     * The UAA ClientId for which to get a token, uses grant_type=client_credentials
     * 
     * @return String
     */
    public String getOauthClientId();

    /**
     * Whether to Base64 encode the clientid:secret, clientId and secret separated by a colon then base64 encoded. This is placed in an HTTP header for Basic
     * Authorization calls to UAA on login redirects
     * 
     * @return String
     */
    public boolean getOauthClientIdEncode();

    /**
     * The hostname of the corporate internet proxy server that monitors traffic from your corporate network to the internet
     * 
     * @return String
     */
    public String getOauthProxyHost();

    /**
     * The hostname of the corporate internet proxy server that monitors traffic from your corporate network to the internet
     * 
     * @return String
     */
    public String getOauthProxyPort();

    /**
     * The type of token returned from the UAA or OAuth server, e.g. JWT
     * 
     * @return String
     */
    public String getOauthTokenType();

    /**
     * The UAA Username for which to get a token, for grant_type=password flows (not recommended, use grant_type=client_credentials instead)
     * 
     * @return String
     */
    public String getOauthUserName();

    /**
     * The UAA user password for which to get a token, for grant_type=password flows (not recommended, use grant_type=client_credentials instead)
     * 
     * @return String
     */
    public String getOauthUserPassword();

    /**
     * Whether the user password should be base64 encoded
     * 
     * @return String
     */
    public boolean isOauthEncodeUserPassword();

    /**
     * How long to wait for a HTTP connection
     * 
     * @return String
     */
    public int getOauthConnectionTimeout();
    
    /**
     * The default for how long to wait for a HTTP connection

     * @return -
     */
    public int getDefaultConnectionTimeout();

    /**
     * How long to wait for an HTTP Response from the UAA or OAuth server
     * 
     * @return String
     */
    public int getOauthSocketTimeout();
    
    /**
     * The default for how long to wait for an HTTP Response from the UAA or OAuth server
     * 
     * @return -
     */
    public int getDefaultSocketTimeout();


    /**
     * The HTTP Client Max Pool Size
     * 
     * @return -
     */
    public int getPoolMaxSize();

    /**
     * When to validate an HTTP Connection
     * 
     * @return -
     */
    public int getPoolValidateAfterInactivityTime();

    /**
     * How long to wait for an HTTP Connection from the pool if the pool is currently empty
     * 
     * @return -
     */
    public int getPoolConnectionRequestTimeout();
    

}
