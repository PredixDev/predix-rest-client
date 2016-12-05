package com.ge.predix.solsvc.restclient.config;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 
 * @author predix
 */
@Component("defaultOauthRestConfig")
@SuppressWarnings({})
public class DefaultOauthRestConfig implements IOauthRestConfig, EnvironmentAware {
	private static Logger log = LoggerFactory.getLogger(DefaultOauthRestConfig.class);

	@Value("${predix.oauth.issuerId.url}")
    private String oauthIssuerId;
    @Value("${predix.oauth.uri:#{null}}")
    private String oauthUri;
    
	
    @Value("${predix.oauth.proxyHost:#{null}}")
    private String oauthProxyHost;
    @Value("${predix.oauth.proxyPort:#{null}}")
    private String oauthProxyPort;

    @Value("${predix.oauth.grantType:client_credentials}")
	private String oauthGrantType;
    @Value("${predix.oauth.tokenType:JWT}")
    private String oauthTokenType;
    @Value("${predix.oauth.clientId:#{null}}")
	private String oauthClientId;
	@Value("${predix.oauth.clientIdEncode:true}")
	private boolean oauthClientIdEncode;


	@Value("${predix.oauth.userName:#{null}}")
	private String oauthUserName;
	@Value("${predix.oauth.userPassword:#{null}}")
	private String oauthUserPassword;
	@Value("${predix.oauth.encodePassword:false}")
	private boolean oauthEncodeUserPassword;
	
	@Value("${predix.oauth.certLocation:file:./certs/authTruststore.jks}")
	private String oauthCertLocation;
	@Value("${predix.oauth.certPassword:#{null}}")
	private String oauthCertPassword;
	@Value("${predix.oauth.connectionTimeout:10000}")
	private int oauthConnectionTimeout; // timeout till connection with server
										// is established
	@Value("${predix.oauth.socketTimeout:10000}")
	private int oauthSocketTimeout; // timeout to receive the data from the
									// server

	// connection pool parameters
	@Value("${predix.rest.poolMaxSize:10}")
	private int poolMaxSize;
	@Value("${predix.rest.poolValidateAfterInactivityTime:10000}")
	private int poolValidateAfterInactivityTime;
	@Value("${predix.rest.poolConnectionRequestTimeout:10000}")
	private int poolConnectionRequestTimeout;
	@Value("${predix.rest.defaultConnectionTimeout:20000}")
	private int defaultConnectionTimeout;
	@Value("${predix.rest.defaultSocketTimeout:25000}")
	private int defaultSocketTimeout;

    /**
     * The name of the VCAP property holding the name of the bound time series endpoint
     */
    public static final String UAA_VCAPS_NAME = "predix_uaa_name";   //$NON-NLS-1$
	/**
	 * -
	 */
	public DefaultOauthRestConfig() {
		super();
	}
	
    /**
     * @return -
     */
    @Override
    public String getOauthIssuerId()
    {
        return this.oauthIssuerId;
    }
   

    /**
     * you may override the setter with an @value annotated property
     * 
     * @param oauthIssuerId -
     */
    public void setOauthIssuerId(String oauthIssuerId)
    {
        
        this.oauthIssuerId = oauthIssuerId;
    }

	/**
	 * @return -
	 */
	@Override
	public String getOauthGrantType() {
		return this.oauthGrantType;
	}

	/**
	 * @return -
	 */
	@Override
	public String getOauthCertLocation() {
		return this.oauthCertLocation;
	}

	/**
	 * @return -
	 */
	@Override
	public String getOauthCertPassword() {
		return this.oauthCertPassword;
	}

	/**
	 * @return -
	 */
	@Override
	public String getOauthClientId() {
		if ( this.oauthClientId != null )
			return this.oauthClientId.trim();
		return this.oauthClientId;
	}

    /**
     * you may override the setter with an @value annotated property
     * 
     * @param oauthClientId -
     */
    public void setOauthClientId(String oauthClientId)
    {
        this.oauthClientId = oauthClientId;
        
    }
	/**
	 * @return -
	 */
	@Override
	public boolean getOauthClientIdEncode() {
		return this.oauthClientIdEncode;
	}

	/**
	 * @return -
	 */
	@Override
	public String getOauthProxyHost() {
		if (this.oauthProxyHost != null)
			return this.oauthProxyHost.trim();
		return this.oauthProxyHost;
	}

	/**
	 * @return -
	 */
	@Override
	public String getOauthProxyPort() {
		if ( this.oauthProxyPort != null )
			return this.oauthProxyPort;
		return this.oauthProxyPort;
	}

	/**
	 * @return -
	 */
	@Override
	public String getOauthTokenType() {
		return this.oauthTokenType;
	}

	/**
	 * @return the userName
	 */
	@Override
	public String getOauthUserName() {
		return this.oauthUserName;
	}

	/**
	 * @return the password
	 */
	@Override
	public String getOauthUserPassword() {
		return this.oauthUserPassword;
	}

	/**
	 * @return the encodePassword
	 */
	@Override
	public boolean isOauthEncodeUserPassword() {
		return this.oauthEncodeUserPassword;
	}

	@Override
	public int getOauthSocketTimeout() {
		return this.oauthSocketTimeout;
	}

	@Override
	public int getOauthConnectionTimeout() {
		return this.oauthConnectionTimeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ge.predix.solsvc.restclient.IRestConfig#printName()
	 */
	@Override
	public String printName() {
		return "RestConfig"; //$NON-NLS-1$
	}

	@Override
	public int getPoolMaxSize() {
		return this.poolMaxSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ge.predix.solsvc.restclient.config.IOauthRestConfig#
	 * getValidateAfterInactivityTime()
	 */
	@Override
	public int getPoolValidateAfterInactivityTime() {
		return this.poolValidateAfterInactivityTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ge.predix.solsvc.restclient.config.IOauthRestConfig#
	 * getOauthConnectionRequestTimeout()
	 */
	@Override
	public int getPoolConnectionRequestTimeout() {
		return this.poolConnectionRequestTimeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ge.predix.solsvc.restclient.config.IOauthRestConfig#
	 * getDefaultConnectionTimeout()
	 */
	@Override
	public int getDefaultConnectionTimeout() {
		return this.defaultConnectionTimeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ge.predix.solsvc.restclient.config.IOauthRestConfig#
	 * getDefaultSocketTimeout()
	 */
	@Override
	public int getDefaultSocketTimeout() {
		return this.defaultSocketTimeout;
	}
	

    /*
     * (non-Javadoc)
     * @see org.springframework.context.EnvironmentAware#setEnvironment(org.
     * springframework.core.env.Environment)
     */
    @SuppressWarnings("nls")
    @Override
    public void setEnvironment(Environment env)
    {
        String vcapPropertyName = null;
        String uaaName = env.getProperty(UAA_VCAPS_NAME); // this is set on the manifest of the application

        vcapPropertyName = null;
        vcapPropertyName = "vcap.services." + uaaName + ".credentials.issuerId";
        if ( !StringUtils.isEmpty(env.getProperty(vcapPropertyName)) ) {
            this.oauthIssuerId = env.getProperty(vcapPropertyName);
        }

      
        vcapPropertyName = "vcap.services." + uaaName + ".credentials.uri";
        if ( !StringUtils.isEmpty(env.getProperty(vcapPropertyName)) )
        {
            this.oauthUri = env.getProperty(vcapPropertyName);
        }
        
        log.info("DefaultOauthRestConfig=" + this.toString());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "DefaultOauthRestConfig [oauthIssuerId=" + this.oauthIssuerId + ", oauthUri=" + this.oauthUri + ", oauthProxyHost="
                + this.oauthProxyHost + ", oauthProxyPort=" + this.oauthProxyPort + ", oauthGrantType=" + this.oauthGrantType
                + ", oauthTokenType=" + this.oauthTokenType + ", oauthClientId=" + this.oauthClientId + ", oauthClientIdEncode="
                + this.oauthClientIdEncode + ", oauthUserName=" + this.oauthUserName + ", oauthUserPassword=" + this.oauthUserPassword
                + ", oauthEncodeUserPassword=" + this.oauthEncodeUserPassword + ", oauthCertLocation=" + this.oauthCertLocation
                + ", oauthCertPassword=" + this.oauthCertPassword + ", oauthConnectionTimeout=" + this.oauthConnectionTimeout
                + ", oauthSocketTimeout=" + this.oauthSocketTimeout + ", poolMaxSize=" + this.poolMaxSize
                + ", poolValidateAfterInactivityTime=" + this.poolValidateAfterInactivityTime
                + ", poolConnectionRequestTimeout=" + this.poolConnectionRequestTimeout + ", defaultConnectionTimeout="
                + this.defaultConnectionTimeout + ", defaultSocketTimeout=" + this.defaultSocketTimeout + "]";
    }


}
