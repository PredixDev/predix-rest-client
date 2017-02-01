package com.ge.predix.solsvc.restclient.testconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.restclient.config.DefaultOauthRestConfig;
import com.ge.predix.solsvc.restclient.config.IOauthRestConfig;

/**
 * 
 * @author predix
 */
@Component("testOauthRestConfig2")
@SuppressWarnings({})
public class TestOauthRestConfig2 extends DefaultOauthRestConfig
        implements IOauthRestConfig
{
	/**
	 * @param oauthIssuerId the oauthIssuerId to set
	 */
    @Override
	@Value("${predix.oauth.issuerId.url2}")
	public void setOauthIssuerId(String oauthIssuerId) {
		super.setOauthIssuerId(oauthIssuerId);
	}

	/**
	 * @param oauthClientId the oauthClientId to set
	 */
    @Override
	@Value("${predix.oauth.clientId2:#{null}}")
	public void setOauthClientId(String oauthClientId) {
		super.setOauthClientId(oauthClientId);
	}
    
	@Override
	@Value("${predix.oauth.proxyHost2:#{null}}")
	public void setOauthProxyHost(String oauthProxyHost) {
		super.setOauthProxyHost(oauthProxyHost);
	}

	@Override
	@Value("${predix.oauth.proxyPort2:#{null}}")
	public void setOauthProxyPort(String oauthProxyPort) {
		super.setOauthProxyPort(oauthProxyPort);
	}
}
