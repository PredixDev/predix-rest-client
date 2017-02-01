package com.ge.predix.solsvc.restclient;

import java.util.List;

import org.apache.http.Header;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.ge.predix.solsvc.restclient.config.IOauthRestConfig;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * Use the PredixRestClient to make secure token calls to the Cloud Foundry UAA security service
 * 
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
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(CFPredixUAAIT.class);

    @Autowired
    private RestClient          restClient;
    
    @Autowired
    @Qualifier("defaultOauthRestConfig")
    private IOauthRestConfig    restConfig;


    /**
     * 
     */
    @Test
    public void getToken()
    {
        List<Header> headers = this.restClient.getSecureTokenForClientId();

        //log.debug("TOKEN = " + tokenString);
        Assert.notNull(headers);
        Assert.isTrue(headers.toString().contains("Bearer"));

    }
}
