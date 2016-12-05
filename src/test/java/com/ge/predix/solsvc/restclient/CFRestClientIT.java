package com.ge.predix.solsvc.restclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.solsvc.restclient.config.IOauthRestConfig;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * Use the PredixRestClient to make secure token calls to PredixAsset
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-local-properties-context.xml"
        
})
@SuppressWarnings("nls")
@ActiveProfiles(profiles = "local")
public class CFRestClientIT
{

    private static final Logger log = LoggerFactory.getLogger(CFRestClientIT.class);

    @Autowired
    private RestClient          restClient;

    @Autowired
    @Qualifier("defaultOauthRestConfig")
    private IOauthRestConfig    restConfig;

    @Value("${asset.service.base.url}")
    private String              assetBaseUrl;

    @Value("${predix.asset.zoneid}")
    private String              zoneId;

    /**
     * @throws IOException -
     * 
     */
    @Test
    public void doTest()
            throws IOException
    {   	
        get();
        delete();
        post();
        getOneAsset();
        delete();
    }

    /**
     * @throws IOException -
     */
    @Test
    public void doSecureTest()
            throws IOException
    {
        String url = "https://google.com";
        log.info("Get " + url);

        List<Header> headers = new ArrayList<Header>();

        CloseableHttpResponse response = this.restClient.get(url, headers,
                this.restConfig.getDefaultConnectionTimeout(), this.restConfig.getDefaultSocketTimeout());
        Assert.assertNotNull(response);
        int httpStatus = response.getStatusLine().getStatusCode();
        Assert.assertEquals(200, httpStatus);
        String responseString = this.restClient.getResponse(response);
        response.close();
        log.debug("Response =" + responseString);
        // Assert.doesNotContain(responseString, "Not Authorized");
        Assert.assertNotNull(responseString);
    }

    private void get()
            throws IOException
    {
        String baseUri = getBaseUri();
        String url = baseUri + "/asset/compressor-2015";
        log.info("Get Asset " + url);

        List<Header> headers = this.restClient.getSecureTokenForClientId();
        headers.add(new BasicHeader("Predix-Zone-Id", this.zoneId));

        CloseableHttpResponse response = this.restClient.get(url, headers,
                this.restConfig.getDefaultConnectionTimeout(), this.restConfig.getDefaultSocketTimeout());
        Assert.assertNotNull(response);
        int httpStatus = response.getStatusLine().getStatusCode();
        Assert.assertEquals(200, httpStatus);
        String responseString = this.restClient.getResponse(response);
        response.close();
        log.debug("Response =" + responseString);
        // Assert.doesNotContain(responseString, "Not Authorized");
        Assert.assertNotNull(responseString);
    }

    /**
     * -
     * 
     * @throws IOException -
     */
    private void post()
            throws IOException
    {
        String baseUri = getBaseUri();
        String url = baseUri + "/asset";
        log.info("Create Asset ");
        String reqBody = "[{\"assetId\": \"rmd_user_test_asset\",\"description\": \"Asset created for testing purposes\",\"uri\": \"/asset/rmd_user_test_asset\"}]";
        List<Header> headers = this.restClient.getSecureTokenForClientId();
        headers.add(new BasicHeader("Content-Type", "application/json"));
        headers.add(new BasicHeader("Predix-Zone-Id", this.zoneId));
        CloseableHttpResponse response = this.restClient.post(url, reqBody, headers,
                this.restConfig.getDefaultConnectionTimeout(), this.restConfig.getDefaultSocketTimeout());
        int httpStatus = response.getStatusLine().getStatusCode();
        Assert.assertTrue("httpStatus=" + httpStatus + " not equal to 201 or 204",
                httpStatus == 201 || httpStatus == 204);
        Assert.assertNotNull(response);
        assert (httpStatus == 204);

        response.close();
    }

    /**
     * -
     */
    private void getOneAsset()
    {
        String baseUri = getBaseUri();
        String url = baseUri + "/asset/rmd_user_test_asset";
        log.info("Get One Asset " + url);
        log.info("Get One Asset zone=" + this.zoneId);
        log.info("Get One Asset oauthClientId=" + this.restConfig.getOauthClientId());
        log.info("Get One Asset getOauthClientIdEncode=" + this.restConfig.getOauthClientIdEncode());

        List<Header> headers = this.restClient.getSecureTokenForClientId();
        headers.add(new BasicHeader("Predix-Zone-Id", this.zoneId));
        @SuppressWarnings("resource")
        HttpResponse response = this.restClient.get(url, headers, this.restConfig.getDefaultConnectionTimeout(),
                this.restConfig.getDefaultSocketTimeout());
        Assert.assertNotNull(response);
        int httpStatus = response.getStatusLine().getStatusCode();
        String responseString = this.restClient.getResponse(response);
        log.info("url=" + url);
        log.info("httpStatus=" + httpStatus);
        log.info("responseString=" + responseString);
        assert (httpStatus == 200);
        // Assert.doesNotContain(responseString, "Not Authorized");
        Assert.assertNotNull(responseString);
    }

    /**
     * -
     */
    private void delete()
    {
        String baseUri = getBaseUri();
        String url = baseUri + "/asset/rmd_user_test_asset";
        log.info("Delete Asset " + url);
        List<Header> headers = this.restClient.getSecureTokenForClientId();
        headers.add(new BasicHeader("Content-Type", "application/json"));
        headers.add(new BasicHeader("Predix-Zone-Id", this.zoneId));
        @SuppressWarnings("resource")
        HttpResponse response = this.restClient.delete(url, headers, this.restConfig.getDefaultConnectionTimeout(),
                this.restConfig.getDefaultSocketTimeout());
        int httpStatus = response.getStatusLine().getStatusCode();
        assert (httpStatus == 204);
        Assert.assertNotNull(response);
    }

    private String getBaseUri()
    {
        return this.assetBaseUrl;
    }
}
