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

import com.ge.predix.solsvc.restclient.config.RestConfigFactory;
import com.ge.predix.solsvc.restclient.config.IOauthRestConfig;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * Use the PredixRestClient to make secure token calls to the Cloud Foundry UAA
 * security service
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
		"classpath*:META-INF/spring/predix-rest-client-local-properties-context.xml" })
@ActiveProfiles(profiles = "local")
@SuppressWarnings("nls")
public class CFPredixUAAIT  {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(CFPredixUAAIT.class);

	@Autowired
	private RestClient restClient;
	
	//we say 'runtimeConfig' but to keep our test cases tidy we read from the properties
	@Value("${predix.oauth.issuerId.url:#{null}}")
	private String oauthIssuerId;
	@Value("${predix.oauth.clientId:#{null}}")
	private String oauthClientId;

	/**
	 * 
	 */
	@Test
	public void getTokenUsingEnvironmentAndOrProperties() {
		List<Header> headers = this.restClient.getSecureTokenForClientId();

		// log.debug("TOKEN = " + tokenString);
		Assert.notNull(headers);
		Assert.isTrue(headers.toString().contains("Bearer"));

	}

	
	/**
	 * This is used in bulk situations where a microservice is supporting many different Users or ClientIds.s
	 */
	@Test
	public void getTokenUsingRuntimeConfig() {
		boolean encodeClientId = true;
		IOauthRestConfig testConfig = RestConfigFactory.clientCredentials(this.oauthIssuerId, this.oauthClientId, encodeClientId );
		this.restClient.overrideRestConfig(testConfig);
		List<Header> headers = this.restClient.getSecureTokenForClientId();

		// log.debug("TOKEN = " + tokenString);
		Assert.notNull(headers);
		Assert.isTrue(headers.toString().contains("Bearer"));
	}

}
