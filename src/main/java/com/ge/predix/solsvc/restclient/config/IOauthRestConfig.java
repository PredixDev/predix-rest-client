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
     * @return String
     */
    public String getOauthResource();
    
    /**
     * @return String
     */
    public String getOauthRestHost();
    
    /**
     * @return String
     */
    public String getOauthRestPort();
   
    /**
     * @return String
     */
    public String getOauthCertLocation();
   
    /**
     * @return String
     */
    public String getOauthCertPassword();
   
    /**
     * @return String
     */
    public String getOauthClientId();
    
    /**
     * @return String
     */
    public boolean getOauthClientIdEncode();
   
    /**
     * @return String
     */
    public String getOauthProxyHost();
 

    /**
     * @return String
     */
    public String getOauthProxyPort();
  

    /**
     * @return String
     */
    public String getOauthTokenType();
  
    /**
     * @return String
     */ 
    public String getOauthUserName();
   

    /**
     * @return String
     */
    public String getOauthUserPassword();
   

    /**
     * @return String
     */
    public boolean isOauthEncodeUserPassword();
   

    /**
     * @return String
     */
	public int getOauthSocketTimeout() ;

	 /**
     * @return String
     */
	public int getOauthConnectionTimeout() ;

	 /**
     * @return String 
     */
	public String getOauthResourceProtocol();

	 /**
	 * @param oauthResourceProtocol  -
     */
	public void setOauthResourceProtocol(String oauthResourceProtocol) ;
	
	/**
	 * @return -
	 */
	public String getOauthGrantType();
	
	/**
	 * 
	 * @return
	 */
	public int getOauthPoolMaxSize();
	
	/**
	 * 
	 * @return
	 */
	public int getOauthDefaultMaxPerRoute();
	

}
