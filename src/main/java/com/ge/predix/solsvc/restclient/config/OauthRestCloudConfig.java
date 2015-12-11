package com.ge.predix.solsvc.restclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 
 * @author predix
 */
@Component
@Profile("cloud")
@SuppressWarnings({})
public class OauthRestCloudConfig implements IOauthRestConfig {
	@Value("${predix_oauthResourceProtocol:https}")
	private String oauthResourceProtocol;
	@Value("${predix_oauthResource:/oauth/token}")
	private String oauthResource;
	@Value("${predix_oauthRestHost}")
	private String oauthRestHost;
    @Value("${predix_oauthRestPort:80}")
    private String oauthRestPort;
    @Value("${predix_oauthGrantType:client_credentials}")
    private String oauthGrantType;
	@Value("${predix_proxyHost:#{null}}")
	private String proxyHost;
	@Value("${predix_proxyPort:#{null}}")
	private String proxyPort;
	@Value("${predix_oauthClientId:#{null}}")
	private String oauthClientId;
	@Value("${predix_oauthClientIdEncode:true}")
	private boolean oauthClientIdEncode;
	@Value("${predix_tokenType:JWT}")
	private String tokenType;

	@Value("${predix_userName:#{null}}")
	private String userName;
	@Value("${predix_password:#{null}}")
	private String password;
	@Value("${predix_encodePassword:true}")
	private boolean encodePassword;
	@Value("${predix_certLocation:#{null}}")
	private String certLocation;
	@Value("${predix_certPassword:#{null}}")
	private String certPassword;
	@Value("${predix_socketTimeout:0}")
	private int socketTimeout;
	@Value("${predix_connectionTimeout:0}")
	private int connectionTimeout;
	@Value("${predix_poolMaxSize:10}")
	private int poolMaxSize;
	@Value("${defaultMaxPerRoute:5}")
	private int defaultMaxPerRoute;

	/**
	 * @return -
	 */
	@Override
	public String getOauthResource() {
		return this.oauthResource;
	}

	/**
	 * @return -
	 */
	@Override
	public String getOauthRestHost() {
		return this.oauthRestHost;
	}

	/**
	 * @return -
	 */
	@Override
	public String getOauthRestPort() {
		return this.oauthRestPort;
	}

	/**
     * @return the oauthGrantType
     */
    @Override
    public String getOauthGrantType()
    {
        return this.oauthGrantType;
    }

    /**
	 * @return -
	 */
	@Override
	public String getOauthCertLocation() {
		return this.certLocation;
	}

	/**
	 * @return -
	 */
	@Override
	public String getOauthCertPassword() {
		return this.certPassword;
	}

	/**
	 * @return -
	 */
	@Override
	public String getOauthClientId() {
		return this.oauthClientId;
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
		return this.proxyHost;
	}

	/**
	 * @return -
	 */
	@Override
	public String getOauthProxyPort() {
		return this.proxyPort;
	}

	/**
	 * @return -
	 */
	@Override
	public String getOauthTokenType() {
		return this.tokenType;
	}

	/**
	 * @return the userName
	 */
	@Override
	public String getOauthUserName() {
		return this.userName;
	}

	/**
	 * @return the password
	 */
	@Override
	public String getOauthUserPassword() {
		return this.password;
	}

	/**
	 * @return the encodePassword
	 */
	@Override
	public boolean isOauthEncodeUserPassword() {
		return this.encodePassword;
	}

	@Override
	public int getOauthSocketTimeout() {

		return this.socketTimeout;
	}

	@Override
	public int getOauthConnectionTimeout() {
		return this.connectionTimeout;
	}

	@Override
	public String getOauthResourceProtocol() {
		return this.oauthResourceProtocol;
	}

	@Override
	public void setOauthResourceProtocol(String oauthResourceProtocol) {
		this.oauthResourceProtocol = oauthResourceProtocol;
	}

	/* (non-Javadoc)
	 * @see com.ge.predix.solsvc.restclient.IRestConfig#printName()
	 */
	@Override
	public String printName() {
		
		return "RestCloudConfig"; //$NON-NLS-1$
	}

	@Override
	public int getOauthPoolMaxSize() {
		return poolMaxSize;
	}

	@Override
	public int getOauthDefaultMaxPerRoute() {
		return defaultMaxPerRoute;
	}

}
