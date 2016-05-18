package com.ge.predix.solsvc.restclient;

import java.util.List;

import org.apache.http.Header;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * Use the PredixRestClient to make secure token calls to the Cloud Foundry UAA security service

 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-local-properties-context.xml" 
})
@ActiveProfiles(profiles = "local")
@SuppressWarnings("nls")
public class CFPredixUAAIT
{
    private static final Logger log = LoggerFactory.getLogger(CFPredixUAAIT.class);

    @Autowired
    private RestClient restClient;
    
    @Value("${predix.oauth.clientId}")
    private String oauthClientId;
    
    @Value("${predix.oauth.restHost}")
	private
    String oauthHost;
    
    /**
	 * 
	 */
    @Test
    public void getToken()
    {
        
        String oauthPort = "80";
        String oauthGrantType = "client_credentials";
        String oauthResource = "/oauth/token";
        String proxyHost = null;
        String proxyPort = null;

        List<Header> headers = this.restClient.getOauthHttpHeaders();
        String tokenString = this.restClient.requestToken(headers, oauthResource, getOauthHost(), oauthPort, oauthGrantType, proxyHost,
                proxyPort);

        log.debug("TOKEN = " + tokenString);
        Assert.notNull(tokenString);
        Assert.isTrue(tokenString.contains("access_token"));

    }

	/**
	 * @return the oauthHost
	 */
	public String getOauthHost() {
		return this.oauthHost;
	}

	/**
	 * @param oauthHost the oauthHost to set
	 */
	public void setOauthHost(String oauthHost) {
		this.oauthHost = oauthHost;
	}
}
