package com.ge.predix.solsvc.restclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.solsvc.restclient.config.IOauthRestConfig;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import org.springframework.util.Assert;

/**
 * Use the PredixRestClient to make secure token calls to PredixAsset
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/Test-predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-local-properties-context.xml"
})
@SuppressWarnings("nls")
@ActiveProfiles(profiles = "local")
public class CFRestMultipleClientIT
{

    private static final Logger log = LoggerFactory.getLogger(CFRestMultipleClientIT.class);

    @Autowired
    private RestClient          restClient;
    
    @Autowired
    private RestClient          restClient2;

    @Autowired
    @Qualifier("testOauthRestConfig1")
    private IOauthRestConfig    restConfig1;
    
    @Autowired
    @Qualifier("testOauthRestConfig2")
    private IOauthRestConfig    restConfig2;


    /**
     * @throws IOException -
     * 
     */
    @Test
    public void doTest()
            throws IOException
    {
    	this.restClient.overrideRestConfig(this.restConfig1);
    	this.restClient2.overrideRestConfig(this.restConfig2);
    	get();
    	getUsingRestClient2(); 	
       
    }

  
    private void get()
    {
      
        List<Header> headers = this.restClient.getSecureTokenForClientId();
       

        //log.debug("TOKEN = " + tokenString);
        Assert.notNull(headers);
        Assert.isTrue(headers.toString().contains("Bearer"));
    }
    
    private void getUsingRestClient2()
    {
        
        List<Header> headers = this.restClient2.getSecureTokenForClientId();
        //log.debug("TOKEN = " + tokenString);
        Assert.notNull(headers);
        Assert.isTrue(headers.toString().contains("Bearer"));
       
    }
}
