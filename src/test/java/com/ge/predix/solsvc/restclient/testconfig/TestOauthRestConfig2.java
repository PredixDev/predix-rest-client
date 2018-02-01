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
	@Value("${predix.rest.proxyHost2:#{null}}")
	public void setProxyHost(String proxyHost) {
		super.setProxyHost(proxyHost);
	}

	@Override
	@Value("${predix.rest.proxyPort2:#{null}}")
	public void setProxyPort(String proxyPort) {
		super.setProxyPort(proxyPort);
	}
}
